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

@Entity("nabaztag")
public class Nabaztag {
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    @NotNull
    private String macAddress;

    private String name;

    @Indexed
    private String apikey;

    private ObjectId owner;

    @Transient
    private boolean connected = false;

    @Embedded()
    List<ApplicationConfig> applicationConfigList = new ArrayList<ApplicationConfig>();

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addApplicationConfig(ApplicationConfig config) {
        applicationConfigList.add(config);
    }

    public void removeApplicationConfig(String uuid) {
        Iterator<ApplicationConfig> iterator = applicationConfigList.iterator();
        while (iterator.hasNext()) {
            ApplicationConfig applicationConfig = iterator.next();
            if (applicationConfig.getUuid().equals(uuid)) {
                iterator.remove();
                return;
            }
        }
    }

    public List<ApplicationConfig> getApplicationConfigList() {
        return applicationConfigList;
    }

    public ObjectId getOwner() {
        return owner;
    }

    public void setOwner(ObjectId owner) {
        this.owner = owner;
    }

    public void setOwner(String owner) {
        setOwner(new ObjectId(owner));
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getIdString() {
        return id.toString();
    }

    public String getOwnerIdString() {
        return owner.toString();
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
