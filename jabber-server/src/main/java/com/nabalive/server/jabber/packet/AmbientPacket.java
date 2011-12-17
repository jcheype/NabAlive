package com.nabalive.server.jabber.packet;

import com.nabalive.common.server.Packet;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/25/11
 */
public class AmbientPacket extends BasePacket {
    private final int TYPE = 0x04;
    private final byte[] HEADER = {0x7F, (byte)0xFF, (byte)0xFF, (byte)0xFE};


    public final static byte Disable_Service = 0;
    public final static byte Service_Weather = 1;
    public final static byte Service_StockMarket = 2;
    public final static byte Service_Periph = 3;
    public final static byte MoveLeftEar = 4;
    public final static byte MoveRightEar = 5;
    public final static byte Service_EMail = 6;
    public final static byte Service_AirQuality = 7;
    public final static byte Service_Nose = 8;
    public final static byte Service_BottomLed = 9;
    public final static byte Service_TaiChi = 0x0e;

    private final ChannelBuffer channelBuffer = ChannelBuffers.dynamicBuffer();

    public AmbientPacket() {
        channelBuffer.writeBytes(HEADER);
    }

    @Override
    protected int getType() {
        return TYPE;
    }

    @Override
    protected ChannelBuffer getData() {
        return channelBuffer.copy();
    }

    public void add(byte service, int value){
        channelBuffer.writeByte(service);
        channelBuffer.writeByte(value);
    }
}
