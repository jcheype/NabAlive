package com.nabalive.server.jabber.packet;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/15/11
 */
public class MessagePacket extends BasePacket {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int TYPE = 0x0A;
    private final String content;
    private final int inversion_table[] = {1, 171, 205, 183, 57, 163, 197, 239, 241, 27, 61, 167, 41, 19, 53, 223, 225, 139, 173, 151, 25, 131, 165, 207, 209, 251, 29, 135, 9, 243, 21, 191, 193, 107, 141, 119, 249, 99, 133, 175, 177, 219, 253, 103, 233, 211, 245, 159, 161, 75, 109, 87, 217, 67, 101, 143, 145, 187, 221, 71, 201, 179, 213, 127, 129, 43, 77, 55, 185, 35, 69, 111, 113, 155, 189, 39, 169, 147, 181, 95, 97, 11, 45, 23, 153, 3, 37, 79, 81, 123, 157, 7, 137, 115, 149, 63, 65, 235, 13, 247, 121, 227, 5, 47, 49, 91, 125, 231, 105, 83, 117, 31, 33, 203, 237, 215, 89, 195, 229, 15, 17, 59, 93, 199, 73, 51, 85, 255};

    public MessagePacket(String content) {
        this.content = content;
    }

    @Override
    public ChannelBuffer getData() {
        final ChannelBuffer channelBuffer = ChannelBuffers.dynamicBuffer();
        int previousChar = 35;
        channelBuffer.writeByte(0);
        for (int b : content.getBytes(CharsetUtil.UTF_8)) {
            channelBuffer.writeByte(inversion_table[previousChar % 128] * b + 47);
            previousChar = b;
        }

        return channelBuffer;
    }


    @Override
    protected int getType() {
        return TYPE;
    }
}
