package com.nabalive.server.web;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 12/13/11
 */

public class Format {

    static public String get(String key, String... args){
        ResourceBundle bundle = ResourceBundle.getBundle("lang.default");
        String fmt = bundle.getString(key);
        if(args.length > 0){
            return String.format(fmt, args);
        }
        return fmt;
    }

    static public String get(String baseName, Locale locale, String key, Object... args){
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
        String fmt = bundle.getString(key);
        if(args.length > 0){
            return String.format(fmt, args);
        }
        return fmt;
    }
}
