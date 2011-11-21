package com.nabalive.common.server;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/17/11
 */
public class Event {
    public enum Type {
        PRESENCE, BUTTON, EARS, UNKNOWN_MESSAGE
    }

    public final String content;
    public final Type type;

    public Event(String content, Type type) {
        this.content = content;
        this.type = type;
    }
}
