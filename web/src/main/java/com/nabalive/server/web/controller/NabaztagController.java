package com.nabalive.server.web.controller;

import com.google.common.base.CharMatcher;
import com.nabalive.common.server.MessageService;
import com.nabalive.data.core.dao.NabaztagDAO;
import com.nabalive.data.core.model.Nabaztag;
import com.nabalive.framework.web.Request;
import com.nabalive.framework.web.Response;
import com.nabalive.framework.web.Route;
import com.nabalive.framework.web.SimpleRestHandler;
import com.nabalive.server.jabber.ConnectionManager;
import com.nabalive.server.web.Token;
import com.nabalive.server.web.TokenUtil;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/20/11
 */
@Component
public class NabaztagController {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

    @Autowired
    private SimpleRestHandler restHandler;

    @Autowired
    NabaztagDAO nabaztagDAO;

    @Autowired
    ConnectionManager connectionManager;

    @Autowired
    MessageService messageService;

    @PostConstruct
    void init() {
        restHandler.get(new Route("/nabaztags") {
            @Override
            public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                Token token = TokenUtil.decode(checkNotNull(request.getParamOrHeader("token")), Token.class);

                List<Nabaztag> nabaztagList = nabaztagDAO.find(nabaztagDAO.createQuery().filter("owner", token.getUserId())).asList();
                for(Nabaztag nabaztag: nabaztagList){
                    if(connectionManager.containsKey(nabaztag.getMacAddress())){
                        nabaztag.setConnected(true);
                    }
                }
                response.writeJSON(nabaztagList);
            }
        })
                .post(new Route("/nabaztags", ".*/json") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        Token token = TokenUtil.decode(checkNotNull(request.getParamOrHeader("token")), Token.class);
                        logger.debug("received json: {}", request.content);
                        Map<String, String> nabMap = mapper.readValue(request.content, Map.class);

                        Nabaztag nabaztag = new Nabaztag();
                        nabaztag.setMacAddress(nabMap.get("mac"));
                        nabaztag.setName(nabMap.get("name"));
                        nabaztag.setApikey(UUID.randomUUID().toString());
                        nabaztag.setOwner(token.getUserId());

                        nabaztagDAO.save(nabaztag);

                        response.writeJSON(nabaztag);
                    }
                })
                .post(new Route("/nabaztags") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        Token token = TokenUtil.decode(checkNotNull(request.getParamOrHeader("token")), Token.class);
                        String mac = checkNotNull(request.getParam("mac"));
                        String name = request.getParam("name");

                        Nabaztag nabaztag = new Nabaztag();
                        nabaztag.setMacAddress(mac);
                        nabaztag.setName(name);
                        nabaztag.setApikey(UUID.randomUUID().toString());
                        nabaztag.setOwner(token.getUserId());

                        nabaztagDAO.save(nabaztag);

                        response.writeJSON(nabaztag);
                    }
                })
                .delete(new Route("/nabaztags/:id") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        Token token = TokenUtil.decode(checkNotNull(request.getParamOrHeader("token")), Token.class);

                        String id = checkNotNull(map.get("id"));
                        Nabaztag nabaztag = checkNotNull(nabaztagDAO.get(new ObjectId(id)));
                        if (token.getUserId().equals(nabaztag.getOwner())) {
                            nabaztagDAO.delete(nabaztag);
                            response.writeJSON("ok");
                        } else {
                            response.write(new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED));
                        }
                    }
                })
                .get(new Route("/nabaztags/:apikey/play") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        String apikey = checkNotNull(map.get("apikey"));
                        String url = checkNotNull(request.getParam("url"));
                        Nabaztag nabaztag = checkNotNull(nabaztagDAO.findOne("apikey", apikey));

                        String urlSanitized = CharMatcher.isNot('\n').retainFrom(url);

                        Random randomGenerator = new Random();
                        String commande = "ST " + urlSanitized + "\nPL " + randomGenerator.nextInt(8) + "\nMW\n";
                        logger.debug("COMMAND: " + commande);
                        messageService.sendMessage(nabaztag.getMacAddress(), commande);
                        response.writeJSON("ok");
                    }
                });
    }
}
