package com.nabalive.framework.web;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 10/24/11
 */
public class Request {
    private static final Logger logger = LoggerFactory.getLogger(Request.class);

    public final ChannelHandlerContext ctx;
    public final HttpRequest request;
    public final QueryStringDecoder qs;
    public final Map<String, List<String>> parameters;
    public final String content;

    public Request(ChannelHandlerContext ctx, HttpRequest request, QueryStringDecoder qs) {
        this.ctx = ctx;
        this.request = request;
        this.qs = qs;
        Map<String, List<String>> parameters = qs.getParameters();
        String content = null;
        try {

            content = request.getContent().toString(CharsetUtil.UTF_8);
            if (request.getMethod() == HttpMethod.POST)
                qs = new QueryStringDecoder("/?" + content);
            parameters = qs.getParameters();

        }catch(Exception e){
            logger.debug("post request seems DATA");
            logger.trace("error in parsing request", e);
        }
        this.content = content;
        this.parameters = parameters;
    }

    public String getParam(String key, String defaultValue) {
        List<String> l = parameters.get(key);
        if (l != null && l.size() > 0) {
            return l.get(0);
        }
        return null;
    }

    public String getParam(String key) {
        List<String> l = parameters.get(key);
        if (l != null && l.size() > 0) {
            return l.get(0);
        }
        return null;
    }

    public String getHeader(String key) {
        return request.getHeader(key);
    }

    public String getParamOrHeader(String key) {
        String result = getParam(key);
        if (result == null)
            result = request.getHeader(key);
        return result;
    }

}
