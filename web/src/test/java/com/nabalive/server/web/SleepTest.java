package com.nabalive.server.web;

import com.nabalive.data.core.model.Nabaztag;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 12/22/11
 */
public class SleepTest {
    @Test
    public void sleepTest(){
        ConnectionWelcome welcome = new ConnectionWelcome();

        Nabaztag nabaztag = new Nabaztag();
        nabaztag.getSleep().add("14:06-7");
        nabaztag.getSleep().add("08:50-5");
        nabaztag.getSleep().add("10:17-3");


        nabaztag.getWakeup().add("14:03-6");
        nabaztag.getWakeup().add("11:45-6");
        nabaztag.getWakeup().add("10:32-2");
        boolean b = welcome.checkSleep(nabaztag);

        System.out.println("b:" + b);


    }
}
