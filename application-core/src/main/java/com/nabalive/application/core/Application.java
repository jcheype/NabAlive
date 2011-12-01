package com.nabalive.application.core;

import com.nabalive.data.core.model.ApplicationConfig;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/22/11
 */
public interface Application {
    public void onStartup(String mac, ApplicationConfig applicationConfig) throws Exception;
    public void onButton(String mac, int clicks) throws Exception ;
    public void onEars(String mac, int stepLeft, int stepRight) throws Exception ;
    public void onRfid(String mac, String content) throws Exception ;
    public void onPresence(String mac, String status) throws Exception ;

    public String getApikey();
}
