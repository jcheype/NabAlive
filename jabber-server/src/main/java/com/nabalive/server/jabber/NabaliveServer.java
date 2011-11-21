package com.nabalive.server.jabber;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import static org.jboss.netty.channel.Channels.pipeline;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/16/11
 */

@Component("nabaliveServer")
public class NabaliveServer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Timer timer = new HashedWheelTimer();

    @Autowired
    NabaliveServerHandler nabaliveServerHandler;
    private ServerBootstrap bootstrap;
    private Channel bind;

    @PostConstruct
    public void start() {
        logger.info("Starting server.");
        // Configure the server.
        bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Set up the pipeline factory.
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = pipeline();
                pipeline.addLast("timeout", new IdleStateHandler(timer, 0, 0, 20));
                pipeline.addLast("nabaliveServerHandler", nabaliveServerHandler);
                return pipeline;
            }
        });

        bootstrap.setOption("reuseAddress", true);
        // Bind and start to accept incoming connections.
        bind = bootstrap.bind(new InetSocketAddress(5222));

    }

    @PreDestroy
    public void stop(){
        bind.close();
    }
}
