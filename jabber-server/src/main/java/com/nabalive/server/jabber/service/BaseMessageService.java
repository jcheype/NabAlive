package com.nabalive.server.jabber.service;

import com.nabalive.common.server.MessageService;
import com.nabalive.common.server.Packet;
import com.nabalive.server.jabber.ConnectionManager;
import com.nabalive.server.jabber.Status;
import com.nabalive.server.jabber.packet.MessagePacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/17/11
 */

@Component
public class BaseMessageService implements MessageService {
    @Autowired
    ConnectionManager connectionManager;

    @Override
    public void sendMessage(String to, String message) {
        final MessagePacket messagePacket = new MessagePacket(message);
        sendMessage(to, messagePacket);
    }

    @Override
    public void sendMessage(String to, Packet message){
        Status status = checkNotNull(connectionManager.get(to));
        String xmpp = message.getXmpp("www.nabalive.server.jabber", status.getJid().toString());
        status.write(xmpp);
    }
}
