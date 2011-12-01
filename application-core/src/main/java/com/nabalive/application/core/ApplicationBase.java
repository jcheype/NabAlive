package com.nabalive.application.core;

import com.nabalive.data.core.model.ApplicationConfig;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/18/11
 */
public class ApplicationBase implements Application {
    public void onStartup(String mac, ApplicationConfig applicationConfig) throws Exception {}
    public void onButton(String mac, int clicks) throws Exception {}
    public void onEars(String mac, int stepLeft, int stepRight) throws Exception {}
    public void onRfid(String mac, String content) throws Exception {}
    public void onPresence(String mac, String status) throws Exception {}

    @Override
    public String getApikey() { throw new NotImplementedException(); }

}
