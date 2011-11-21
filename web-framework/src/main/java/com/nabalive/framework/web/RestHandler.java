package com.nabalive.framework.web;

import org.jboss.netty.handler.codec.http.HttpMethod;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 10/20/11
 */
public interface RestHandler {
    public static final HttpMethod beforeFilter = new HttpMethod("BEFORE_FILTER");
    public static final HttpMethod afterFilter = new HttpMethod("AFTER_FILTER");

    List<Route> getRouteList(HttpMethod method);
}
