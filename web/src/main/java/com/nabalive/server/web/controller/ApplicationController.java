package com.nabalive.server.web.controller;

import com.google.common.io.ByteStreams;
import com.nabalive.data.core.dao.ApplicationLogoDAO;
import com.nabalive.data.core.dao.ApplicationStoreDAO;
import com.nabalive.data.core.model.ApplicationLogo;
import com.nabalive.data.core.model.ApplicationStore;
import com.nabalive.framework.web.Request;
import com.nabalive.framework.web.Response;
import com.nabalive.framework.web.Route;
import com.nabalive.framework.web.SimpleRestHandler;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/29/11
 */

@Component
public class ApplicationController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SimpleRestHandler restHandler;

    @Autowired
    private ApplicationStoreDAO applicationStoreDAO;

    @Autowired
    private ApplicationLogoDAO applicationLogoDAO;

    @PostConstruct
    void init() {
        restHandler
                .get(new Route("/applications") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        List<ApplicationStore> applicationStores = applicationStoreDAO.find().asList();
                        response.writeJSON(applicationStores);
                    }
                })
                .get(new Route("/applications/:apikey/logo.png") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        ChannelBuffer buffer;
                        String apikey = checkNotNull(map.get("apikey"));
                        ApplicationLogo applicationLogo = applicationLogoDAO.get(apikey);

                        if(applicationLogo != null)
                            buffer = ChannelBuffers.copiedBuffer(applicationLogo.getData());
                        else {
                            byte[] bytes = ByteStreams.toByteArray(getClass().getResourceAsStream("/app/default.png"));
                            buffer = ChannelBuffers.copiedBuffer(bytes);
                        }

                        DefaultHttpResponse defaultHttpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                        defaultHttpResponse.setHeader(HttpHeaders.Names.CONTENT_TYPE, "image/png");
                        defaultHttpResponse.setContent(buffer);

                        response.write(defaultHttpResponse);
                    }
                });
    }
}
