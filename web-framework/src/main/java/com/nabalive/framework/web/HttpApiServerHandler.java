package com.nabalive.framework.web;

import com.nabalive.framework.web.exception.HttpException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.Map;
import java.util.UUID;

import org.jboss.netty.handler.codec.http.HttpHeaders;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 5/5/11
 */
public class HttpApiServerHandler extends IdleStateAwareChannelHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpApiServerHandler.class);
    private RestHandler restHandler;

    public HttpApiServerHandler(RestHandler restHandler) {
        this.restHandler = restHandler;
    }

    private void execBeforeFilters(Request request, Response response) throws Exception {
        String path = request.qs.getPath();

        for (Route route : restHandler.getRouteList(RestHandler.beforeFilter)) {
            final Map<String, String> map = route.parse(path, request.request.getHeader("Content-Type"));
            if (map != null) {
                route.handle(request, response, map);
            }
        }
    }

    private void execAfterFilters(Request request, Response response) throws Exception {
        String path = request.qs.getPath();

        for (Route route : restHandler.getRouteList(RestHandler.afterFilter)) {
            final Map<String, String> map = route.parse(path, request.request.getHeader("Content-Type"));
            if (map != null) {
                route.handle(request, response, map);
            }
        }
    }

    private void execHttpMethod(Request request, Response response) throws Exception {
        String path = request.qs.getPath();

        for (Route route : restHandler.getRouteList(request.request.getMethod())) {
            final Map<String, String> map = route.parse(path, request.request.getHeader("Content-Type"));
            if (map != null) {
                route.handle(request, response, map);
                return;
            }
        }

        throw new HttpException(HttpResponseStatus.NOT_FOUND);
    }


    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) {
        HttpRequest httpRequest = (HttpRequest) event.getMessage();
        logger.debug("Request: {}", httpRequest);
        
        QueryStringDecoder qs = new QueryStringDecoder(httpRequest.getUri());

        final Request request = new Request(ctx, httpRequest, qs);
        final Response response = new Response(ctx, httpRequest);

        try {
            execBeforeFilters(request, response);
            execHttpMethod(request, response);
            execAfterFilters(request, response);
        } catch (HttpException e) {
            HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, e.getStatus());
            ctx.getChannel().write(httpResponse).addListener(ChannelFutureListener.CLOSE);
        } catch (Exception e) {
            HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
            if (logger.isDebugEnabled()) {
                ChannelBuffer channelBuffer = ChannelBuffers.dynamicBuffer();
                PrintWriter printWriter = new PrintWriter(new ChannelBufferOutputStream(channelBuffer));
                e.printStackTrace(printWriter);
                printWriter.close();
                httpResponse.setContent(channelBuffer);
            }
            logger.error("web server error: ", e);
            ctx.getChannel().write(httpResponse).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        String errorid = UUID.randomUUID().toString();
        logger.error("ERROR HTTP: {}\n", errorid, e.getCause());

        HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        httpResponse.setHeader("id", errorid);
        if (logger.isDebugEnabled()) {
            ChannelBuffer channelBuffer = ChannelBuffers.dynamicBuffer();
            PrintWriter printWriter = new PrintWriter(new ChannelBufferOutputStream(channelBuffer));
            e.getCause().printStackTrace(printWriter);
            printWriter.close();
            httpResponse.setContent(channelBuffer);
        }
        ctx.getChannel().write(httpResponse).addListener(ChannelFutureListener.CLOSE);
        Channel ch = e.getChannel();
        ch.close();

    }

    @Override
    public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {
        e.getChannel().close();
    }

    public void setRestHandler(RestHandler restHandler) {
        this.restHandler = restHandler;
    }
}
