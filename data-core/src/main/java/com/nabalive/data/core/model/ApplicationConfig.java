package com.nabalive.data.core.model;

import com.google.code.morphia.annotations.*;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotNull;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/16/11
 */

@Embedded
public class ApplicationConfig {
    @Indexed()
    @NotNull
    private String uuid;

    @Indexed()
    @NotNull
    private String applicationStoreApikey;

    private String appName;
    private String name;

    Map<String, List<String>> parameters = new HashMap<String, List<String>>();

    private List<String> tags = new ArrayList<String>();

    synchronized public String getUuid() {
        return uuid;
    }

    public String getApplicationStoreApikey() {
        return applicationStoreApikey;
    }

    public void setApplicationStoreApikey(String applicationStoreApikey) {
        this.applicationStoreApikey = applicationStoreApikey;
    }

    public Map<String, List<String>> getParameters() {
        return parameters;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PrePersist
    public void prePersist() {
        if(uuid == null)
            uuid = UUID.randomUUID().toString();
    }

}
