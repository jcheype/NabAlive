package com.nabalive.data.core.model;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/16/11
 */

@Entity("application")
public class ApplicationStore {
    static enum Triggers{
        RFID, ASR, PERMANENT
    }

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    @NotNull
    private String apikey;

    @Indexed(unique = true)
    @NotNull
    private String name;

    private String description;

    private String asrName;

    @Embedded
    private List<Field> fields = new ArrayList<Field>();

    private List<Triggers> triggers = new ArrayList<Triggers>();

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
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

    public List<Triggers> getTriggers() {
        return triggers;
    }

    public String getAsrName() {
        return asrName;
    }

    public void setAsrName(String asrName) {
        this.asrName = asrName;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
