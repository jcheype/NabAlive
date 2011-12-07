package com.nabalive.application.core;

import com.nabalive.data.core.model.ApplicationConfig;
import com.nabalive.data.core.model.Nabaztag;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/22/11
 */
public interface Application {
    public void onStartup(Nabaztag nabaztag, ApplicationConfig applicationConfig) throws Exception;
    public void onButton(Nabaztag nabaztag, int clicks) throws Exception ;
    public void onEars(Nabaztag nabaztag, int stepLeft, int stepRight) throws Exception ;
    public void onRfid(Nabaztag nabaztag, String content) throws Exception ;
    public void onPresence(Nabaztag nabaztag, String status) throws Exception ;

    public String getApikey();
}
