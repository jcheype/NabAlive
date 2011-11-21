package com.nabalive.server.jabber.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/15/11
 */
public class Jid {
    Pattern jidPattern = Pattern.compile("([^@]*)@([^/]*)/(.*)");

    String user = null;
    String server = null;
    String resource = null;

    public Jid(String jid) {
        final Matcher fromMatcher = jidPattern.matcher(jid);

        if (fromMatcher.matches()) {
            user = fromMatcher.group(1);
            server = fromMatcher.group(2);
            resource = fromMatcher.group(3);
        }
    }

    public String getUser() {
        return user;
    }

    public String getServer() {
        return server;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    @Override
    public String toString() {
        String result = user +"@"+ server + "/";
        if(resource != null)
            result += resource;
        return result;
    }
}
