package com.nabalive.server.jabber.packet;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 12/17/11
 */
public class PingPacket extends BasePacket {
    private final int TYPE = 0x03;
    private final int time;

    public PingPacket(int time) {
        this.time = time;
    }

    @Override
    protected int getType() {
        return TYPE;
    }

    @Override
    protected ChannelBuffer getData() {
        ChannelBuffer channelBuffer = ChannelBuffers.buffer(1);
        channelBuffer.writeByte(time);
        return channelBuffer;
    }
}
