package com.nabalive.framework.web.exception;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 10/28/11
 */
public class HttpException extends Exception {
    private final HttpResponseStatus status;

    public HttpException(HttpResponseStatus status) {
        this.status = status;
    }

    public HttpException(HttpResponseStatus status, String s) {
        super(s);
        this.status = status;
    }

    public HttpResponseStatus getStatus() {
        return status;
    }
}
