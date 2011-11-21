package com.nabalive.server.jabber.handler;

import com.nabalive.server.jabber.Status;
import com.nabalive.server.jabber.util.Jid;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/15/11
 */

@Component
public class IqHandler extends JabberBaseHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    Pattern statusChangePattern = Pattern.compile("<bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'><resource>([^<]*)</resource></bind>");

    public void onMessage(ChannelHandlerContext ctx, MessageEvent e, Status status, String message, Document document) {
        String id = document.getDocumentElement().getAttribute("id");
        String from = document.getDocumentElement().getAttribute("from");
        Jid jid = new Jid(from);

        String to = document.getDocumentElement().getAttribute("to");
        final Matcher matcher = statusChangePattern.matcher(message);
        if (matcher.find()) {
            String resource = matcher.group(1);
            logger.info("change status: " + resource);
            String reply = "<iq id='" + id + "' type='result'><bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'>" +
                    "<jid>" + jid.getUser() + "@" + jid.getServer() + "/" + resource + "</jid></bind></iq>";
            write(e.getChannel(), reply);
            jid.setResource(resource);
            status.setJid(jid);
        } else if (message.contains("<session xmlns='urn:ietf:params:xml:ns:xmpp-session'/>")) {
            String reply = "<iq id='" + id + "' type='result' from='" + to + "'><session xmlns='urn:ietf:params:xml:ns:xmpp-session'/></iq>";
            write(e.getChannel(), reply);
        } else if (message.contains("<query xmlns=\"violet:iq:sources\"><packet xmlns=\"violet:packet\" format=\"1.0\"/></query>")) {
            String reply = "<iq from='net.violet.platform@xmpp.nabaztag.com/sources'" +
                    "to='" + from + "' id='" + id + "' type='result'>" +
                    "<query xmlns='violet:iq:sources'>" +
                    "<packet xmlns='violet:packet' format='1.0' ttl='604800'>fwQAAAx////+BAAFAA7/CAALAAABAP8=</packet>" +
                    "</query>" +
                    "</iq>";
            write(e.getChannel(), reply);

        } else if (message.contains("<unbind xmlns='urn:ietf:params:xml:ns:xmpp-bind'>")) {
            String reply = "<iq id='" + id + "' type='result'/>";
            write(e.getChannel(), reply);
        }

    }
}
