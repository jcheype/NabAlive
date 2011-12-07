package com.nabalive.data.core.model;

import com.google.code.morphia.annotations.CappedAt;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 12/7/11
 */

@Entity(cap = @CappedAt(104857600), value = "tmpData")
public class TmpData {
    @Id
    private ObjectId id;

    @NotNull
    private Map<String, String> infos = new HashMap<String, String>();

    @NotNull
    private byte[] data;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Map<String, String> getInfos() {
        return infos;
    }

    public void setInfos(Map<String, String> infos) {
        this.infos = infos;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
