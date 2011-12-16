package com.nabalive.server.web;

import com.nabalive.common.server.Event;
import com.nabalive.common.server.EventListener;
import com.nabalive.common.server.MessageService;
import com.nabalive.data.core.dao.NabaztagDAO;
import com.nabalive.data.core.model.Nabaztag;
import com.nabalive.server.jabber.ConnectionManager;
import com.nabalive.server.web.controller.NabaztagController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 12/13/11
 */

@Component
public class ConnectionWelcome implements EventListener{
    public final static String WELCOME_URL = "http://karotz.s3.amazonaws.com/nab/connection.mp3";
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ConnectionManager connectionManager;

    @Autowired
    NabaztagController nabaztagController;

    @PostConstruct
    public void init(){
        connectionManager.addEventListener(this);
    }

    @Autowired
    NabaztagDAO nabaztagDAO;
    
    @Autowired
    MessageService messageService;
    
    @Override
    public void onEvent(String sender, Event event) throws Exception {
        logger.debug("event type {}", event.type);
        logger.debug("event type {}", event.content);
        if(event.type == Event.Type.UNBIND_RESOURCE && event.content.contains("<resource>boot</resource></unbind>")){
            Nabaztag nabaztag = nabaztagDAO.findOne("macAddress", sender);
            logger.debug("WELCOME: unbind {}", nabaztag);

            if(nabaztag == null){
                logger.debug("WELCOME: SENDING SOUND");
                messageService.sendMessage(sender, "ST " + WELCOME_URL + "\nMW\n");
            }
        }
    }
}
