package com.nabalive.applications.clock;

import com.nabalive.application.core.ApplicationBase;
import com.nabalive.common.server.MessageService;
import com.nabalive.data.core.model.ApplicationConfig;
import com.ning.http.client.AsyncHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;

import static com.google.common.base.Preconditions.checkNotNull;

@Component("clock")
public class ClockApplication extends ApplicationBase {
    private static Random rand = new Random();
    private static final String BASE_URL = "http://www.google.com/ig/api";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    @Autowired
    private MessageService messageService;

    @Override
    public String getApikey() {
        return "23FE2439-C6E4-4E8D-BE1B-423EA6106CFA";
    }

    @Override
    public void onStartup(String mac, ApplicationConfig applicationConfig) throws Exception {
        String tzID = checkNotNull(applicationConfig.getParameters().get("tz").get(0));

        TimeZone timeZone = checkNotNull(TimeZone.getTimeZone(tzID));
        Calendar cal = new GregorianCalendar(timeZone);
        int hour24 = cal.get(Calendar.HOUR_OF_DAY);

        StringBuilder command = new StringBuilder();

        command.append("MU http://karotz.s3.amazonaws.com/applications/clock/fr/signature.mp3\nPL 3\nMW\n");
        command.append("MU http://karotz.s3.amazonaws.com/applications/clock/fr/"+((hour24+1)%24)+"/"+(rand.nextInt(4)+1)+".mp3\nPL 3\nMW\n");

        messageService.sendMessage(mac, command.toString());
    }
}
