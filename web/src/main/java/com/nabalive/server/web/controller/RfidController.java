package com.nabalive.server.web.controller;

import com.google.common.collect.ImmutableList;
import com.nabalive.application.core.Application;
import com.nabalive.application.core.ApplicationManager;
import com.nabalive.common.server.MessageService;
import com.nabalive.data.core.dao.ApplicationStoreDAO;
import com.nabalive.data.core.dao.NabaztagDAO;
import com.nabalive.data.core.model.ApplicationConfig;
import com.nabalive.data.core.model.Nabaztag;
import com.nabalive.data.core.model.Tag;
import com.nabalive.framework.web.Request;
import com.nabalive.framework.web.Response;
import com.nabalive.framework.web.Route;
import com.nabalive.framework.web.SimpleRestHandler;
import com.nabalive.server.web.ConnectionWelcome;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.concurrent.Immutable;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/29/11
 */

@Component
public class RfidController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    MessageService messageService;

    @Autowired
    private SimpleRestHandler restHandler;

    @Autowired
    private ApplicationManager applicationManager;

    @Autowired
    private NabaztagDAO nabaztagDAO;

    @Autowired
    private ApplicationStoreDAO applicationStoreDAO;

    @Autowired
    private NabaztagController nabaztagController;

    @PostConstruct
    void init() {
        restHandler
                .get(new Route("/vl/rfid.jsp") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        String mac = checkNotNull(request.getParam("sn")).toLowerCase();
                        String tagValue = checkNotNull(request.getParam("t")).toLowerCase();
                        String host = request.request.getHeader("Host");

                        Nabaztag nabaztag = nabaztagDAO.findOne("macAddress", mac);
                        if(nabaztag == null){
                            messageService.sendMessage(mac, "ST " + ConnectionWelcome.WELCOME_URL + "\nMW\n");
                            response.write("ok");
                            return;
                        }

                        if (!nabaztag.hasTag(tagValue)) {
                            Tag tag = new Tag();
                            tag.setValue(tagValue);
                            tag.setName(tagValue);
                            nabaztag.getTags().add(tag);
                            nabaztagDAO.save(nabaztag);
                            nabaztagController.tts(nabaztag.getMacAddress(), host, "fr", "Nouveau tag ajouté");
                        } else {
                            for (ApplicationConfig applicationConfig : nabaztag.getApplicationConfigList()) {
                                if (applicationConfig.getTags().contains(tagValue)) {
                                    String apikey = applicationConfig.getApplicationStoreApikey();
                                    Application application = applicationManager.getApplication(apikey);

                                    applicationConfig.getParameters().put("__RFID__", (new ImmutableList.Builder<String>()).add(tagValue).build());
                                    application.onStartup(nabaztag, applicationConfig);
                                    response.write("ok");
                                    return;
                                }
                            }
                            nabaztagController.tts(nabaztag.getMacAddress(), host, "fr", "Aucune application associée au tag");
                        }
                        response.write("ok");
                    }
                });
    }
}
