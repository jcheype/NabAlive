package com.nabalive.server.web;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.nabalive.common.server.Event;
import com.nabalive.common.server.EventListener;
import com.nabalive.common.server.MessageService;
import com.nabalive.data.core.dao.NabaztagDAO;
import com.nabalive.data.core.model.Nabaztag;
import com.nabalive.server.jabber.ConnectionManager;
import com.nabalive.server.jabber.packet.PingPacket;
import com.nabalive.server.jabber.packet.SleepPacket;
import com.nabalive.server.web.controller.NabaztagController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 12/13/11
 */

@Component
public class ConnectionWelcome implements EventListener {
    public final static String WELCOME_URL = "http://karotz.s3.amazonaws.com/nab/connection.mp3";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final static Pattern schedulePattern = Pattern.compile("(\\d+):(\\d+)-(\\d)");

    @Autowired
    private ConnectionManager connectionManager;

    @Autowired
    NabaztagController nabaztagController;

    @PostConstruct
    public void init() {
        connectionManager.addEventListener(this);
    }

    @Autowired
    NabaztagDAO nabaztagDAO;

    @Autowired
    MessageService messageService;

    private List<Long> toSec(Set<String> scheduleSet) {
        List<Long> list = Ordering.natural().immutableSortedCopy(Lists.transform(new ArrayList<String>(scheduleSet), new Function<String, Long>() {
            @Override
            public Long apply(@Nullable String s) {
                Matcher matcher = schedulePattern.matcher(s);
                if (matcher.find()) {
                    int h = Integer.parseInt(matcher.group(1));
                    int m = Integer.parseInt(matcher.group(2));
                    int d = Integer.parseInt(matcher.group(3));

                    return new Long(d * 3600L * 24L * h * 3600L * m * 60L);
                }
                return null;
            }
        }));

        return list;
    }

    public boolean checkSleep(Nabaztag nabaztag) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        long secFromWeek = day * 3600L * 24L * hour * 3600L * minute * 60L;

        List<Long> sleepList = toSec(nabaztag.getSleep());
        List<Long> wakeupList = toSec(nabaztag.getWakeup());

        long lastSleep = 0;
        for (long sleep : sleepList) {
            if (sleep < secFromWeek)
                lastSleep = sleep;
        }

        long lastWakeup = 0;
        for (long wakeup : wakeupList) {
            if (wakeup < secFromWeek)
                lastWakeup = wakeup;
        }


        if (lastWakeup == 0 && lastSleep == 0) {
            if (!sleepList.isEmpty())
                lastSleep = sleepList.get(sleepList.size() - 1);
            if (!wakeupList.isEmpty())
                lastWakeup = wakeupList.get(wakeupList.size() - 1);
        }

        logger.debug("lastSleep {}", lastSleep);
        logger.debug("lastWakeup {}", lastWakeup);

        if (lastSleep > lastWakeup) {
            return true;
        }
        return false;
    }

    @Override
    public void onEvent(String sender, Event event) throws Exception {
        logger.debug("event type {}", event.type);
        logger.debug("event type {}", event.content);
        if (event.type == Event.Type.UNBIND_RESOURCE && event.content.contains("<resource>boot</resource></unbind>")) {
            Nabaztag nabaztag = nabaztagDAO.findOne("macAddress", sender);
            logger.debug("WELCOME: unbind {}", nabaztag);

            messageService.sendMessage(sender, new PingPacket(60));

            if (nabaztag == null) {
                logger.debug("WELCOME: SENDING SOUND");
                String command = "CH http://www.nabalive.com/api/chor/rand/5\nMW\n" +
                        "ST " + WELCOME_URL + "\nMW\n";
                messageService.sendMessage(sender, command);
            } else {
                boolean isSleep = checkSleep(nabaztag);
                logger.debug("WELCOME: ISSLEEP :" + isSleep);
                if (isSleep) {
                    logger.debug("WELCOME: SENDING SLEEP");
                    messageService.sendMessage(nabaztag.getMacAddress(), new SleepPacket(SleepPacket.Action.Sleep));
                }
            }
        }
    }
}
