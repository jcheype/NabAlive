package com.nabalive.server.jabber.service;

import com.nabalive.common.server.EventListener;
import com.nabalive.common.server.EventRegistrationService;
import com.nabalive.server.jabber.ConnectionManager;
import com.nabalive.server.jabber.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/17/11
 */

@Component
public class BaseEventRegistrationService implements EventRegistrationService{
    @Autowired
    ConnectionManager connectionManager;

    @Override
    public void register(String id, EventListener eventListener) {
        Status status = connectionManager.get(id);
        if(status == null){
            throw new IllegalArgumentException("id ["+id+"] is not found");
        }
        status.registerEvent(eventListener);
    }
}
