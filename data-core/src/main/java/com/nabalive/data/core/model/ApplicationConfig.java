package com.nabalive.data.core.model;

import com.google.code.morphia.annotations.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


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

    
    @Reference(lazy = true)
    private Application application;

    Map<String, String> parameters = new HashMap<String, String>();

    synchronized public String getUuid() {
        if(uuid == null)
            uuid = UUID.randomUUID().toString();
        return uuid;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
