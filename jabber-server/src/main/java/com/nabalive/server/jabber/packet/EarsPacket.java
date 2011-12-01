package com.nabalive.server.jabber.packet;

import com.nabalive.common.server.Packet;

import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/25/11
 */
public class EarsPacket implements Packet {
    private final int left;
    private final int right;

    public EarsPacket(int left, int right) {
        this.left = left;
        this.right = right;
    }

    public String getXmpp(String from, String to) {
        StringBuilder msg = new StringBuilder();
        msg.append("<message from='" + from + "' ");
        msg.append("to='" + to + "' ");
        msg.append("id='" + UUID.randomUUID().toString() + "'>");
        msg.append("<packet xmlns='violet:packet' format='1.0' ttl='604800'>");
        msg.append("<ears xmlns=\"violet:nabaztag:ears\"><left>"+left+"</left><right>"+right+"</right></ears>");
        msg.append("</packet></message>");
        return msg.toString();
    }

}
