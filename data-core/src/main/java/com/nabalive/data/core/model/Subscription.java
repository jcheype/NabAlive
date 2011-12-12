package com.nabalive.data.core.model;

import com.google.code.morphia.annotations.Embedded;

import javax.validation.constraints.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 12/9/11
 */

@Embedded
public class Subscription {
    @NotNull
    private String ownerLastName;
    @NotNull
    private String ownerFisrtName;
    @NotNull
    private String name;
    @NotNull
    private String objectId;

    public String getOwnerLastName() {
        return ownerLastName;
    }

    public void setOwnerLastName(String ownerLastName) {
        this.ownerLastName = ownerLastName;
    }

    public String getOwnerFisrtName() {
        return ownerFisrtName;
    }

    public void setOwnerFisrtName(String ownerFisrtName) {
        this.ownerFisrtName = ownerFisrtName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subscription that = (Subscription) o;

        if (!name.equals(that.name)) return false;
        if (!objectId.equals(that.objectId)) return false;
        if (!ownerFisrtName.equals(that.ownerFisrtName)) return false;
        if (!ownerLastName.equals(that.ownerLastName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ownerLastName.hashCode();
        result = 31 * result + ownerFisrtName.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + objectId.hashCode();
        return result;
    }
}
