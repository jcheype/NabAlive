package com.nabalive.server.web.controller;

import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.nabalive.common.server.MessageService;
import com.nabalive.data.core.dao.NabaztagDAO;
import com.nabalive.data.core.dao.UserDAO;
import com.nabalive.data.core.model.ApplicationConfig;
import com.nabalive.data.core.model.Nabaztag;
import com.nabalive.data.core.model.Subscription;
import com.nabalive.data.core.model.User;
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
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

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
    UserDAO userDAO;

    @Autowired
    ConnectionManager connectionManager;

    @Autowired
    MessageService messageService;

    @PostConstruct
    void init() {
        restHandler
                .get(new Route("/nabaztags") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        Token token = TokenUtil.decode(checkNotNull(request.getParamOrHeader("token")), Token.class);

                        List<Nabaztag> nabaztagList = nabaztagDAO.find(nabaztagDAO.createQuery().filter("owner", token.getUserId())).asList();
                        for (Nabaztag nabaztag : nabaztagList) {
                            if (connectionManager.containsKey(nabaztag.getMacAddress())) {
                                nabaztag.setConnected(true);
                            }
                        }
                        response.writeJSON(nabaztagList);
                    }
                })
                .post(new Route("/nabaztags", ".*/json.*") {
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
                .post(new Route("/nabaztags/:mac/addconfig") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        Token token = TokenUtil.decode(checkNotNull(request.getParamOrHeader("token")), Token.class);
                        List<String> tags = request.parameters.get("rfid");
                        String appApikey = checkNotNull(request.getParam("apikey"));
                        String name = checkNotNull(request.getParam("name"));
                        String appName = checkNotNull(request.getParam("appName"));
                        String uuid = request.getParam("uuid");
                        String mac = checkNotNull(map.get("mac"));
                        Nabaztag nabaztag = checkNotNull(nabaztagDAO.findOne("macAddress", mac));

                        if (!nabaztag.getOwner().equals(token.getUserId()))
                            throw new IllegalArgumentException();

                        Iterator<ApplicationConfig> iterator = nabaztag.getApplicationConfigList().iterator();
                        while (iterator.hasNext()) {
                            ApplicationConfig next = iterator.next();
                            if (tags != null)
                                next.getTags().removeAll(tags);
                            if (next.getUuid().equals(uuid))
                                iterator.remove();
                        }
                        ApplicationConfig config = new ApplicationConfig();
                        config.setApplicationStoreApikey(appApikey);
                        config.getTags().clear();
                        if (tags != null)
                            config.getTags().addAll(tags);

                        for (Map.Entry<String, List<String>> entry : request.parameters.entrySet()) {
                            if (entry.getKey().startsWith("parameter.")) {
                                String key = entry.getKey().substring("parameter.".length());
                                config.getParameters().put(key, entry.getValue());
                            }
                        }

                        config.setName(name);
                        config.setAppName(appName);

                        nabaztag.addApplicationConfig(config);

                        nabaztagDAO.save(nabaztag);
                        response.writeJSON(nabaztag);
                    }
                })
                .delete(new Route("/config/:uuid") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        Token token = TokenUtil.decode(checkNotNull(request.getParamOrHeader("token")), Token.class);
                        String uuid = checkNotNull(map.get("uuid"));
                        Nabaztag nabaztag = checkNotNull(nabaztagDAO.findOne("applicationConfigList.uuid", uuid));
                        if (!nabaztag.getOwner().equals(token.getUserId()))
                            throw new IllegalArgumentException();

                        Iterator<ApplicationConfig> iterator = nabaztag.getApplicationConfigList().iterator();
                        while (iterator.hasNext()) {
                            ApplicationConfig next = iterator.next();
                            if (next.getUuid().equals(uuid))
                                iterator.remove();
                        }
                        nabaztagDAO.save(nabaztag);
                        response.writeJSON(nabaztag);
                    }
                })
                .get(new Route("/nabaztags/:mac") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        Token token = TokenUtil.decode(checkNotNull(request.getParamOrHeader("token")), Token.class);

                        String mac = checkNotNull(map.get("mac"));
                        Nabaztag nabaztag = checkNotNull(nabaztagDAO.findOne("macAddress", mac));
                        if (token.getUserId().equals(nabaztag.getOwner())) {
                            response.writeJSON(nabaztag);
                        } else {
                            response.write(new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED));
                        }
                    }
                })
                .delete(new Route("/nabaztags/:mac") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        Token token = TokenUtil.decode(checkNotNull(request.getParamOrHeader("token")), Token.class);

                        String mac = checkNotNull(map.get("mac"));
                        Nabaztag nabaztag = checkNotNull(nabaztagDAO.findOne("macAddress", mac));
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
                        List<String> urlList = checkNotNull(request.qs.getParameters().get("url"));
                        Nabaztag nabaztag = checkNotNull(nabaztagDAO.findOne("apikey", apikey));

                        List<String> urlListSanitized = Lists.transform(urlList, new Function<String, String>() {
                            @Override
                            public String apply(@Nullable String url) {
                                return CharMatcher.isNot('\n').retainFrom(url);
                            }
                        });
                        StringBuilder commands = new StringBuilder();
                        for (String url : urlListSanitized) {
                            commands.append("ST " + url + "\nMW\n");
                        }
                        logger.debug("COMMAND: {}", commands);
                        messageService.sendMessage(nabaztag.getMacAddress(), commands.toString());
                        response.writeJSON("ok");
                    }
                })
                .get(new Route("/nabaztags/:apikey/tts/:voice") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        String apikey = checkNotNull(map.get("apikey"));
                        String text = checkNotNull(request.getParam("text"));
                        String voice = checkNotNull(map.get("voice"));
                        Nabaztag nabaztag = checkNotNull(nabaztagDAO.findOne("apikey", apikey));

                        String host = request.request.getHeader("Host");
                        tts(nabaztag, host, voice, text);

                        response.writeJSON("ok");
                    }
                })
                .get(new Route("/nabaztags/:apikey/exec") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        String apikey = checkNotNull(map.get("apikey"));
                        String command = checkNotNull(request.getParam("command"));
                        Nabaztag nabaztag = checkNotNull(nabaztagDAO.findOne("apikey", apikey));

                        logger.debug("COMMAND: {}", command);
                        messageService.sendMessage(nabaztag.getMacAddress(), command);
                        response.writeJSON("ok");
                    }
                })
                .get(new Route("/nabaztags/:apikey/subscribe") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        String apikey = checkNotNull(map.get("apikey"));
                        String email = checkNotNull(request.getParam("email"));
                        Nabaztag nabaztag = checkNotNull(nabaztagDAO.findOne("apikey", apikey));
                        Set<Subscription> subscriptionSet = nabaztag.getSubscribe();

                        User user = checkNotNull(userDAO.findOne("email", email));
                        Query<Nabaztag> query = nabaztagDAO.createQuery().filter("owner", user.getId());
                        for (Nabaztag nab : nabaztagDAO.find(query).asList()) {
                            Subscription subscription = new Subscription();
                            subscription.setName(nab.getName());
                            subscription.setOwnerFisrtName(user.getFirstname());
                            subscription.setOwnerLastName(user.getLastname());
                            subscription.setObjectId(nab.getId().toString());

                            subscriptionSet.add(subscription);
                        }

                        UpdateOperations<Nabaztag> updateOperations = nabaztagDAO.createUpdateOperations().set("subscribe", subscriptionSet);
                        nabaztagDAO.update(nabaztagDAO.createQuery().filter("_id", nabaztag.getId()), updateOperations);
                        response.writeJSON("ok");
                    }
                })
                .delete(new Route("/nabaztags/:apikey/subscribe/:objectId") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        String apikey = checkNotNull(map.get("apikey"));
                        String objectId = checkNotNull(map.get("objectId"));
                        Nabaztag nabaztag = checkNotNull(nabaztagDAO.findOne("apikey", apikey));
                        Set<Subscription> subscriptionSet = nabaztag.getSubscribe();

                        Iterator<Subscription> iterator = subscriptionSet.iterator();
                        while (iterator.hasNext()) {
                            Subscription next = iterator.next();
                            if (next.getObjectId().equals(objectId))
                                iterator.remove();
                        }


                        UpdateOperations<Nabaztag> updateOperations = nabaztagDAO.createUpdateOperations().set("subscribe", subscriptionSet);
                        nabaztagDAO.update(nabaztagDAO.createQuery().filter("_id", nabaztag.getId()), updateOperations);
                        response.writeJSON("ok");
                    }
                })
                .get(new Route("/nab2nabs/:apikey/send") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        String apikey = checkNotNull(map.get("apikey"));
                        String url = checkNotNull(request.getParam("url"));
                        Nabaztag nabaztag = checkNotNull(nabaztagDAO.findOne("apikey", apikey));
                        Set<Subscription> subscriptionSet = nabaztag.getSubscribe();

                        List<ObjectId> objectList = new ArrayList<ObjectId>();
                        for(Subscription subscription: subscriptionSet){
                            objectList.add(new ObjectId(subscription.getObjectId()));
                        }
                        List<Nabaztag> nabaztagList = nabaztagDAO.find(nabaztagDAO.createQuery().field("_id").in(objectList)).asList();
                        
                        String command = "ST " + url + "\nMW\n";
                        for(Nabaztag nab : nabaztagList){
                            if(connectionManager.containsKey(nab.getMacAddress()))
                                messageService.sendMessage(nab.getMacAddress(), command);
                        }

                        response.writeJSON("ok");
                    }
                });
    }

    public void tts(Nabaztag nabaztag, String host, String voice, String text) throws Exception {
        StringBuilder commands = new StringBuilder();
        String encodedText = URLEncoder.encode(text, "UTF-8");
        String url = "http://" + host + "/tts/" + nabaztag.getApikey() + "/" + voice + "?text=" + encodedText;

        commands.append("MC " + url + "\nMW\n");

        logger.debug("COMMAND: {}", commands);
        messageService.sendMessage(nabaztag.getMacAddress(), commands.toString());
    }
}
