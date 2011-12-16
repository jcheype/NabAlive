package com.nabalive.server.web;

import org.apache.commons.codec.binary.Hex;
import org.jboss.netty.buffer.ChannelBuffer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 12/12/11
 */
public class ChorTest {

    @Test
    public void testGenChor() throws Exception {
        ChorBuilder chorBuilder = new ChorBuilder("10,0,motor,1,20,0,0,0,led,2,0,238,0,2,led,1,250,0,0,3,led,2,0,0,0", 5);
        ChannelBuffer buffer = chorBuilder.build();
        System.out.println("length: " + buffer.readableBytes());
        System.out.println(Hex.encodeHexString(buffer.array()));
    }
}
