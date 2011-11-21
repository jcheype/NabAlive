package com.nabalive.server.web;

import org.bson.types.ObjectId;
import org.msgpack.annotation.Message;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/20/11
 */

@Message
public class Token {
    byte[] userIdBytes;

    public ObjectId getUserId() {
        return new ObjectId(userIdBytes);
    }

    public void setUserId(ObjectId userId) {
        this.userIdBytes = userId.toByteArray();
    }
}
