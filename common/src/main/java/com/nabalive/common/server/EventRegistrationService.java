package com.nabalive.common.server;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/17/11
 */
public interface EventRegistrationService {
    void register(String id, EventListener eventListener) throws IllegalArgumentException;
}
