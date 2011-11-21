package com.nabalive.data.core.model;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/16/11
 */

@Entity("application")
public class Application {
    @Id
    private ObjectId apikey;

    @Indexed(unique = true)
    @NotNull
    private String name;
    
    private String description;

    public ObjectId getApikey() {
        return apikey;
    }

    public void setApikey(ObjectId apikey) {
        this.apikey = apikey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
