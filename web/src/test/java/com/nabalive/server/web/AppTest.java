package com.nabalive.server.web;

import com.nabalive.data.core.model.Nabaztag;
import com.nabalive.server.web.controller.NabaztagController;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest {
    @Test
    public void utcConversionTest(){
        List<String> sleep = new ArrayList<String>();
        sleep.add("04:05-1");

        List<String> strings = Nabaztag.changeTz(sleep, "Europe/Paris", "UTC");
        System.out.println(strings);

    }
}
