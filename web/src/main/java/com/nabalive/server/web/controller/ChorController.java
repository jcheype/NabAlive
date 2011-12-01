package com.nabalive.server.web.controller;

import com.nabalive.data.core.dao.NabaztagDAO;
import com.nabalive.framework.web.Request;
import com.nabalive.framework.web.Response;
import com.nabalive.framework.web.Route;
import com.nabalive.framework.web.SimpleRestHandler;
import com.nabalive.server.web.ChorBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Map;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/25/11
 */
public class ChorController {
        private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SimpleRestHandler restHandler;

    @PostConstruct
    void init() {
        restHandler
                .get(new Route("/api/chor") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        String chor = checkNotNull(request.getParam("data"));
                        int loop = Integer.parseInt(firstNonNull(request.getParam("loop"), "1"));
                        ChorBuilder chorBuilder = new ChorBuilder(chor);
                        response.write(chorBuilder.build(loop));
                    }
                })
                .get(new Route("/api/chor/ears") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        ChorBuilder chorBuilder = new ChorBuilder();
                        String left = request.getParam("left");
                        String right = request.getParam("right");

                        if (left != null) {
                            int val = Integer.parseInt(left);
                            chorBuilder.setEar((byte) 0, (byte) (val > 0 ? 0 : 1), (byte) Math.abs(val));
                        }
                        if (right != null) {
                            int val = Integer.parseInt(right);
                            chorBuilder.setEar((byte) 1, (byte) (val > 0 ? 0 : 1), (byte) Math.abs(val));
                        }
                        response.write(chorBuilder.build());
                    }
                })
                .get(new Route("/api/chor/led/:led/:color") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        ChorBuilder chorBuilder = new ChorBuilder();
                        String color = checkNotNull(map.get("color"));
                        int led = Integer.parseInt(checkNotNull(map.get("led")));
                        chorBuilder.setLed((byte)led, color);
                        response.write(chorBuilder.build());
                    }
                });
    }
}
