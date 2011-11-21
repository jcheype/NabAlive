package com.nabalive.server.web.controller;

import com.google.common.io.ByteStreams;
import com.nabalive.framework.web.Request;
import com.nabalive.framework.web.Response;
import com.nabalive.framework.web.Route;
import com.nabalive.framework.web.SimpleRestHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/20/11
 */
@Component
public class LocateController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SimpleRestHandler restHandler;

    private final Pattern domainPattern = Pattern.compile("([^:]+)");

    @PostConstruct
    void init() {
        restHandler.get(new Route("/vl/locate.jsp.*") {
            @Override
            public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                String host = request.request.getHeader("Host");
                logger.debug("host: {}", host);
                Matcher matcher = domainPattern.matcher(host);
                if (matcher.find()) {
                    String domain = matcher.group(1);
                    StringBuilder sb = new StringBuilder();
                    sb.append("ping "+domain+"\n");
                    sb.append("broad "+domain+"\n");
                    sb.append("xmpp_domain "+domain+"\n");
                    response.write(sb.toString());
                } else {
                    response.write(new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR));
                }
            }
        })
        .get(new Route("/vl/bc.jsp.*") {
            @Override
            public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                InputStream resourceAsStream = getClass().getResourceAsStream("/bc.jsp");

                response.write(ByteStreams.toByteArray(resourceAsStream));
            }
        });
    }
}
