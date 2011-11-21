package com.nabalive.framework.web;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 10/20/11
 */
public class HttpServer {
    public final ChannelGroup cg = new DefaultChannelGroup();

    private ServerBootstrap bootstrap = null;
    int port = 9444;

    private RestHandler restHandler;

    public void start() {
        //Timer timer = new HashedWheelTimer();
        System.out.println("Runtime.getRuntime().availableProcessors(): " + Runtime.getRuntime().availableProcessors());

//        ExecutorService bossExec = new OrderedMemoryAwareThreadPoolExecutor(2, 400000000, 2000000000, 60, TimeUnit.SECONDS);
//        ExecutorService ioExec = new OrderedMemoryAwareThreadPoolExecutor(Runtime.getRuntime().availableProcessors() + 1, 400000000, 2000000000, 60, TimeUnit.SECONDS);
//
//        bootstrap = new ServerBootstrap(
//                new NioServerSocketChannelFactory(bossExec, ioExec, Runtime.getRuntime().availableProcessors() + 1));

        // Configure the server.
        bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
        bootstrap.setOption("receiveBufferSize", 128 * 1024);
        bootstrap.setOption("sendBufferSize", 128 * 1024);
        bootstrap.setOption("reuseAddress", true);
        bootstrap.setOption("backlog", 16384);

        final HttpApiServerHandler httpApiServerHandler = new HttpApiServerHandler(restHandler);
        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new HttpApiServerPipelineFactory(httpApiServerHandler));

        // Bind and start to accept incoming connections.
        Channel c = bootstrap.bind(new InetSocketAddress(port));
        cg.add(c);
    }

    public void stop() {
        cg.close().awaitUninterruptibly();
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setRestHandler(RestHandler restHandler) {
        this.restHandler = restHandler;
    }
}
