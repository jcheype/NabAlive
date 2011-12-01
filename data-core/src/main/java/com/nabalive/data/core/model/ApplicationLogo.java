package com.nabalive.data.core.model;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

import javax.validation.constraints.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/30/11
 */

@Entity("applicationLogo")
public class ApplicationLogo {
    @Id
    private String apikey;

    @NotNull
    private byte[] data;

    private String filename;
    private String contentType;

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
