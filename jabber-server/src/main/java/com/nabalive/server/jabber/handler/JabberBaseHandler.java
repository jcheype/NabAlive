package com.nabalive.server.jabber.handler;

import com.nabalive.server.jabber.Status;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/16/11
 */

public abstract class JabberBaseHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final ThreadLocal<DocumentBuilder> builderLocal =
            new ThreadLocal<DocumentBuilder>() {
                @Override
                protected DocumentBuilder initialValue() {
                    try {
                        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                        documentBuilderFactory.setNamespaceAware(false);
                        documentBuilderFactory.setValidating(false);
                        documentBuilderFactory.setFeature("http://xml.org/sax/features/namespaces", false);
                        documentBuilderFactory.setFeature("http://xml.org/sax/features/validation", false);
                        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
                        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                        return
                                documentBuilderFactory
                                        .newDocumentBuilder();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };

    final public void onMessage(ChannelHandlerContext ctx, MessageEvent e, Status status, String message) throws IOException, SAXException {
        Document document = builderLocal.get().parse(new InputSource(new StringReader(message)));
        builderLocal.get().reset();
        onMessage(ctx, e, status, message, document);
    }

    public void write(Channel channel, String data) {
        logger.debug(">>>>>>>>>> " + data);
        channel.write(ChannelBuffers.copiedBuffer(data.getBytes(CharsetUtil.UTF_8)));
    }

    protected abstract void onMessage(ChannelHandlerContext ctx, MessageEvent e, Status status, String message, Document document);
}
