package com.nabalive.framework.web;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 10/20/11
 */
public class Route {
    private static final Logger logger = LoggerFactory.getLogger(Route.class);

    private final Pattern pattern;
    private final Pattern patternContentType;
    private final Pattern paramsFinder = Pattern.compile("/:([^/]*)");
    private final Map<String, Integer> names;

    public Route(String path) {
        this(path, null);
    }
    public Route(String path, String contentType) {
        if(contentType != null)
            patternContentType = Pattern.compile(contentType);
        else
            patternContentType = null;
        final HashMap<String, Integer> map = new HashMap<String, Integer>();

        int i = 1;
        final Matcher matcher = paramsFinder.matcher(path);

        while (matcher.find()) {
            String name = matcher.group(1);
            map.put(name, i++);
            logger.debug(name);
        }

        String newStringPattern = path.replaceAll("/(:[^/]*)", "/([^/]*)");
        logger.debug("new pattern: " + newStringPattern);

        pattern = Pattern.compile(newStringPattern);

        names = new ImmutableMap.Builder<String, Integer>()
                .putAll(map)
                .build();
    }

    public Map<String, String> parse(String path, String contentType) {
        if(contentType != null && patternContentType != null){
            Matcher contentTypeMatcher = patternContentType.matcher(contentType);
            if(!contentTypeMatcher.matches()){
                return null;
            }
        }
        final Matcher matcher = pattern.matcher(path);
        if (!matcher.matches()) {
            return null;
        }
        Map<String, String> result = new HashMap<String, String>();
        for (Map.Entry<String, Integer> entry : names.entrySet()) {
            String value = matcher.group(entry.getValue());
            result.put(entry.getKey(), value);
        }
        return result;
    }

    public void handle(Request request, Response response, Map<String,String> map) throws Exception {
        throw new NotImplementedException();
    }

    @Override
    public String toString() {
        return "Route{" +
                "pattern=" + pattern +
                '}';
    }
}
