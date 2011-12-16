package com.nabalive.server.web.controller;

import com.google.common.base.Supplier;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.io.ByteStreams;
import com.nabalive.data.core.dao.NabaztagDAO;
import com.nabalive.data.core.dao.UserDAO;
import com.nabalive.data.core.model.Nabaztag;
import com.nabalive.framework.web.Request;
import com.nabalive.framework.web.Response;
import com.nabalive.framework.web.Route;
import com.nabalive.framework.web.SimpleRestHandler;
import com.nabalive.server.jabber.ConnectionManager;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import org.apache.commons.lang.StringEscapeUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/28/11
 */
@Component
public class TtsController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SimpleRestHandler restHandler;

    @Autowired
    ConnectionManager connectionManager;

    @Autowired
    private NabaztagDAO nabaztagDAO;

    private final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private final Cache<String, byte[]> ttsCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .concurrencyLevel(4)
            .expireAfterWrite(10, TimeUnit.DAYS)
            .build(CacheLoader.from(new Supplier<byte[]>() {
                public byte[] get() {
                    return null;
                }
            }));

    private final String frenchTtsUrl = checkNotNull(System.getProperty("frenchTtsUrl"), "You must set frenchTtsUrl property");

    @PostConstruct
    void init() {
        restHandler
                .get(new Route("/tts/:apikey/:voice") {
                    @Override
                    public void handle(Request request, final Response response, Map<String, String> map) throws Exception {
                        String text = StringEscapeUtils.escapeXml(checkNotNull(request.getParam("text")));
                        String voice = checkNotNull(map.get("voice"));


                        if (!text.startsWith("<s>")) {
                            text = "<s>" + text + "</s>";
                        }

                        final String key = text + "|" + voice;
                        byte[] bytes = ttsCache.asMap().get(key);
                        if(bytes != null){
                            response.write(ChannelBuffers.copiedBuffer(bytes));
                            return;
                        }

                        asyncHttpClient.preparePost(frenchTtsUrl).setBody(text).execute(new AsyncCompletionHandler<com.ning.http.client.Response>() {

                            @Override
                            public com.ning.http.client.Response onCompleted(com.ning.http.client.Response asyncResponse) throws Exception {
                                InputStream inputStream = asyncResponse.getResponseBodyAsStream();
                                byte[] bytes = ByteStreams.toByteArray(inputStream);
                                ttsCache.asMap().put(key, bytes);
                                response.write(bytes);
                                return asyncResponse;
                            }

                            @Override
                            public void onThrowable(Throwable t) {
                                logger.error("TTS error", t);
                                HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
                                response.write(httpResponse);
                            }
                        });

                    }
                })
                .get(new Route("/tts/:voice") {
                    @Override
                    public void handle(Request request, final Response response, Map<String, String> map) throws Exception {
                        String text = StringEscapeUtils.escapeXml(checkNotNull(request.getParam("text")));
                        String voice = checkNotNull(map.get("voice"));


                        if (!text.startsWith("<s>")) {
                            text = "<s>" + text + "</s>";
                        }

                        final String key = text + "|" + voice;
                        byte[] bytes = ttsCache.asMap().get(key);
                        if(bytes != null){
                            response.write(ChannelBuffers.copiedBuffer(bytes));
                            return;
                        }

                        asyncHttpClient.preparePost(frenchTtsUrl).setBody(text).execute(new AsyncCompletionHandler<com.ning.http.client.Response>() {

                            @Override
                            public com.ning.http.client.Response onCompleted(com.ning.http.client.Response asyncResponse) throws Exception {
                                InputStream inputStream = asyncResponse.getResponseBodyAsStream();
                                byte[] bytes = ByteStreams.toByteArray(inputStream);
                                ttsCache.asMap().put(key, bytes);
                                response.write(bytes);
                                return asyncResponse;
                            }

                            @Override
                            public void onThrowable(Throwable t) {
                                logger.error("TTS error", t);
                                HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
                                response.write(httpResponse);
                            }
                        });

                    }
                });
    }
}
