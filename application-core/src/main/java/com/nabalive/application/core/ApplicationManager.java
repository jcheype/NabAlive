package com.nabalive.application.core;

import com.nabalive.common.server.Event;
import com.nabalive.common.server.EventListener;
import com.nabalive.common.server.MessageService;
import com.nabalive.server.jabber.ConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationManager implements EventListener, MessageService {
    @Autowired
    ConnectionManager connectionManager;

    

    @Override
    public void onEvent(String sender, Event event) throws Exception {
        switch (event.type) {
            case BUTTON:
                break;
            case EARS:
                break;
            case PRESENCE:
                break;
        }
    }

    @Override
    public void sendMessage(String to, String message) {
    }
}
