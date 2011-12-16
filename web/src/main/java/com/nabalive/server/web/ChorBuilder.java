package com.nabalive.server.web;

import org.apache.commons.codec.binary.Hex;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/25/11
 */
public class ChorBuilder {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    ChannelBuffer channelBuffer = ChannelBuffers.dynamicBuffer();
    int timer = 0;

    public ChorBuilder() {
        this(10);
    }

    private int atoi(String number) {
        return Integer.parseInt(number);
    }

    private byte atob(String number) {
        return (byte) Integer.parseInt(number);
    }

    //10,0,motor,1,20,0,0,0,led,2,0,238,0,2,led,1,250,0,0,3,led,2,0,0,0
    public ChorBuilder(String chor, int loop) {
        String[] items = chor.split(",");
        List<String> itemList = Arrays.asList(items);
        String tempoStr = itemList.get(0);
        int tempo = atoi(tempoStr);
        setTempo(tempo);

        itemList = itemList.subList(1, itemList.size());
        logger.debug("itemList: {}", itemList);

        for (int i = 0; i < loop; i++) {
            Iterator<String> it = itemList.iterator();

            while (it.hasNext()) {
                waitChor(atoi(it.next()));
                String cmd = it.next();
                if ("motor".equals(cmd)) {
                    byte ear = atob(it.next());
                    byte pos = (byte) (atoi(it.next()) / 10);
                    it.next(); // drop
                    byte dir = atob(it.next());

                    setEar(ear, dir, pos);
                }
                if ("led".equals(cmd)) {
                    byte led = atob(it.next());
                    byte r = atob(it.next());
                    byte v = atob(it.next());
                    byte b = atob(it.next());

                    setLed(led, r, v, b);
                }
            }
        }

    }

    public ChorBuilder(int tempo) {
        setTempo(tempo);
    }

    private void setTempo(int tempo) {
        channelBuffer.writeByte(0);
        channelBuffer.writeByte(1);
        channelBuffer.writeByte(tempo);
    }

    public ChorBuilder waitChor(int time) {
        timer = time;
        return this;
    }

    public ChorBuilder setLed(byte led, String rvb) {
        try {
            byte[] bytes = Hex.decodeHex(rvb.toCharArray());
            channelBuffer.writeByte(getTime());
            channelBuffer.writeByte(7);
            channelBuffer.writeByte(led);
            channelBuffer.writeBytes(bytes, 0, 3);
            channelBuffer.writeZero(2);

        } catch (Exception e) {
            logger.info("error chor: ", e);
        }
        return this;
    }

    public ChorBuilder setLed(byte led, int rvb) {
        channelBuffer.writeByte(getTime());
        channelBuffer.writeByte(7);
        channelBuffer.writeByte(led);
        channelBuffer.writeInt(rvb);
        channelBuffer.writeZero(2);

        return this;
    }

    public ChorBuilder setLed(byte led, byte r, byte v, byte b) {
        channelBuffer.writeByte(getTime());
        channelBuffer.writeByte(7);
        channelBuffer.writeByte(led);
        channelBuffer.writeByte(r);
        channelBuffer.writeByte(v);
        channelBuffer.writeByte(b);
        channelBuffer.writeZero(2);

        return this;
    }

    public ChorBuilder setLed(byte led) {
        channelBuffer.writeByte(getTime());
        channelBuffer.writeByte(0xE);
        channelBuffer.writeByte(led);
        return this;
    }

    public ChorBuilder setEar(byte ear, byte direction, byte position) {
        channelBuffer.writeByte(getTime());
        channelBuffer.writeByte(8);
        channelBuffer.writeByte(ear);
        channelBuffer.writeByte(position);
        channelBuffer.writeByte(direction);
        return this;
    }

    public ChorBuilder setEarRelative(byte ear, byte position) {
        channelBuffer.writeByte(getTime());
        channelBuffer.writeByte(0x11);
        channelBuffer.writeByte(ear);
        channelBuffer.writeByte(position);
        return this;
    }

    public ChorBuilder randMidi() {
        channelBuffer.writeByte(getTime());
        channelBuffer.writeByte(0x10);
        return this;
    }

    public ChannelBuffer build() {
        ChannelBuffer res = ChannelBuffers.dynamicBuffer();
        res.writeInt(channelBuffer.readableBytes());
        res.writeBytes(channelBuffer);
        res.writeZero(4);
        return res;
    }

    int getTime() {
        int res = timer;
        timer = 0;
        return res;
    }
}
