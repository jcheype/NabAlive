package com.nabalive.server.jabber;

import com.nabalive.server.jabber.handler.IqHandler;
import com.nabalive.server.jabber.handler.MessageHandler;
import com.nabalive.server.jabber.handler.PresenceHandler;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.base64.Base64;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/15/11
 */

@Component
public class NabaliveServerHandler extends IdleStateAwareChannelHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ConnectionManager connectionManager;

    @Autowired
    IqHandler iqHandler;
    @Autowired
    PresenceHandler presenceHandler;
    @Autowired
    MessageHandler messageHandler;

    AtomicLong id = new AtomicLong(System.currentTimeMillis());

    Pattern response64Pattern = Pattern.compile("<response[^>]*>(.*)</response>");

    //username="xxxxxx",nonce="47313",cnonce="8551452066932",nc=00000001,qop=auth,digest-uri="xmpp/www.jcheype.com",response=327aff5fc68a25524df782c8a8883e44,charset=utf-
    Pattern authPattern = Pattern.compile("username=\"([^\"]*)\".*");

    private Status getStatus(ChannelHandlerContext ctx) {
        synchronized (ctx) {
            Status status = (Status) ctx.getAttachment();
            if (status == null) {
                status = new Status(ctx, connectionManager.getEventListeners());
                ctx.setAttachment(status);
            }
            return status;
        }
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        final ChannelBuffer messageCB = (ChannelBuffer) e.getMessage();
        String message = messageCB.toString(CharsetUtil.UTF_8);
        final Status status = getStatus(ctx);


        if (status.getJid() != null)
            logger.debug(status.getJid().getUser() + " <<<<<<<<<<< {}", message);
        else
            logger.debug("<<<<<<<<<<< {}", message);


        if (message.startsWith("<?xml ")) {
            onStreamOpen(ctx, e, message);
        } else if (message.startsWith("<auth ")) {
            onAuth(ctx, e, message);
        } else if (message.startsWith("<response xmlns='urn:ietf:params:xml:ns:xmpp-sasl'")) {
            onResponse(ctx, e, message);
        } else if (message.startsWith("<iq ")) {
            iqHandler.onMessage(ctx, e, status, message);
        } else if (message.startsWith("<presence ")) {
            presenceHandler.onMessage(ctx, e, status, message);
        } else if (message.startsWith("<message ")) {
            messageHandler.onMessage(ctx, e, status, message);
        }
    }


    private void write(Channel channel, String data) {
        logger.debug(">>>>>>>>>> " + data);

        channel.write(ChannelBuffers.copiedBuffer(data.getBytes(CharsetUtil.UTF_8)));
    }

    private void onResponse(ChannelHandlerContext ctx, MessageEvent e, String message) {
        final Matcher matcher = response64Pattern.matcher(message);
        if (matcher.matches()) {
            String base64Response = matcher.group(1);
            final ChannelBuffer decode = Base64.decode(ChannelBuffers.copiedBuffer(base64Response.getBytes(CharsetUtil.UTF_8)));
            String decodedAuth = decode.toString(CharsetUtil.UTF_8);
            logger.debug("<<<DECODED<<<<" + decodedAuth);
            Matcher matcherAuth = authPattern.matcher(decodedAuth);
            if (matcherAuth.matches()) {
                String username = matcherAuth.group(1);
                Status status = getStatus(ctx);
                status.setAuthenticated(true);
                status.setUsername(username);
                connectionManager.put(username, status);
            }

        }

        final String reply1 = "<success xmlns='urn:ietf:params:xml:ns:xmpp-sasl'/>";
        write(e.getChannel(), reply1);
    }

    private void onAuth(ChannelHandlerContext ctx, MessageEvent e, String message) {
        Random randomGenerator = new Random();
        final String chanllenge = "nonce=\"" + randomGenerator.nextInt(99999) + "\",qop=\"auth\",charset=utf-8,algorithm=md5-sess";
        final ChannelBuffer encode = Base64.encode(ChannelBuffers.copiedBuffer(chanllenge.getBytes(CharsetUtil.UTF_8)));
        final String reply1 = "<challenge xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>" +
                encode.toString(CharsetUtil.UTF_8) +
                "</challenge>";
        write(e.getChannel(), reply1);
    }


    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        String username = getStatus(ctx).getUsername();
        logger.debug("Channel closed: " + username);
        if (username != null)
            connectionManager.remove(username);
    }

    private void onStreamOpen(ChannelHandlerContext ctx, MessageEvent e, String message) {
        if (!getStatus(ctx).isAuthenticated()) {
            final String reply1 = "<?xml version='1.0'?><stream:stream xmlns='jabber:client' " +
                    "xmlns:stream='http://etherx.jabber.org/streams' id='" + id.incrementAndGet() + "' " +
                    "from='www.nabalive.server.jabber.com' version='1.0' xml:lang='en'>";

            write(e.getChannel(), reply1);

            final String reply2 = "<stream:features>\n" +
                    "<mechanisms xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>\n" +
                    "<mechanism>\n" +
                    "DIGEST-MD5\n" +
                    "</mechanism>\n" +
                    "<mechanism>\n" +
                    "PLAIN\n" +
                    "</mechanism>\n" +
                    "</mechanisms>\n" +
                    "<register xmlns='http://violet.net/features/violet-register'/>\n" +
                    "</stream:features>";

            write(e.getChannel(), reply2);
        } else {
            write(e.getChannel(), "<?xml version='1.0'?><stream:stream xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' id='1331400675' from='www.nabalive.server.jabber.com' version='1.0' xml:lang='en'>");
            write(e.getChannel(), "<stream:features><bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'><required/></bind><unbind xmlns='urn:ietf:params:xml:ns:xmpp-bind'/><session xmlns='urn:ietf:params:xml:ns:xmpp-session'/></stream:features>");
        }
    }

    @Override
    public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {
        String username = getStatus(ctx).getUsername();
        logger.debug("Channel idle: " + username);

        e.getChannel().close();
    }
}
