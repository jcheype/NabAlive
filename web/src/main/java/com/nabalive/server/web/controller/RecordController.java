package com.nabalive.server.web.controller;

import com.google.code.morphia.query.Query;
import com.google.common.io.ByteStreams;
import com.mongodb.WriteConcern;
import com.nabalive.common.server.MessageService;
import com.nabalive.data.core.dao.NabaztagDAO;
import com.nabalive.data.core.dao.TmpDataDAO;
import com.nabalive.data.core.model.Nabaztag;
import com.nabalive.data.core.model.TmpData;
import com.nabalive.framework.web.Request;
import com.nabalive.framework.web.Response;
import com.nabalive.framework.web.Route;
import com.nabalive.framework.web.SimpleRestHandler;
import com.nabalive.framework.web.exception.HttpException;
import com.nabalive.server.jabber.ConnectionManager;
import org.bson.types.ObjectId;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 12/7/11
 */

@Component
public class RecordController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SimpleRestHandler restHandler;

    @Autowired
    private NabaztagDAO nabaztagDAO;

    @Autowired
    ConnectionManager connectionManager;

    @Autowired
    TmpDataDAO tmpDataDAO;

    @Autowired
    MessageService messageService;

    ScheduledExecutorService ses = Executors.newScheduledThreadPool(8);

    @PostConstruct
    void init() {
        restHandler
                .post(new Route("/vl/record.jsp") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        String mac = checkNotNull(request.getParam("sn")).toLowerCase();
                        if (!connectionManager.containsKey(mac))
                            throw new HttpException(HttpResponseStatus.NOT_FOUND, "sn is not connected");

                        Nabaztag nabaztag = checkNotNull(nabaztagDAO.findOne("macAddress", mac));

                        ChannelBuffer content = request.request.getContent();
                        logger.debug("record orig size: {}", content.readableBytes());
                        ChannelBufferInputStream inputStream = new ChannelBufferInputStream(content);

                        TmpData sound = new TmpData();
                        sound.setData(ByteStreams.toByteArray(inputStream));
                        tmpDataDAO.save(sound, WriteConcern.SAFE);


                        String host = request.request.getHeader("Host");
                        String url = "http://" + host + "/record/" + sound.getId().toString();
                        logger.debug("sound url: {}", url);

                        final String command = "ST " + url + "\nMW\n";

                        Query<Nabaztag> query = nabaztagDAO.createQuery();
                        query.filter("subscribe", nabaztag.getId());
                        List<Nabaztag> nabaztags = nabaztagDAO.find(query).asList();
                        logger.debug("sending to {} subscribers", nabaztags.size());
                        for (Nabaztag nab : nabaztags) {
                            if (connectionManager.containsKey(nab.getMacAddress())) {
                                final Nabaztag nabTmp = nab;
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            logger.debug("sending to {}", nabTmp.getMacAddress());
                                            logger.debug("command {}", command);
                                            messageService.sendMessage(nabTmp.getMacAddress(), command);
                                        } catch (ExecutionException e) {
                                            logger.error("cannot send msg", e);
                                        }
                                    }
                                };
                                ses.schedule(runnable, 100, TimeUnit.MILLISECONDS);
                            }
                        }
                        response.write("ok");
                    }
                })
                .get(new Route("/record/:recordId") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        ObjectId recordId = new ObjectId(checkNotNull(map.get("recordId")));

                        TmpData sound = checkNotNull(tmpDataDAO.get(recordId));

                        response.write(sound.getData());
                    }
                });
    }
}
