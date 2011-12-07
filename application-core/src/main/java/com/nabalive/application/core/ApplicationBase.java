package com.nabalive.application.core;

import com.nabalive.data.core.model.ApplicationConfig;
import com.nabalive.data.core.model.Nabaztag;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/18/11
 */
public class ApplicationBase implements Application {
    public void onStartup(Nabaztag nabaztag, ApplicationConfig applicationConfig) throws Exception {}
    public void onButton(Nabaztag nabaztag, int clicks) throws Exception {}
    public void onEars(Nabaztag nabaztag, int stepLeft, int stepRight) throws Exception {}
    public void onRfid(Nabaztag nabaztag, String content) throws Exception {}
    public void onPresence(Nabaztag nabaztag, String status) throws Exception {}

    @Override
    public String getApikey() { throw new UnsupportedOperationException(); }

}
