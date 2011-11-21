package com.nabalive.server.jabber.service;

import com.nabalive.common.server.MessageService;
import com.nabalive.server.jabber.ConnectionManager;
import com.nabalive.server.jabber.Status;
import com.nabalive.server.jabber.packet.MessagePacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

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
        Status status = checkNotNull(connectionManager.get(to));

        //final String commande = "ST http://zenradio.fr:8800\nPL " + randomGenerator.nextInt(8) + "\nMW\n";
        final MessagePacket messagePacket = new MessagePacket(message);

        String xmpp = messagePacket.getXmpp("www.nabalive.server.jabber", status.getJid().toString());

        status.write(xmpp);
    }
}
