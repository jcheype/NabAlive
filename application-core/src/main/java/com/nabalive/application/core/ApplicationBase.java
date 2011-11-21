package com.nabalive.application.core;

import com.nabalive.common.server.Event;
import com.nabalive.common.server.EventListener;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/18/11
 */
public abstract class ApplicationBase implements EventListener {
    public abstract void onStartup(String currentid, Map<String,String> params);
    public void onButton(String currentid, int clicks){};
    public void onEars(String currentid, int stepLeft, int stepRight){};
    public void onRfid(String currentid, String content){};
    public void onPresence(String currentid, String status){};
}
