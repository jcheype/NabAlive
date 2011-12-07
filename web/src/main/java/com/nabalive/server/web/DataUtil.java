package com.nabalive.server.web;

import com.google.common.io.ByteStreams;
import org.iq80.snappy.SnappyInputStream;
import org.iq80.snappy.SnappyOutputStream;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 12/7/11
 */
public class DataUtil {
    
    public static ChannelBuffer compress(ChannelBuffer in) throws IOException {
        ChannelBufferInputStream channelBufferInputStream = new ChannelBufferInputStream(in);
        ChannelBuffer out = ChannelBuffers.dynamicBuffer(in.readableBytes());
        ChannelBufferOutputStream channelBufferOutputStream = new ChannelBufferOutputStream(out);

        compress(ByteStreams.toByteArray(channelBufferInputStream), channelBufferOutputStream);
        return out;
    }

    public static byte[] compress(byte[] in) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        compress(in, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    public static void compress(byte[] in, OutputStream out) throws IOException {
        SnappyOutputStream snappyOutputStream = new SnappyOutputStream(out);
        snappyOutputStream.write(in);
        snappyOutputStream.close();
    }

    public static ChannelBuffer decompress(ChannelBuffer in) throws IOException {
        ChannelBufferInputStream channelBufferInputStream = new ChannelBufferInputStream(in);
        SnappyInputStream inputStream = new SnappyInputStream(channelBufferInputStream);
        ChannelBuffer channelBuffer = ChannelBuffers.copiedBuffer(ByteStreams.toByteArray(inputStream));
        return channelBuffer;
    }

    public static byte[] decompress(byte[] in) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(in);
        SnappyInputStream inputStream = new SnappyInputStream(byteArrayInputStream);
        return ByteStreams.toByteArray(inputStream);
    }
}
