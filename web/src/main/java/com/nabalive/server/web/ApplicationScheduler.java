package com.nabalive.server.web;

import com.google.code.morphia.query.Query;
import com.nabalive.application.core.Application;
import com.nabalive.application.core.ApplicationManager;
import com.nabalive.data.core.dao.NabaztagDAO;
import com.nabalive.data.core.model.ApplicationConfig;
import com.nabalive.data.core.model.Nabaztag;
import com.nabalive.server.jabber.ConnectionManager;
import com.nabalive.server.jabber.Status;
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

    private final String CLOCK_APIKEY = "23FE2439-C6E4-4E8D-BE1B-423EA6106CFA";
    private final String MOOD_APIKEY = "BC330670-6D25-4FB7-8613-EFD384D035E1";

    private ApplicationConfig findClockConfig(String apikey, List<ApplicationConfig> applicationConfigList) {
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
            if (connectionManager.containsKey(nabaztag.getMacAddress())) {
                try {
                    application.onStartup(nabaztag, findClockConfig(CLOCK_APIKEY, nabaztag.getApplicationConfigList()));
                } catch (Exception e) {
                    logger.debug("cannot send message", e);
                }
            }
        }
    }

    @Scheduled(fixedDelay = 60000)
    public void mood() {
        logger.debug("mood trigger");

        Application application = checkNotNull(applicationManager.getApplication(MOOD_APIKEY)); // mood

        Query<Nabaztag> query = nabaztagDAO.createQuery().filter("applicationConfigList.applicationStoreApikey", MOOD_APIKEY);
        Iterator<Nabaztag> iterator = nabaztagDAO.find(query).iterator();
        while (iterator.hasNext()) {
            Nabaztag nabaztag = iterator.next();
            if (connectionManager.containsKey(nabaztag.getMacAddress())) {
                if (rand.nextInt(3) == 0){
                    try {
                        application.onStartup(nabaztag, findClockConfig(MOOD_APIKEY, nabaztag.getApplicationConfigList()));
                    } catch (Exception e) {
                        logger.debug("cannot send message", e);
                    }
                }
            }
        }
    }
}
