package com.nabalive.applications.meteo;

import com.google.common.base.Supplier;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.nabalive.application.core.ApplicationBase;
import com.nabalive.common.server.MessageService;
import com.nabalive.data.core.model.ApplicationConfig;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;

@Component("meteo")
public class MeteoApplication extends ApplicationBase {
    private static final String BASE_URL = "http://www.google.com/ig/api";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

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

    private final Cache<String, MeteoResult> meteoCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .concurrencyLevel(4)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build(CacheLoader.from(new Supplier<MeteoResult>() {
                public MeteoResult get() {
                    return null;
                }
            }));


    @Autowired
    private MessageService messageService;

    @Override
    public String getApikey() {
        return "30A17469-B3CB-4049-8941-8E2DBFF1DC08";
    }

    private int getConditionCode(String condition) {
        if (condition.contains("snow")) {
            return 4;
        }
        if (condition.contains("sunny")) {
            return 0;//soleil
        }
        if (condition.contains("cloudy")) {
            return 1;//nuage
        }
        if (condition.contains("rain")) {
            return 3;//pluie
        }
        if (condition.contains("flurries")) {
            return 5;//orage
        }
        if (condition.contains("storm")) {
            return 5;//orage
        }
        if (condition.contains("fog")) {
            return 2;//orage
        }

        return -1;

    }

    private String getCondition(String condition) {
        int code = getConditionCode(condition);
        if (code > -1)
            return "MU http://karotz.s3.amazonaws.com/applications/weather/fr/sky/" + code + ".mp3\nPL 3\nMW\n";
        return "";
    }


    private void sendMeteo(String mac, MeteoResult meteoResult) {
        logger.debug("sending meteo: {}", meteoResult);
        StringBuilder command = new StringBuilder();

        String unit = "C".equals(meteoResult.unit) ? "degree" : "farenheit";

        command.append("MU http://karotz.s3.amazonaws.com/applications/weather/fr/signature.mp3\nPL 3\nMW\n");
        command.append("MU http://karotz.s3.amazonaws.com/applications/weather/fr/today.mp3\nPL 3\nMW\n");
        command.append(getCondition(meteoResult.todayCondition));
        command.append("MU http://karotz.s3.amazonaws.com/applications/weather/fr/temp/" + meteoResult.todayTempHigh + ".mp3\nPL 3\nMW\n");
        command.append("MU http://karotz.s3.amazonaws.com/applications/weather/fr/degree.mp3\nPL 3\nMW\n");
        command.append("MU http://karotz.s3.amazonaws.com/applications/weather/fr/tomorrow.mp3\nPL 3\nMW\n");
        command.append(getCondition(meteoResult.tomorrowCondition));
        command.append("MU http://karotz.s3.amazonaws.com/applications/weather/fr/temp/" + meteoResult.tomorrowTempHigh + ".mp3\nPL 3\nMW\n");
        command.append("MU http://karotz.s3.amazonaws.com/applications/weather/fr/" + unit + ".mp3\nPL 3\nMW\n");

        try {
            messageService.sendMessage(mac, command.toString());
        } catch (ExecutionException e) {
            logger.error("error in meteo", e);
        }
    }

    public String getNodeValue(NodeList nodeList, String nodeName) {
        int length = nodeList.getLength();
        for (int i = 0; i < length; i++) {
            Node item = nodeList.item(i);
            if (nodeName.equals(item.getNodeName())) {
                return item.getAttributes().getNamedItem("data").getNodeValue();
            }
        }
        return null;
    }

    public void httpCall(final String mac, final String city, final String country, final String unit, final String key) throws UnsupportedEncodingException {
        StringBuilder url = new StringBuilder(BASE_URL);
        url.append("?weather=").append(URLEncoder.encode(city + "," + country, "UTF-8"));
        url.append("&hl=fr");

        logger.debug("making httpCall: {}", url);

        try {
            asyncHttpClient.prepareGet(url.toString()).execute(new AsyncCompletionHandler<Response>() {

                @Override
                public Response onCompleted(Response response) throws Exception {
                    DocumentBuilder builder = builderLocal.get();
                    String responseBody = response.getResponseBody("ISO-8859-1");
                    StringReader reader = new StringReader(responseBody);
                    InputSource is = new InputSource(reader);
                    Document document = builder.parse(is);
                    NodeList forecastConditions = document.getElementsByTagName("forecast_conditions");

                    MeteoResult meteoResult = new MeteoResult(
                            getNodeValue(forecastConditions.item(0).getChildNodes(), "low"),
                            getNodeValue(forecastConditions.item(0).getChildNodes(), "high"),
                            getNodeValue(forecastConditions.item(0).getChildNodes(), "icon"),

                            getNodeValue(forecastConditions.item(1).getChildNodes(), "low"),
                            getNodeValue(forecastConditions.item(1).getChildNodes(), "high"),
                            getNodeValue(forecastConditions.item(1).getChildNodes(), "icon"),
                            unit
                    );
                    meteoCache.asMap().putIfAbsent(key, meteoResult);
                    sendMeteo(mac, meteoResult);
                    return response;
                }

                @Override
                public void onThrowable(Throwable t) {
                    logger.error("error in meteo, http received", t);
                }
            });
        } catch (IOException e) {
            logger.error("error in meteo, http call", e);
        }
    }


    @Override
    public void onStartup(String mac, ApplicationConfig applicationConfig) throws Exception {
        String city = checkNotNull(applicationConfig.getParameters().get("city")).get(0);
        String country = checkNotNull(applicationConfig.getParameters().get("country")).get(0);
        String unit;
        if(applicationConfig.getParameters().get("unit") != null)
            unit = applicationConfig.getParameters().get("unit").get(0);
        else
            unit = "C";

        String key = city + "|" + country + "|" + unit;

        MeteoResult meteoResult = meteoCache.asMap().get(key);
        if (meteoResult == null) {
            httpCall(mac, city, country, unit, key);
        } else
            sendMeteo(mac, meteoResult);
    }

    class MeteoResult {
        public final String todayTempLow;
        public final String todayTempHigh;
        public final String todayCondition;
        public final String tomorrowTempLow;
        public final String tomorrowTempHigh;
        public final String tomorrowCondition;

        public final String unit;

        MeteoResult(String todayTempLow,
                    String todayTempHigh,
                    String todayCondition,
                    String tomorrowTempLow,
                    String tomorrowTempHigh,
                    String tomorrowCondition,
                    String unit) {
            this.todayTempLow = todayTempLow;
            this.todayTempHigh = todayTempHigh;
            this.todayCondition = todayCondition;
            this.tomorrowCondition = tomorrowCondition;
            this.tomorrowTempLow = tomorrowTempLow;
            this.tomorrowTempHigh = tomorrowTempHigh;
            this.unit = unit;
        }

        @Override
        public String toString() {
            return "MeteoResult{" +
                    "todayTempLow='" + todayTempLow + '\'' +
                    ", todayTempHigh='" + todayTempHigh + '\'' +
                    ", todayCondition='" + todayCondition + '\'' +
                    ", tomorrowTempLow='" + tomorrowTempLow + '\'' +
                    ", tomorrowTempHigh='" + tomorrowTempHigh + '\'' +
                    ", tomorrowCondition='" + tomorrowCondition + '\'' +
                    ", unit='" + unit + '\'' +
                    '}';
        }
    }
}
