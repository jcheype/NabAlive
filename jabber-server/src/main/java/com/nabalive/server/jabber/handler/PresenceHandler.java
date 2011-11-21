package com.nabalive.server.jabber.handler;

import com.nabalive.common.server.Event;
import com.nabalive.server.jabber.Status;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/15/11
 */

@Component
public class PresenceHandler extends JabberBaseHandler {
    public void onMessage(ChannelHandlerContext ctx, MessageEvent e, Status status, String message, Document document) {
        String id = document.getDocumentElement().getAttribute("id");
        String from = document.getDocumentElement().getAttribute("from");

        String reply = "<presence from='" + from + "' to='" + from + "' id='" + id + "'/>";
        write(e.getChannel(), reply);

        status.onEvent(new Event(message, Event.Type.PRESENCE));
    }
}
