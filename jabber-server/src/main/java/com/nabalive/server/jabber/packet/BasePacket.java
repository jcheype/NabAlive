package com.nabalive.server.jabber.packet;

import com.nabalive.common.server.Packet;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.base64.Base64;
import org.jboss.netty.util.CharsetUtil;

import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/24/11
 */
public abstract class BasePacket implements Packet {


    protected abstract int getType();

    protected abstract ChannelBuffer getData();

    public ChannelBuffer getFullData() {
        final ChannelBuffer channelBuffer = ChannelBuffers.dynamicBuffer();
        channelBuffer.writeByte(0x7f);
        channelBuffer.writeByte(getType());
        final ChannelBuffer encryptedData = getData();
        int len = encryptedData.readableBytes();
        byte[] lenBytes = new byte[]{(byte) (len >>> 16), (byte) (len >>> 8), (byte) len};
        channelBuffer.writeBytes(lenBytes);
        channelBuffer.writeBytes(encryptedData);
        channelBuffer.writeByte(0xFF);

        return channelBuffer;
    }

    public String getBase64() {
        return Base64.encode(getFullData(), false).toString(CharsetUtil.UTF_8);
    }

    public String getXmpp(String from, String to) {
        StringBuilder msg = new StringBuilder();
        msg.append("<message from='" + from + "' ");
        msg.append("to='" + to + "' ");
        msg.append("id='" + UUID.randomUUID().toString() + "'>");
        msg.append("<packet xmlns='violet:packet' format='1.0' ttl='604800'>");
        msg.append(getBase64());
        msg.append("</packet></message>");
        return msg.toString();
    }
}
