package com.nabalive.server.web.controller;

import com.google.common.io.ByteStreams;
import com.nabalive.application.core.Application;
import com.nabalive.application.core.ApplicationManager;
import com.nabalive.data.core.model.ApplicationConfig;
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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/20/11
 */
@Component
public class LocateController {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String XMPP_PORT = System.getProperty("xmpp.port");

    @Autowired
    private SimpleRestHandler restHandler;

    @Autowired
    private ApplicationManager applicationManager;

    private final Pattern domainPattern = Pattern.compile("([^:]+)");

    @PostConstruct
    void init() {
        restHandler
                .get(new Route(".*/locate.jsp") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        String host = request.request.getHeader("Host");
                        logger.debug("host: {}", host);
                        Matcher matcher = domainPattern.matcher(host);
                        if (matcher.find()) {
                            String domain = matcher.group(1);
                            StringBuilder sb = new StringBuilder();
                            sb.append("ping " + domain + "\n");
                            sb.append("broad " + domain + "\n");
                            if(XMPP_PORT != null)
                                sb.append("xmpp_domain " + domain + ":"+ XMPP_PORT +"\n");
                            else
                                sb.append("xmpp_domain " + domain + "\n");
                            response.write(sb.toString());
                        } else {
                            response.write(new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR));
                        }
                    }
                })
                .get(new Route(".*/bc.jsp") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        InputStream resourceAsStream = getClass().getResourceAsStream("/bc.jsp");

                        response.write(ByteStreams.toByteArray(resourceAsStream));
                    }
                });
    }
}
