package com.nabalive.server.web.controller;

import com.nabalive.data.core.dao.UserDAO;
import com.nabalive.data.core.model.User;
import com.nabalive.framework.web.Request;
import com.nabalive.framework.web.Response;
import com.nabalive.framework.web.Route;
import com.nabalive.framework.web.SimpleRestHandler;
import com.nabalive.server.web.Token;
import com.nabalive.server.web.TokenUtil;
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
 * Date: 11/19/11
 */

@Component
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SimpleRestHandler restHandler;

    @Autowired
    private UserDAO userDAO;

    @PostConstruct
    void init() {
        restHandler
                .post(new Route("/user/login") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        String email = checkNotNull(request.getParam("email"));
                        String password = checkNotNull(request.getParam("password"));
                        User user = checkNotNull(userDAO.findOne("email", email));
                        user.checkPassword(password);

                        Token token = new Token();
                        token.setUserId(user.getId());

//                        Map result = new HashMap();
//                        result.put("token", TokenUtil.encode(token));

                        response.writeJSON(TokenUtil.encode(token));
                    }
                })
                .post(new Route("/user/register") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        String firstname = checkNotNull(request.getParam("firstname"));
                        String lastname = checkNotNull(request.getParam("lastname"));
                        String email = checkNotNull(request.getParam("email"));
                        String password = checkNotNull(request.getParam("password"));

                        User user = new User();
                        user.setFirstname(firstname);
                        user.setLastname(lastname);
                        user.setEmail(email);
                        user.setPassword(password);

                        userDAO.save(user);
                        logger.info("new user: {}", user.getId());

                        Token token = new Token();
                        token.setUserId(user.getId());
                        response.writeJSON(TokenUtil.encode(token));
                    }
                })
                .get(new Route("/user/info") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        String tokenString = checkNotNull(request.getParamOrHeader("token"));
                        Token token = TokenUtil.decode(tokenString, Token.class);

                        User user = checkNotNull(userDAO.get(token.getUserId()));
                        response.writeJSON(user);
                    }
                })
                .get(new Route("/user/test") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        response.write("ok");
                    }
                });
    }
}


