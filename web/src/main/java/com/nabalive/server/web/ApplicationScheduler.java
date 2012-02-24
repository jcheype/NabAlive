package com.nabalive.server.web;

import com.google.code.morphia.query.Query;
import com.nabalive.application.core.Application;
import com.nabalive.application.core.ApplicationManager;
import com.nabalive.common.server.MessageService;
import com.nabalive.common.server.Packet;
import com.nabalive.data.core.dao.NabaztagDAO;
import com.nabalive.data.core.model.ApplicationConfig;
import com.nabalive.data.core.model.Nabaztag;
import com.nabalive.server.jabber.ConnectionManager;
import com.nabalive.server.jabber.Status;
import com.nabalive.server.jabber.packet.SleepPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/23/11
 */
@Component
public class ApplicationScheduler {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final static Random rand = new Random();
    @Autowired
    private ApplicationManager applicationManager;
    @Autowired
    private ConnectionManager connectionManager;
    @Autowired
    private NabaztagDAO nabaztagDAO;

    @Autowired
    private MessageService messageService;

    private final String CLOCK_APIKEY = "23FE2439-C6E4-4E8D-BE1B-423EA6106CFA";
    private final String MOOD_APIKEY = "BC330670-6D25-4FB7-8613-EFD384D035E1";
    private final String TAICHI_APIKEY = "872AC9F0-F513-4980-B4DA-7D57CCB8D20E";

    private ApplicationConfig findConfig(String apikey, List<ApplicationConfig> applicationConfigList) {
        for (ApplicationConfig config : applicationConfigList) {
            if (apikey.equalsIgnoreCase(config.getApplicationStoreApikey())) {
                return config;
            }
        }
        return null;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void clock() {
        logger.debug("clock trigger");

        Application application = checkNotNull(applicationManager.getApplication(CLOCK_APIKEY)); // clock

        Query<Nabaztag> query = nabaztagDAO.createQuery().filter("applicationConfigList.applicationStoreApikey", CLOCK_APIKEY);
        Iterator<Nabaztag> iterator = nabaztagDAO.find(query).iterator();
        while (iterator.hasNext()) {
            Nabaztag nabaztag = iterator.next();
            Status status = connectionManager.get(nabaztag.getMacAddress());
            if (status != null && !status.isAsleep()) {
                try {
                    application.onStartup(nabaztag, findConfig(CLOCK_APIKEY, nabaztag.getApplicationConfigList()));
                } catch (Exception e) {
                    logger.debug("cannot send message", e);
                }
            }
        }
    }

    @Scheduled(fixedDelay = 300000)
    public void mood() {
        logger.debug("mood trigger");

        Application application = checkNotNull(applicationManager.getApplication(MOOD_APIKEY)); // mood

        Query<Nabaztag> query = nabaztagDAO.createQuery().filter("applicationConfigList.applicationStoreApikey", MOOD_APIKEY);
        Iterator<Nabaztag> iterator = nabaztagDAO.find(query).iterator();
        while (iterator.hasNext()) {
            Nabaztag nabaztag = iterator.next();
            Status status = connectionManager.get(nabaztag.getMacAddress());
            if (status != null && status.isIdle()) {
                if (rand.nextInt(4) == 0){
                    try {
                        application.onStartup(nabaztag, findConfig(MOOD_APIKEY, nabaztag.getApplicationConfigList()));
                    } catch (Exception e) {
                        logger.debug("cannot send message", e);
                    }
                }
            }
        }
    }


    @Scheduled(fixedDelay = 400000)
    public void taichi() {
        logger.debug("taichi trigger");

        Application application = checkNotNull(applicationManager.getApplication(TAICHI_APIKEY)); // taichi

        Query<Nabaztag> query = nabaztagDAO.createQuery().filter("applicationConfigList.applicationStoreApikey", TAICHI_APIKEY);
        Iterator<Nabaztag> iterator = nabaztagDAO.find(query).iterator();
        while (iterator.hasNext()) {
            Nabaztag nabaztag = iterator.next();
            Status status = connectionManager.get(nabaztag.getMacAddress());
            if (status != null && status.isIdle()) {
                if (rand.nextInt(4) == 0){
                    try {
                        application.onStartup(nabaztag, findConfig(TAICHI_APIKEY, nabaztag.getApplicationConfigList()));
                    } catch (Exception e) {
                        logger.debug("cannot send message", e);
                    }
                }
            }
        }
    }
    
    private void sendToAll(Query<Nabaztag> query, Packet p){
        Iterator<Nabaztag> iterator = nabaztagDAO.find(query).iterator();
        while (iterator.hasNext()) {
            Nabaztag nabaztag = iterator.next();
            if (connectionManager.containsKey(nabaztag.getMacAddress())) {
                messageService.sendMessage(nabaztag.getMacAddress(), p);
            }
        }
    }

    @Scheduled(cron = "0 * * * * *")
    public void scheduled() {
        logger.debug("scheduled");
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int day = cal.get(Calendar.DAY_OF_WEEK);

        String scheduleKey = String.format("%02d:%02d-%d", hour, minute, day);

        logger.debug("scheduleKey: {}", scheduleKey);

        Query<Nabaztag> queryWakeUp = nabaztagDAO.createQuery().filter("wakeup", scheduleKey);
        sendToAll(queryWakeUp, new SleepPacket(SleepPacket.Action.WakeUp));

        Query<Nabaztag> querySleep = nabaztagDAO.createQuery().filter("sleep", scheduleKey);
        sendToAll(querySleep, new SleepPacket(SleepPacket.Action.Sleep));
    }
}
