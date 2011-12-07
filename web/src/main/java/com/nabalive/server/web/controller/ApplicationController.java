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
import com.nabalive.framework.web.exception.HttpException;
import org.apache.commons.fileupload.MultipartStream;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/29/11
 */

@Component
public class ApplicationController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    Pattern boundaryPattern = Pattern.compile("multipart/form-data; boundary=(.*)$");
    Pattern headerContentTypePattern = Pattern.compile("Content-Type: (.*)");

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

                        if (applicationLogo != null)
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

//                .post(new Route("/applications/:apikey/logo.png") {
//                    @Override
//                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
//                        String apikey = checkNotNull(map.get("apikey"));
//                        String contentType = request.getHeader("Content-Type");
//                        Matcher matcher = boundaryPattern.matcher(contentType);
//                        if (!matcher.matches())
//                            throw new HttpException(HttpResponseStatus.INTERNAL_SERVER_ERROR, "bad content type");
//
//                        String boundary = matcher.group(1);
//
//                        ChannelBuffer content = request.request.getContent();
//                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                        MultipartStream multipartStream = new MultipartStream(new ChannelBufferInputStream(content), boundary.getBytes(CharsetUtil.UTF_8));
//                        String contentTypeLogo = null;
//                        boolean nextPart = multipartStream.skipPreamble();
//                        while (nextPart) {
//                            String header = multipartStream.readHeaders();
//                            if(header.contains("form-data; name=\"logo\"")){
//                                contentTypeLogo = "application/octet-stream"; // TODO parse contentType
//                                multipartStream.readBodyData(outputStream);
//                                nextPart=false;
//                            }
//                            else
//                                nextPart = multipartStream.readBoundary();
//                        }
//                        if(contentTypeLogo == null)
//                            throw new HttpException(HttpResponseStatus.INTERNAL_SERVER_ERROR, "cannot parse data");
//
//                        ApplicationLogo applicationLogo = new ApplicationLogo();
//                        applicationLogo.setApikey(apikey);
//                        applicationLogo.setContentType(contentTypeLogo);
//                        applicationLogo.setFilename("logo.png");
//                        applicationLogo.setData(outputStream.toByteArray());
//
//                        applicationLogoDAO.save(applicationLogo);
//
//                        response.writeJSON(applicationLogo);
//                    }
//                })

    }
}
