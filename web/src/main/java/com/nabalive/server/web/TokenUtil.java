package com.nabalive.server.web;

import com.google.common.io.ByteStreams;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.iq80.snappy.SnappyInputStream;
import org.iq80.snappy.SnappyOutputStream;
import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/19/11
 */
public class TokenUtil {
    private static final Logger logger = LoggerFactory.getLogger(TokenUtil.class);

    private final static SecretKeySpec SECRET_KEY_SPEC;
    //    public final static Pattern tokenPattern = Pattern.compile("(.*)-([0-9a-f]+)");
    private static MessagePack messagePack = new MessagePack();

    private static final ThreadLocal<Cipher> cipherEncrypt =
            new ThreadLocal<Cipher>() {
                @Override
                protected Cipher initialValue() {
                    try {
                        Cipher cipher = Cipher.getInstance("RC4");
                        cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY_SPEC);

                        return cipher;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };

    private static final ThreadLocal<Cipher> cipherDecrypt =
            new ThreadLocal<Cipher>() {
                @Override
                protected Cipher initialValue() {
                    try {
                        Cipher cipher = Cipher.getInstance("RC4");
                        cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY_SPEC);

                        return cipher;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };

    static {
        String key = System.getProperty("SECRET_KEY");
        byte[] rawKey = null;
        if (key == null) {

            try {
                KeyGenerator kgen = KeyGenerator.getInstance("AES");
                kgen.init(128);
                SecretKey skey = kgen.generateKey();
                rawKey = skey.getEncoded();

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        } else {
            try {
                rawKey = Hex.decodeHex(key.toCharArray());
            } catch (DecoderException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("SECRET_KEY: " + Hex.encodeHexString(rawKey));
        }
        SECRET_KEY_SPEC = new SecretKeySpec(rawKey, "RC4");

    }

//    public static String genToken(String value) throws NoSuchAlgorithmException {
//        MessageDigest md = MessageDigest.getInstance("SHA-1");
//
//        md.update(value.getBytes(CharsetUtil.UTF_8));
//        md.update(SECRET_KEY_SPEC.getEncoded());
//
//        byte[] signBin = md.digest();
//        String sign = Hex.encodeHexString(signBin);
//
//        return value + "-" + sign;
//    }

    public static String encode(Object object) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {


        byte[] origBytes = messagePack.write(object);
        System.out.println("orig length: " + origBytes.length);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SnappyOutputStream snappyOutputStream = new SnappyOutputStream(out);
        snappyOutputStream.write(origBytes);
        snappyOutputStream.close();

        byte[] comressedBytes = out.toByteArray();
        System.out.println("comressedBytes length: " + comressedBytes.length);

        byte[] bytes = cipherEncrypt.get().doFinal(comressedBytes);
        System.out.println("crypted length: " + bytes.length);


        String encodedString = Hex.encodeHexString(bytes);

        return encodedString;
    }


    public static <T> T decode(String value, Class<T> clazz) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, DecoderException, IOException {
        return decode(Hex.decodeHex(value.toCharArray()), clazz);
    }

    public static <T> T decode(byte[] cryptedBytes, Class<T> clazz) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, DecoderException, IOException {
        byte[] bytes = cipherDecrypt.get().doFinal(cryptedBytes);


        SnappyInputStream snappyInputStream = new SnappyInputStream(new ByteArrayInputStream(bytes));


        //MessagePack messagePack = new MessagePack();
        return messagePack.read(ByteStreams.toByteArray(snappyInputStream), clazz);
    }


//    public static void checkToken(String token) throws NoSuchAlgorithmException {
//        Matcher matcher = tokenPattern.matcher(token);
//        if (!matcher.matches()) {
//            throw new IllegalArgumentException("bad token format");
//        }
//        String value = matcher.group(1);
//        String s = genToken(value);
//        if (!s.equals(token))
//            throw new IllegalArgumentException("bad token");
//
//    }
}
