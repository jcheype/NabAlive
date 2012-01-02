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
    private final String HOST = System.getProperty("host");

    @Autowired
    private SimpleRestHandler restHandler;

    @Autowired
    private ApplicationManager applicationManager;

    //private final Pattern domainPattern = Pattern.compile("([^:]+)");

    @PostConstruct
    void init() {
        restHandler
                .get(new Route(".*/locate.jsp") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        StringBuilder sb = new StringBuilder();
                        sb.append("ping " + HOST + "\n");
                        sb.append("broad " + HOST + "\n");
                        if (XMPP_PORT != null)
                            sb.append("xmpp_domain " + HOST + ":" + XMPP_PORT + "\n");
                        else
                            sb.append("xmpp_domain " + HOST + "\n");
                        response.write(sb.toString());

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
