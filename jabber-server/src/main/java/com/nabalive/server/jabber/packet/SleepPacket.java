package com.nabalive.server.jabber.packet;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 12/16/11
 */
public class SleepPacket extends BasePacket{
    private final int TYPE = 0x0B;
    private final Action action;

    public static enum Action {
        WakeUp, Sleep
    }

    public SleepPacket(Action action) {
        this.action = action;
    }

    @Override
    protected int getType() {
        return TYPE;
    }

    @Override
    protected ChannelBuffer getData() {
        ChannelBuffer channelBuffer = ChannelBuffers.buffer(1);
        if(action == Action.WakeUp)
            channelBuffer.writeByte(0);
        else
            channelBuffer.writeByte(1);

        return channelBuffer;
    }
}
