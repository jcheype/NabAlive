package com.nabalive.applications.horoscope;

import com.nabalive.application.core.ApplicationBase;
import com.nabalive.common.server.MessageService;
import com.nabalive.data.core.model.ApplicationConfig;
import com.nabalive.data.core.model.Nabaztag;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

@Component("horoscope")
public class HoroscopeApplication extends ApplicationBase {
    private static final String BASE_URL_FR = "http://www.horoscope.fr/rss/horoscope/jour/";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private final Pattern signPattern = Pattern.compile(".*horoscope_(\\w+)\\.html");

    private Map<String, HoroscopeOfDay> horoscopeMap = new HashMap<String, HoroscopeOfDay>();

    private static final ThreadLocal<DocumentBuilder> builderLocal =
            new ThreadLocal<DocumentBuilder>() {
                @Override
                protected DocumentBuilder initialValue() {
                    try {
                        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                        documentBuilderFactory.setNamespaceAware(false);
                        documentBuilderFactory.setValidating(false);
                        documentBuilderFactory.setFeature("http://xml.org/sax/features/namespaces", false);
                        documentBuilderFactory.setFeature("http://xml.org/sax/features/validation", false);
                        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
                        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                        return
                                documentBuilderFactory
                                        .newDocumentBuilder();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };

    @Autowired
    private MessageService messageService;

    @Override
    public String getApikey() {
        return "0C71C95C-9931-4B0E-92F4-5CA5C64297A6";
    }

    private void setHoroscopeOfDay(Nabaztag nabaztag, String tzID, String sign) throws IOException, ExecutionException {
        TimeZone timeZone = checkNotNull(TimeZone.getTimeZone(tzID));
        Calendar cal = new GregorianCalendar(timeZone);
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        HoroscopeOfDay horoscopeOfDay = horoscopeMap.get(tzID);
        if (horoscopeOfDay == null || horoscopeOfDay.dayOfYear != dayOfYear) {
            initHoroscopeOfDay(nabaztag, dayOfYear, tzID, sign);
        } else
            sendHoroscope(nabaztag, horoscopeOfDay, sign);
    }

    private void sendHoroscope(Nabaztag nabaztag, HoroscopeOfDay horoscopeOfDay, String sign) throws ExecutionException, UnsupportedEncodingException {
        String msg = horoscopeOfDay.horoscopeMap.get(sign);
        if(msg == null)
            msg = "Pas d'horoscope trouvé";

        String text = URLEncoder.encode(msg, "UTF-8");
        StringBuilder command = new StringBuilder();
        command.append("MU http://www.nabalive.com/tts/"+nabaztag.getApikey()+"/fr?text="+text+"\nMW\n");


        messageService.sendMessage(nabaztag.getMacAddress(), command.toString());
    }

    private void initHoroscopeOfDay(final Nabaztag nabaztag, final int dayOfYear, final String tzID, final String sign) throws IOException {
        asyncHttpClient.prepareGet(BASE_URL_FR).execute(new AsyncCompletionHandler() {
            @Override
            public Object onCompleted(Response response) throws Exception {
                DocumentBuilder builder = builderLocal.get();
                String responseBody = response.getResponseBody("ISO-8859-1");
                StringReader reader = new StringReader(responseBody);
                InputSource is = new InputSource(reader);
                Document document = builder.parse(is);
                NodeList items = document.getElementsByTagName("item");
                int length = items.getLength();

                HoroscopeOfDay horoscopeOfDay = new HoroscopeOfDay(dayOfYear, new HashMap<String, String>());

                for (int i = 0; i < length; i++) {
                    NodeList childNodes = items.item(i).getChildNodes();
                    int childNodesLength = childNodes.getLength();

                    String sign = null;
                    String description = null;
                    for (int j = 0; j < childNodesLength; j++) {
                        String nodeName = childNodes.item(j).getNodeName();
                        if (nodeName.equals("link")) {
                            sign = parseLink(childNodes.item(j));
                        }
                        if (nodeName.equals("description")) {
                            description = childNodes.item(j).getFirstChild().getTextContent();
                        }
                    }
                    if (sign != null && description != null) {
                        horoscopeOfDay.horoscopeMap.put(sign, description);
                    }

                }
                if (!horoscopeOfDay.horoscopeMap.isEmpty())
                    horoscopeMap.put(tzID, horoscopeOfDay);
                sendHoroscope(nabaztag, horoscopeOfDay, sign);
                return response;
            }
        });
    }

    private String parseLink(Node node) {
        String content = node.getFirstChild().getTextContent();
        Matcher matcher = signPattern.matcher(content);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }


    @Override
    public void onStartup(Nabaztag nabaztag, ApplicationConfig applicationConfig) throws Exception {
        String sign = checkNotNull(applicationConfig.getParameters().get("sign")).get(0);
        String tz = "paris";
        setHoroscopeOfDay(nabaztag, tz, sign);
    }

    class HoroscopeOfDay {
        public final int dayOfYear;
        public final Map<String, String> horoscopeMap;

        public HoroscopeOfDay(int dayOfYear, Map<String, String> horoscopeMap) {
            this.dayOfYear = dayOfYear;
            this.horoscopeMap = horoscopeMap;
        }
    }
}
