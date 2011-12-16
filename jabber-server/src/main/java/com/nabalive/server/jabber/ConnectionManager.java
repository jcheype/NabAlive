package com.nabalive.server.jabber;

import com.nabalive.common.server.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/16/11
 */

@Component
public class ConnectionManager {
    private final Map<String, Status> connectionMapByMac = new HashMap<String, Status>();
    
    private final List<EventListener> eventListeners = new ArrayList<EventListener>();

    public int size() {
        return connectionMapByMac.size();
    }

    public boolean isEmpty() {
        return connectionMapByMac.isEmpty();
    }

    public boolean containsKey(Object key) {
        return connectionMapByMac.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return connectionMapByMac.containsValue(value);
    }

    public Status get(Object key) {
        return connectionMapByMac.get(key);
    }

    public Status put(String key, Status value) {
        return connectionMapByMac.put(key, value);
    }

    public Status remove(Object key) {
        return connectionMapByMac.remove(key);
    }

    public void putAll(Map<? extends String, ? extends Status> m) {
        connectionMapByMac.putAll(m);
    }

    public void clear() {
        connectionMapByMac.clear();
    }

    public Set<String> keySet() {
        return connectionMapByMac.keySet();
    }

    public Collection<Status> values() {
        return connectionMapByMac.values();
    }

    public Set<Map.Entry<String, Status>> entrySet() {
        return connectionMapByMac.entrySet();
    }

    public void addEventListener(EventListener eventListener){
        eventListeners.add(eventListener);
    }

    public List<EventListener> getEventListeners() {
        return eventListeners;
    }
}
