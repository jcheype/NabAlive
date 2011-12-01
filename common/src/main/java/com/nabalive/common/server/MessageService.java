package com.nabalive.common.server;

import java.util.concurrent.ExecutionException;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/17/11
 */
public interface MessageService {
    void sendMessage(String to, String message) throws ExecutionException;
    void sendMessage(String to, Packet message) throws ExecutionException;
}
