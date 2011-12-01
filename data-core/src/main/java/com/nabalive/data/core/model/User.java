package com.nabalive.data.core.model;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotNull;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


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


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", lastname='" + lastname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", password=" + password +
                '}';
    }
}
