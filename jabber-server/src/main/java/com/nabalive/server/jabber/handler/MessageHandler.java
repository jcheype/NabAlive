package com.nabalive.server.jabber.handler;

import com.nabalive.common.server.Event;
import com.nabalive.server.jabber.Status;
import com.nabalive.server.jabber.packet.MessagePacket;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/16/11
 */

@Component
public class MessageHandler extends JabberBaseHandler {
    @Override
    protected void onMessage(ChannelHandlerContext ctx, MessageEvent e, Status status, String message, Document document) {
        String from = document.getDocumentElement().getAttribute("from");
        if (message.contains("<button xmlns=\"violet:nabaztag:button\">"))
            status.onEvent(new Event(message, Event.Type.BUTTON));
        else if (message.contains("<ears xmlns=\"violet:nabaztag:ears\">"))
            status.onEvent(new Event(message, Event.Type.EARS));
        else
            status.onEvent(new Event(message, Event.Type.UNKNOWN_MESSAGE));

    }
}
