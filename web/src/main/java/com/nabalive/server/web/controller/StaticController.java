package com.nabalive.server.web.controller;

import com.nabalive.framework.web.HttpStaticFileServerHandler;
import com.nabalive.framework.web.SimpleRestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/20/11
 */
@Component
public class StaticController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final HttpStaticFileServerHandler staticFileServerHandler = new HttpStaticFileServerHandler(System.getProperty("public","./web-front/public"));

    @Autowired
    private SimpleRestHandler restHandler;

    @PostConstruct
    public void init() {
        restHandler.staticServe("/", staticFileServerHandler);
        restHandler.staticServe("/index.html", staticFileServerHandler);
        restHandler.staticServe("/index2.html", staticFileServerHandler);
        restHandler.staticServe("/assets/.*", staticFileServerHandler);
        restHandler.staticServe("/image/.*", staticFileServerHandler);
        restHandler.staticServe("/css/.*", staticFileServerHandler);
    }
}
