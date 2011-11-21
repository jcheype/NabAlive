package com.nabalive.server.web;

import org.junit.Test;
import org.msgpack.annotation.Message;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/19/11
 */


public class TokenTest {

//    @Test
//    public void testGenToken() throws NoSuchAlgorithmException {
//        String token = TokenUtil.genToken("1234567890-" + System.currentTimeMillis());
//        System.out.println(token);
//        assertTrue(TokenUtil.tokenPattern.matcher(token).matches());
//    }
//
//    @Test
//    public void testCheckToken() throws Exception {
//        String token = TokenUtil.genToken("1234567890-" + System.currentTimeMillis());
//        TokenUtil.checkToken(token);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void testCheckTokenException() throws Exception {
//        String token = TokenUtil.genToken("1234567890-" + System.currentTimeMillis());
//        String badToken = token.replace('0', '1');
//        System.out.println(badToken);
//        TokenUtil.checkToken(badToken);
//    }

    @Test
    public void testEncodeDecode() throws Exception {
        Msg original = new Msg();
        original.setTitle("toto");
        original.setContent("cool ca marche?");
        original.setSize(-1);


        String encoded = TokenUtil.encode(original);
        System.out.println("encoded: "+encoded);
        Msg decoded = TokenUtil.decode(encoded, Msg.class);


        assertEquals(original, decoded);

        System.out.println(decoded.toString());
    }

    @Message
    public static class Msg{
        String title;
        String content;
        int size;

        public void setTitle(String title) {
            this.title = title;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setSize(int size) {
            this.size = size;
        }

        @Override
        public String toString() {
            return "Message{" +
                    "title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    ", size=" + size +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Msg msg = (Msg) o;

            if (size != msg.size) return false;
            if (content != null ? !content.equals(msg.content) : msg.content != null) return false;
            if (title != null ? !title.equals(msg.title) : msg.title != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = title != null ? title.hashCode() : 0;
            result = 31 * result + (content != null ? content.hashCode() : 0);
            result = 31 * result + size;
            return result;
        }
    }
}
