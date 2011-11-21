package com.nabalive.server.jabber;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/16/11
 */

@Component
public class ConnectionManager {
    private final Map<String, Status> connectionMap = new HashMap<String, Status>();

    public int size() {
        return connectionMap.size();
    }

    public boolean isEmpty() {
        return connectionMap.isEmpty();
    }

    public boolean containsKey(Object key) {
        return connectionMap.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return connectionMap.containsValue(value);
    }

    public Status get(Object key) {
        return connectionMap.get(key);
    }

    public Status put(String key, Status value) {
        return connectionMap.put(key, value);
    }

    public Status remove(Object key) {
        return connectionMap.remove(key);
    }

    public void putAll(Map<? extends String, ? extends Status> m) {
        connectionMap.putAll(m);
    }

    public void clear() {
        connectionMap.clear();
    }

    public Set<String> keySet() {
        return connectionMap.keySet();
    }

    public Collection<Status> values() {
        return connectionMap.values();
    }

    public Set<Map.Entry<String, Status>> entrySet() {
        return connectionMap.entrySet();
    }
}
