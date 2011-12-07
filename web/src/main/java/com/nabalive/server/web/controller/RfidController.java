package com.nabalive.server.web.controller;

import com.nabalive.application.core.Application;
import com.nabalive.application.core.ApplicationManager;
import com.nabalive.data.core.dao.ApplicationStoreDAO;
import com.nabalive.data.core.dao.NabaztagDAO;
import com.nabalive.data.core.model.ApplicationConfig;
import com.nabalive.data.core.model.Nabaztag;
import com.nabalive.framework.web.Request;
import com.nabalive.framework.web.Response;
import com.nabalive.framework.web.Route;
import com.nabalive.framework.web.SimpleRestHandler;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
                        String tag = checkNotNull(request.getParam("t")).toLowerCase();
                        String host = request.request.getHeader("Host");

                        Nabaztag nabaztag = checkNotNull(nabaztagDAO.findOne("macAddress", mac));
                        if (!nabaztag.getTags().contains(tag)) {
                            nabaztag.getTags().add(tag);
                            nabaztagDAO.save(nabaztag);
                            nabaztagController.tts(nabaztag, host, "fr", "Nouveau tag ajouté");
                        } else {
                            for (ApplicationConfig applicationConfig : nabaztag.getApplicationConfigList()) {
                                if (applicationConfig.getTags().contains(tag)) {
                                    String apikey = applicationConfig.getApplicationStoreApikey();
                                    Application application = applicationManager.getApplication(apikey);
                                    application.onStartup(nabaztag, applicationConfig);
                                    response.write("ok");
                                    return;
                                }
                            }
                            nabaztagController.tts(nabaztag, host, "fr", "Aucune application associée au tag");
                        }
                        response.write("ok");
                    }
                });
    }
}
