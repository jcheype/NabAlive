package com.nabalive.server.web.controller;

import com.nabalive.application.core.ApplicationManager;
import com.nabalive.common.server.MessageService;
import com.nabalive.data.core.dao.NabaztagDAO;
import com.nabalive.data.core.model.Nabaztag;
import com.nabalive.framework.web.Request;
import com.nabalive.framework.web.Response;
import com.nabalive.framework.web.Route;
import com.nabalive.framework.web.SimpleRestHandler;
import com.nabalive.framework.web.exception.HttpException;
import com.nabalive.server.jabber.packet.AmbientPacket;
import com.nabalive.server.jabber.packet.SleepPacket;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 12/16/11
 */
@Component

public class ApiController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SimpleRestHandler restHandler;

    @Autowired
    private NabaztagDAO nabaztagDAO;

    @Autowired
    MessageService messageService;

    @PostConstruct
    void init() {
        restHandler
                .get(new Route("(?:/vl)?/:lang/api(?:_stream)?.jsp") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        String mac = checkNotNull(request.getParam("sn")).toLowerCase();
                        String token = checkNotNull(request.getParam("token")).toLowerCase();
                        String lang = checkNotNull(map.get("lang")).toLowerCase();
                        String tts = request.getParam("tts");
                        String urlList = request.getParam("urlList");
                        String color = request.getParam("color");
                        String posleft = request.getParam("posleft");
                        String posright = request.getParam("posright");
                        String action = request.getParam("action");
                        String chor = request.getParam("chor");

                        String host = request.request.getHeader("Host");

                        Nabaztag nabaztag;
                        try{
                            nabaztag = checkNotNull(nabaztagDAO.findOne("macAddress", mac));
                        }catch(NullPointerException e){
                            throw new HttpException(HttpResponseStatus.UNAUTHORIZED, "mac address doesn't exists");
                        }
                        
                        if (!token.equalsIgnoreCase(nabaztag.getApikey())) {
                            throw new HttpException(HttpResponseStatus.UNAUTHORIZED, "token is not valid");
                        }

                        StringBuilder commands = new StringBuilder();
                        if(chor != null){
                            String url = "http://" + host + "/api/chor?data=" + chor;
                            commands.append("CH " + url + "\n");
                        }
                        if (tts != null) {
                            String encodedText = URLEncoder.encode(tts, "UTF-8");
                            String url = "http://" + host + "/tts/" + lang + "?text=" + encodedText;
                            commands.append("ST " + url + "\nMW\n");
                        }
                        if (urlList != null) {
                            for (String url : urlList.split("\\|")) {
                                commands.append("ST " + url + "\nMW\n");
                            }
                        }

                        if (posleft != null || posright != null) {
                            AmbientPacket packet = new AmbientPacket();
                            if (posleft != null) {
                                packet.add(AmbientPacket.MoveLeftEar, Integer.parseInt(posleft));
                            }
                            if (posright != null) {
                                packet.add(AmbientPacket.MoveRightEar, Integer.parseInt(posright));
                            }
                            messageService.sendMessage(mac, packet);
                        }
                        if (action != null) {
                            switch (Integer.parseInt(action)) {
                                case 13:
                                    messageService.sendMessage(mac, new SleepPacket(SleepPacket.Action.WakeUp));
                                    break;
                                case 14:
                                    messageService.sendMessage(mac, new SleepPacket(SleepPacket.Action.Sleep));
                                    break;
                            }
                        }

                        if(commands.length() > 0)
                            messageService.sendMessage(mac, commands.toString());

                        response.writeXML("ok");

                    }
                });
    }


}
