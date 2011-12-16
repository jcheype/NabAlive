package com.nabalive.data.core.model;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/16/11
 */

@Entity("user")
public class User {

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    @Email
    @Size(min=5)
    @NotNull
    String email;

    @Indexed
    @NotNull
    String lastname;

    @Indexed
    @NotNull
    String firstname;

    @NotNull
    byte[] password;

    @JsonIgnore
    String resetId;

    @JsonIgnore
    @NotNull
    List<String> permissions = new ArrayList<String>();


    public ObjectId getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest((email + password).getBytes(Charset.forName("UTF-8")));
        this.password = digest;
    }

    public byte[] getPassword() {
        return password;
    }

    public void checkPassword(String passwordToCheck) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest((email + passwordToCheck).getBytes(Charset.forName("UTF-8")));
        if (!Arrays.equals(password, digest))
            throw new IllegalArgumentException("bad password");
    }

    public String getResetId() {
        return resetId;
    }

    public void setResetId(String resetId) {
        this.resetId = resetId;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", lastname='" + lastname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", password=" + password +
                ", resetId='" + resetId + '\'' +
                ", permissions=" + permissions +
                '}';
    }
}
