package com.nabalive.server.web.controller;

import com.nabalive.data.core.dao.UserDAO;
import com.nabalive.data.core.model.User;
import com.nabalive.framework.web.Request;
import com.nabalive.framework.web.Response;
import com.nabalive.framework.web.Route;
import com.nabalive.framework.web.SimpleRestHandler;
import com.nabalive.framework.web.exception.HttpException;
import com.nabalive.server.jabber.ConnectionManager;
import com.nabalive.server.web.Token;
import com.nabalive.server.web.TokenUtil;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
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
 * Date: 12/15/11
 */

@Component
public class AdminController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SimpleRestHandler restHandler;

    @Autowired
    private ConnectionManager connectionManager;

    @Autowired
    UserDAO userDAO;

    @PostConstruct
    void init() {
        restHandler
                .get(new Route("/admin/connected/infos") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        Infos infos = new Infos(connectionManager.size());
                        response.writeJSON(infos);
                    }
                })
                .get(new Route("/admin/connected/list") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        Token token = TokenUtil.decode(checkNotNull(request.getParamOrHeader("token")), Token.class);
                        User user = checkNotNull(userDAO.get(token.getUserId()));
                        if(!user.getPermissions().contains("admin"))
                            throw new HttpException(HttpResponseStatus.UNAUTHORIZED, "UNAUTHORIZED access");

                        response.writeJSON(connectionManager.keySet());
                    }
                });
    }
    
    class Infos{
        public final int connected;

        Infos(int connected) {
            this.connected = connected;
        }
    }
}
