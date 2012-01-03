package net.violet.platform.vasm;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibBasic {

    private static final String CHARSET = "ISO-8859-1";
    private static final Logger LOGGER = LoggerFactory.getLogger(LibBasic.class);

    /**
     * Lit un entier sur 3 octets (en big endian).
     *
     * @param inStream stream avec les octets à lire.
     * @return l'entier.
     * @throws IOException en cas d'erreur de lecture ou si on arrive à la fin
     *             du flux.
     */
    public static int bin4toi(InputStream inStream) throws IOException {
        return LibBasic.binntoi(inStream, 4);
    }

    /**
     * Lit un entier sur 3 octets (en big endian).
     *
     * @param inStream stream avec les octets à lire.
     * @return l'entier.
     * @throws IOException en cas d'erreur de lecture ou si on arrive à la fin
     *             du flux.
     */
    public static int bin3toi(InputStream inStream) throws IOException {
        return LibBasic.binntoi(inStream, 3);
    }

    /**
     * Lit un entier sur n octets (en big endian).
     *
     * @param inStream stream avec les octets à lire.
     * @param inIntSize nombre d'octets à lire.
     * @return l'entier.
     * @throws IOException en cas d'erreur de lecture ou si on arrive à la fin
     *             du flux.
     */
    private static int binntoi(InputStream inStream, int inIntSize) throws IOException {
        int theResult = 0;
        for (int nbBytes = 0; nbBytes < inIntSize; nbBytes++) {
            theResult = theResult << 8;
            final int theByte = inStream.read();
            if (theByte == -1) {
                throw new EOFException();
            }
            theResult |= theByte;
        }
        return theResult;
    }

    private static int[] inv8 = { 1, 171, 205, 183, 57, 163, 197, 239, 241, 27, 61, 167, 41, 19, 53, 223, 225, 139, 173, 151, 25, 131, 165, 207, 209, 251, 29, 135, 9, 243, 21, 191, 193, 107, 141, 119, 249, 99, 133, 175, 177, 219, 253, 103, 233, 211, 245, 159, 161, 75, 109, 87, 217, 67, 101, 143, 145, 187, 221, 71, 201, 179, 213, 127, 129, 43, 77, 55, 185, 35, 69, 111, 113, 155, 189, 39, 169, 147, 181, 95, 97, 11, 45, 23, 153, 3, 37, 79, 81, 123, 157, 7, 137, 115, 149, 63, 65, 235, 13, 247, 121, 227, 5, 47, 49, 91, 125, 231, 105, 83, 117, 31, 33, 203, 237, 215, 89, 195, 229, 15, 17, 59, 93, 199, 73, 51, 85, 255 };

    public static byte[] crypt8(String src, int key, int alpha) {
        try {
            final byte[] buf = src.getBytes("ISO-8859-1");
            int theKey = key;
            for (int i = 0; i < buf.length; i++) {
                final byte v = buf[i];
                final int x = alpha + v * LibBasic.inv8[theKey >> 1];
                buf[i] = (byte) x;
                theKey = (v + v + 1) & 255;
            }
            return buf;
        } catch (final UnsupportedEncodingException t) {
            LibBasic.LOGGER.debug("!exception in crypt8");
            LibBasic.LOGGER.error(t.getMessage(), t);
        }
        return null;
    }

    public static String uncrypt8(byte[] inBytes, int inKey, int inAlpha) {
        final int theSize = inBytes.length;
        final byte[] theResultBuffer = new byte[theSize];
        String theResult = null;
        int theKey = inKey;
        for (int index = 0; index < theSize; index++) {
            final byte theByte = inBytes[index];
            final int v = ((theByte - inAlpha) * theKey) & 255;
            theKey = (v + v + 1) & 255;
            theResultBuffer[index] = (byte) v;
        }
        try {
            theResult = new String(theResultBuffer, "ISO-8859-1");
        } catch (final UnsupportedEncodingException e) {
            LibBasic.LOGGER.error(e.getMessage(), e);
        }
        return theResult;
    }

    public static void writeIntTo4Bytes(ByteArrayOutputStream inStream, int v) {
        inStream.write((byte) ((v >> 24) & 255));
        inStream.write((byte) ((v >> 16) & 255));
        inStream.write((byte) ((v >> 8) & 255));
        inStream.write((byte) (v & 255));
    }

    public static void writeIntTo3Bytes(ByteArrayOutputStream inStream, int v) {
        inStream.write((byte) ((v >> 16) & 255));
        inStream.write((byte) ((v >> 8) & 255));
        inStream.write((byte) (v & 255));
    }

    public static String getStringFromBytes(byte[] inBytes, String inCharset) {
        try {
            return (inBytes != null) ? new String(inBytes, (inCharset != null) ? inCharset : LibBasic.CHARSET) : "";
        } catch (final UnsupportedEncodingException e) {
            LibBasic.LOGGER.error(e.getMessage(), e);
        }

        return "";
    }

    public static String getStringFromBytes(byte[] inBytes) {
        return LibBasic.getStringFromBytes(inBytes, LibBasic.CHARSET);
    }
}
