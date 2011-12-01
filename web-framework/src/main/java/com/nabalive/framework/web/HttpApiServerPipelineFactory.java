package com.nabalive.framework.web;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

import static org.jboss.netty.channel.Channels.pipeline;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 5/5/11
 */
public class HttpApiServerPipelineFactory implements ChannelPipelineFactory {
    private final Timer timer = new HashedWheelTimer();
    private final HttpApiServerHandler handler;

    public HttpApiServerPipelineFactory(HttpApiServerHandler handler) {
        this.handler = handler;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = pipeline();

        // Uncomment the following line if you want HTTPS
//        SSLEngine engine = //SecureChatSslContextFactory.getServerContext().createSSLEngine();
//        engine.setUseClientMode(false);
//        pipeline.addLast("ssl", new SslHandler(engine));

        pipeline.addLast("timeout", new IdleStateHandler(timer, 0, 0, 20));

        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("encoder", new HttpResponseEncoder());
        
//        pipeline.addLast("comressor", new HttpContentCompressor(9));
        pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());


        //pipeline.addLast("executor", eh);

        pipeline.addLast("handler", handler);
        return pipeline;
    }
}
