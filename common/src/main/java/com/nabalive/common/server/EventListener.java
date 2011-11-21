package com.nabalive.common.server;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/17/11
 */
public interface EventListener {
    void onEvent(String sender, Event event) throws Exception;
}
