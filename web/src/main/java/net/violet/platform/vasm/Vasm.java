package net.violet.platform.vasm;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class Vasm {

    enum HARDWARE {
        V1
    }

    /**
     * Source pour le point bleu.
     */
    private static final String POINTBLEU = "pointbleu.vasm";

    /**
     * Source pour la démo.
     */
    private static final String DEMO = "demo.vasm";

    /**
     * Source pour nabwait.
     */
    private static final String WAIT = "wait.vasm";

    /**
     * Source pour nabasleep.
     */
    private static final String ASLEEP = "asleep_v1.0.vasm";

    /**
     * Source pour nab msg.
     */
    private static final String MSG = "msg_v0.9.vasm";

    public enum VASM_FILE {
        V1(HARDWARE.V1);


        private final HARDWARE mHardware = HARDWARE.V1;
        private final String bluePointFileName;
        private final String demoFileName;
        private final String waitFileName;
        private final String sleepFileName;
        private final String msgFileName;

        private VASM_FILE(HARDWARE inHardware) {
            this.bluePointFileName = "Nabaztag_" + Vasm.POINTBLEU;
            this.demoFileName = "Nabaztag_" + Vasm.DEMO;
            this.waitFileName = "Nabaztag_" + Vasm.WAIT;
            this.sleepFileName = "Nabaztag_" + Vasm.ASLEEP;
            this.msgFileName = "Nabaztag_" + Vasm.MSG;
        }

        private static Map<HARDWARE, VASM_FILE> VASM_FILE_HARDWARE;

        static {
            final Map<HARDWARE, VASM_FILE> theMap = new HashMap<HARDWARE, VASM_FILE>();

            for (final VASM_FILE aVasmFile : VASM_FILE.values()) {
                theMap.put(aVasmFile.getHardware(), aVasmFile);
            }

            VASM_FILE.VASM_FILE_HARDWARE = Collections.unmodifiableMap(theMap);
        }

        public static VASM_FILE findByHardware(HARDWARE inHardware) {
            return VASM_FILE.VASM_FILE_HARDWARE.get(inHardware);
        }

        public Map<Pair<String, EAddrType>, Integer> getOpCodes() {
            if (HARDWARE.V1.equals(this.mHardware)) {
                return Vasm.OPCODES_V1;
            }
            // sinon OpCodes de daldal
            return Vasm.OPCODES_DALDAL;
        }

        public String getBluePointFileName() {
            return this.bluePointFileName;
        }

        public String getDemoFileName() {
            return this.demoFileName;
        }

        public String getWaitFileName() {
            return this.waitFileName;
        }

        public String getSleepFileName() {
            return this.sleepFileName;
        }

        public String getMsgFileName() {
            return this.msgFileName;
        }

        public HARDWARE getHardware() {
            return this.mHardware;
        }
    }

    private static final int MAX_FILE_SIZE = 360000;
    private static final int MAX_FILECUT_SIZE = 30000;
    private static final byte[] AMBER = new byte[]{'a', 'm', 'b', 'e', 'r'};
    private static final byte[] MIND = new byte[]{'m', 'i', 'n', 'd'};

    /**
     * Chemin vers les fichiers source.
     */
    private static final String SOURCE_DIRECTORY = Vasm.class.getCanonicalName().replace(".", "/").replaceFirst("/[^/]+$", "/");

    /**
     * Chemin vers les fichiers audio.
     */
    private static final String AUDIO_DIRECTORY = Vasm.SOURCE_DIRECTORY + "audio/";

    /**
     * Cache des codes source.
     */
    private static final Map<String, String> SOURCE_FILES = new HashMap<String, String>();

    /**
     * Cache des fichiers audio (ex audiooki).
     */
    private static final Map<String, byte[]> AUDIO_FILES = new HashMap<String, byte[]>();

    /**
     * Taille du cache des binaires compilés.
     */
    private static final int BINARY_CACHE_SIZE = 256;

    /**
     * Cache des binaires compilés.
     */
    private static final Map<String, byte[]> BINARY_CACHE = new LinkedHashMap<String, byte[]>() {

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, byte[]> entry) {
            return (size() > Vasm.BINARY_CACHE_SIZE);
        }
    };

    /**
     * Classe qui représente l'association entre equ/labels et la valeur.
     */
    static class AssoVasm {

        private final Map<String, Object> mEquMap;

        public AssoVasm() {
            this.mEquMap = new HashMap<String, Object>();
        }

        public void add(String inLabel, String inStrValue) {
            this.mEquMap.put(inLabel, inStrValue);
        }

        public void add(String inLabel, int inValue) {
            this.mEquMap.put(inLabel, inValue);
        }

        public String find(String l) {
            final Object theEqu = this.mEquMap.get(l);
            final String theResult;
            if (theEqu instanceof String) {
                theResult = (String) theEqu;
            } else {
                theResult = null;
            }
            return theResult;
        }

        public int findi(String l) {
            final Object theEqu = this.mEquMap.get(l);
            final int theResult;
            if (theEqu instanceof Integer) {
                theResult = ((Integer) theEqu).intValue();
            } else {
                theResult = -1;
            }
            return theResult;
        }
    }

    // fin de la classe Asso

    private final ByteArrayOutputStream mBinary;
    private int mLineNumber;

    private static int atoi(String inS) {
        if (inS == null) {
            return 0;
        }
        final String s = inS.trim();
        if (s.length() == 0) {
            return 0;
        }
        if ((s.startsWith("0x")) || (s.startsWith("0X"))) {
            return Integer.parseInt(s.substring(2), 16);
        }
        final Integer y = new Integer(s);
        return y.intValue();
    }

    private static int htoi(String s) {
        int i;
        int res = 0;
        for (i = 0; i < s.length(); i++) {
            res <<= 4;
            final byte c = (byte) s.charAt(i);
            if ((c >= '0') && (c <= '9')) {
                res += c - '0';
            }
            if ((c >= 'a') && (c <= 'f')) {
                res += c - 'a' + 10;
            }
            if ((c >= 'A') && (c <= 'F')) {
                res += c - 'A' + 10;
            }
        }
        return res;
    }

    private static byte[] getfilebytes(String filename) throws IOException {
        // TODO : check is the : cases are useful
        final byte[] theResult;
        // Include dans vasm/audio/
        theResult = Vasm.getAudio(filename);

        return theResult;
    }

    private enum EOpType {
        OP_r,
        OP_w,
        OP_i
    }

    enum EAddrType {
        TYPE_o(0, new EOpType[]{}),
        TYPE_r(1, new EOpType[]{EOpType.OP_r}),
        TYPE_w(2, new EOpType[]{EOpType.OP_w}),
        TYPE_ri(1, new EOpType[]{EOpType.OP_r, EOpType.OP_i}),
        TYPE_rr(1, new EOpType[]{EOpType.OP_r, EOpType.OP_r}),
        TYPE_rir(2, new EOpType[]{EOpType.OP_r, EOpType.OP_i, EOpType.OP_r}),
        TYPE_rrw(3, new EOpType[]{EOpType.OP_r, EOpType.OP_r, EOpType.OP_w}),
        TYPE_rw(3, new EOpType[]{EOpType.OP_r, EOpType.OP_w}),
        TYPE_i(1, new EOpType[]{EOpType.OP_i}),
        TYPE_ii(2, new EOpType[]{EOpType.OP_i, EOpType.OP_i}),
        TYPE_iiii(4, new EOpType[]{EOpType.OP_i, EOpType.OP_i, EOpType.OP_i, EOpType.OP_i});

        private final int mSize;
        private final EOpType[] mOpTypes;

        public int getSize() {
            return this.mSize;
        }

        private EAddrType(int inSize, EOpType[] inOpTypes) {
            this.mSize = inSize;
            this.mOpTypes = inOpTypes;
        }

        public static EAddrType fromOpTypes(EOpType[] inOpTypes) {
            EAddrType theResult = null;
            for (final EAddrType theType : EAddrType.values()) {
                if (Arrays.equals(theType.mOpTypes, inOpTypes)) {
                    theResult = theType;
                    break;
                }
            }
            return theResult;
        }
    }

    /**
     * Table d'association opcode V1 (avec type) -> valeur (de base).
     */
    private static final Map<Pair<String, EAddrType>, Integer> OPCODES_V1;

    static {
        OPCODES_V1 = new HashMap<Pair<String, EAddrType>, Integer>();
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("LD", EAddrType.TYPE_ri), 0x00);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("ADD", EAddrType.TYPE_ri), 0x10);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("SUB", EAddrType.TYPE_ri), 0x20);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("AND", EAddrType.TYPE_ri), 0x30);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("OR", EAddrType.TYPE_ri), 0x40);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("LDR", EAddrType.TYPE_ri), 0x50);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("STR", EAddrType.TYPE_ri), 0x60);
        int theValue = 0x70;
        // len = 0,0,0,0,0,1,1,1,
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("NOP", EAddrType.TYPE_o), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("TRANSITION", EAddrType.TYPE_o), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("RTI", EAddrType.TYPE_o), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("CLRCC", EAddrType.TYPE_o), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("SETCC", EAddrType.TYPE_o), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("ADDCC", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("SUBCC", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("INCW", EAddrType.TYPE_rr), theValue++);
        /* 0x78 */
        // len = 1,1,1,1,1,1,1,1,
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("DECW", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("MULW", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("INPUTRST", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("INT", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("ACK", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("WAIT", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("WAIT", EAddrType.TYPE_i), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("RND", EAddrType.TYPE_r), theValue++);
        /* 0x80 */
        // len = 1,1,1,1,1,1,1,1,
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("DEC", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("INC", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("CLR", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("NEG", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("NOT", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("TST", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("LD", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("ADD", EAddrType.TYPE_rr), theValue++);
        /* 0x88 */
        // len = 1,1,1,1,1,1,1,1,
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("SUB", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("MUL", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("AND", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("OR", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("EOR", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("LSL", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("LSR", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("ASR", EAddrType.TYPE_rr), theValue++);
        /* 0x90 */
        // len = 1,1,1,1,1,2,1,2,
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("ROL", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("ROR", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("CMP", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("BIT", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("LDR", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("LDR", EAddrType.TYPE_rir), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("STR", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("STR", EAddrType.TYPE_rir), theValue++);
        /* 0x98 */
        // len = 3,3,3,2,2,2,2,2,
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("LDT", EAddrType.TYPE_rrw), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("LDTW", EAddrType.TYPE_rw), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("INPUT", EAddrType.TYPE_rw), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("RTIJ", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("BRA", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("BEQ", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("BNE", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("BGT", EAddrType.TYPE_w), theValue++);
        /* 0xA0 */
        // len = 2,2,2,2,2,2,2,1,
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("BGE", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("BLT", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("BLE", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("BHI", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("BHS", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("BLO", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("BLS", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("LED", EAddrType.TYPE_rr), theValue++);
        /* 0xA8 */
        // len = 1,1,2,2,2,0,1,1,
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("PALETTE", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("ECHO", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("PUSH", EAddrType.TYPE_ii), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("PULL", EAddrType.TYPE_ii), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("BSR", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("RTS", EAddrType.TYPE_o), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("MOTOR", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("MIDIPLAY", EAddrType.TYPE_r), theValue++);
        /* 0xB0 */
        // len = 0,1,0,1,1,1,1,1,
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("MIDISTOP", EAddrType.TYPE_o), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("WAVPLAY", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("WAVSTOP", EAddrType.TYPE_o), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("MSEC", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("SEC", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("BUT3", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("VOL", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("MVOL", EAddrType.TYPE_r), theValue++);
        /* 0xB8 */
        // len = 1,1,3,3,1,1,1,0,
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("PUSHBUTTON", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("SRC", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("BRAT", EAddrType.TYPE_rw), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("BSRT", EAddrType.TYPE_rw), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("OSC", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("INV", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("DIV", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("HSV", EAddrType.TYPE_o), theValue++);
        /* 0xC0 */
        // len = 1,1,1,1,1,1,1,4,
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("MOTORGET", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("MUSIC", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("DOWNLOAD", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("MOTORRST", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("SEND", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("SENDREADY", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("LASTPING", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_V1.put(new Pair<String, EAddrType>("TRAME", EAddrType.TYPE_iiii), theValue++);
        assert theValue == 0xC8;
    }

    /**
     * Table d'association opcode DALDAL (avec type) -> valeur (de base).
     */
    private static final Map<Pair<String, EAddrType>, Integer> OPCODES_DALDAL;

    static {
        OPCODES_DALDAL = new HashMap<Pair<String, EAddrType>, Integer>();
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("LD", EAddrType.TYPE_ri), 0x00);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("ADD", EAddrType.TYPE_ri), 0x10);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("SUB", EAddrType.TYPE_ri), 0x20);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("AND", EAddrType.TYPE_ri), 0x30);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("OR", EAddrType.TYPE_ri), 0x40);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("LDR", EAddrType.TYPE_ri), 0x50);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("STR", EAddrType.TYPE_ri), 0x60);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("CMP", EAddrType.TYPE_ri), 0x70);
        int theValue = 0x80;
        // len = 0,0,0,0,0,1,1,1,
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("NOP", EAddrType.TYPE_o), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("TRANSITION", EAddrType.TYPE_o), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("RTI", EAddrType.TYPE_o), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("CLRCC", EAddrType.TYPE_o), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("SETCC", EAddrType.TYPE_o), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("ADDCC", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("SUBCC", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("INCW", EAddrType.TYPE_rr), theValue++);
        /* 0x88 */
        // len = 1,1,1,1,1,1,1,1,
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("DECW", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("MULW", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("INPUTRST", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("INT", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("ACK", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("WAIT", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("WAIT", EAddrType.TYPE_i), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("RND", EAddrType.TYPE_r), theValue++);
        /* 0x90 */
        // len = 1,1,1,1,1,1,1,1,
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("DEC", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("INC", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("CLR", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("NEG", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("NOT", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("TST", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("LD", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("ADD", EAddrType.TYPE_rr), theValue++);
        /* 0x98 */
        // len = 1,1,1,1,1,1,1,1,
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("SUB", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("MUL", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("AND", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("OR", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("EOR", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("LSL", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("LSR", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("ASR", EAddrType.TYPE_rr), theValue++);
        /* 0xA0 */
        // len = 1,1,1,1,1,2,1,2,
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("ROL", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("ROR", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("CMP", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("BIT", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("LDR", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("LDR", EAddrType.TYPE_rir), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("STR", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("STR", EAddrType.TYPE_rir), theValue++);
        /* 0xA8 */
        // len = 3,3,3,2,2,2,2,2,
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("LDT", EAddrType.TYPE_rrw), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("LDTW", EAddrType.TYPE_rw), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("INPUT", EAddrType.TYPE_rw), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("RTIJ", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("BRA", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("BEQ", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("BNE", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("BGT", EAddrType.TYPE_w), theValue++);
        /* 0xB0 */
        // len = 2,2,2,2,2,2,2,1,
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("BGE", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("BLT", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("BLE", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("BHI", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("BHS", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("BLO", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("BLS", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("LED", EAddrType.TYPE_rr), theValue++);
        /* 0xB8 */
        // len = 1,1,2,2,2,0,1,1,
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("PALETTE", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("ECHO", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("PUSH", EAddrType.TYPE_ii), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("PULL", EAddrType.TYPE_ii), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("BSR", EAddrType.TYPE_w), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("RTS", EAddrType.TYPE_o), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("MOTOR", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("MIDIPLAY", EAddrType.TYPE_r), theValue++);
        /* 0xC0 */
        // len = 0,1,0,1,1,1,1,1,
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("MIDISTOP", EAddrType.TYPE_o), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("WAVPLAY", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("WAVSTOP", EAddrType.TYPE_o), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("MSEC", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("SEC", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("BUT3", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("VOL", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("MVOL", EAddrType.TYPE_r), theValue++);
        /* 0xC8 */
        // len = 1,1,3,3,1,1,1,0,
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("PUSHBUTTON", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("SRC", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("BRAT", EAddrType.TYPE_rw), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("BSRT", EAddrType.TYPE_rw), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("OSC", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("INV", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("DIV", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("HSV", EAddrType.TYPE_o), theValue++);
        /* 0xD0 */
        // len = 1,1,1,1,1,1,1,4,
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("MOTORGET", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("MUSIC", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("DOWNLOAD", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("MOTORRST", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("SEND", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("SENDREADY", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("LASTPING", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("TRAME", EAddrType.TYPE_iiii), theValue++);
        /* 0xD8 */
        // len = 0,1,1,1,1,1
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("STOPNET", EAddrType.TYPE_o), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("FADE", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("FADE", EAddrType.TYPE_rr), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("LDC", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("ADDC", EAddrType.TYPE_r), theValue++);
        Vasm.OPCODES_DALDAL.put(new Pair<String, EAddrType>("SETC", EAddrType.TYPE_r), theValue++);
        assert theValue == 0xDD;
    }

    private int cutline(String src, int inI0, String[] words, int n) {
        int i;
        final int l = src.length();
        int k = 0;
        int go = 1;
        byte c;
        int i0 = inI0;

        // on avance jusqu'au prochain det de ligne
        while (go > 0) {
            if (i0 >= l) {
                go = 0;
            } else {
                c = (byte) src.charAt(i0);
                if ((c < 32) && (c != 9)) {
                    i0++;
                } else {
                    go = 0;
                }
                if (c == 10) {
                    this.mLineNumber++;
                }
            }
        }
        i = i0;
        go = 1;
        while (go > 0) {
            if (i >= l) {
                go = 0;
            } else {
                c = (byte) src.charAt(i);
                if (i0 >= 0) {
                    if (c <= 32) {
                        words[k++] = src.substring(i0, i);
                        i0 = -1;
                    }
                } else if (c > 32) {
                    i0 = i;
                }
                if ((c < 32) && (c != 9)) {
                    go = 0;
                } else if (k >= n) {
                    go = 0;
                } else {
                    i++;
                }
            }
        }
        if ((i == l) && (i0 >= 0)) {
            words[k++] = src.substring(i0, l);
        }
        while (k < n) {
            words[k++] = "";
        }
        go = 1;
        while (go > 0) {
            if (i >= l) {
                go = 0;
            } else {
                c = (byte) src.charAt(i);
                if ((c == 10) || (c == 13)) {
                    go = 0;
                } else {
                    i++;
                }
            }
        }
        return i;
    }

    static String[] cutvirg(String src, AssoVasm equ) {
        final String theArgsStrTrimmed = src.trim();
        final String[] theArgs;
        if (theArgsStrTrimmed.equals("")) {
            theArgs = new String[0];
        } else {
            theArgs = theArgsStrTrimmed.split(",");
            final int nbArgs = theArgs.length;
            for (int indexArgs = 0; indexArgs < nbArgs; indexArgs++) {
                final String theArgTrimmed = theArgs[indexArgs].trim();
                final String theVal = equ.find(theArgTrimmed);
                if (theVal != null) {
                    theArgs[indexArgs] = theVal;
                } else {
                    theArgs[indexArgs] = theArgTrimmed;
                }
            }
        }

        return theArgs;
    }

    /**
     * Retourne le type d'addressage de l'instruction à partir des opérandes.
     *
     * @param args le tableau qui contient les opérandes.
     * @return le type d'adressage.
     */
    static EAddrType typeoper(String[] args) {
        final int nbArgs = args.length;
        final EOpType[] theOpTypes = new EOpType[nbArgs];
        int indexArgs;
        for (indexArgs = 0; indexArgs < nbArgs; indexArgs++) {
            final String theArg = args[indexArgs];
            final EOpType theOpType;
            switch (theArg.charAt(0)) {
                case 'R':
                case 'r':
                    theOpType = EOpType.OP_r;
                    break;
                case '@':
                    theOpType = EOpType.OP_w;
                    break;
                default:
                    theOpType = EOpType.OP_i;
            }
            theOpTypes[indexArgs] = theOpType;
        }

        return EAddrType.fromOpTypes(theOpTypes);
    }

    private static int checkint(String s, int start, int end) {
        int i;
        byte c;
        for (i = start; i < end; i++) {
            c = (byte) s.charAt(i);
            if ((c < '0') || (c > '9')) {
                if ((c != '-') || (i != start)) {
                    return 0;
                }
            }
        }
        return 1;
    }

    private static int checkhexa(String s, int start, int end) {
        int i;
        byte c;
        for (i = start + 1; i < end; i++) {
            c = (byte) s.charAt(i);
            if (((c < '0') || (c > '9')) && ((c < 'a') || (c > 'f')) && ((c < 'A') || (c > 'F'))) {
                return 0;
            }
        }
        return 1;
    }

    private void checkInt(String reg) throws VasmException {
        if (reg.charAt(0) == '$') {
            if (Vasm.checkhexa(reg, 1, reg.length()) == 0) {
                throw new VasmException(this.mLineNumber, reg + " is not an int");
                // message d'erreur
            }

            this.mBinary.write(Vasm.htoi(reg.substring(1, reg.length())));
        } else {
            if (Vasm.checkint(reg, 0, reg.length()) == 0) {
                throw new VasmException(this.mLineNumber, reg + " is not an int");
            }
            this.mBinary.write(Vasm.atoi(reg));
        }
    }

    private void checkRegOP(int inOp, String reg) throws VasmException {
        if (Vasm.checkint(reg, 1, reg.length()) == 0) {
            throw new VasmException(this.mLineNumber, reg + " is not a register");
        }

        this.mBinary.write(inOp + Vasm.atoi(reg.substring(1, reg.length())));
    }

    private void checkReg(int inOp, String reg) throws VasmException {
        this.mBinary.write(inOp);
        if (Vasm.checkint(reg, 1, reg.length()) == 0) {
            throw new VasmException(this.mLineNumber, reg + " is not a register");
        }
        this.mBinary.write(Vasm.atoi(reg.substring(1, reg.length())));
    }

    private void checkReg2(int inOp, String a, String b) throws VasmException {
        this.mBinary.write(inOp);
        if (Vasm.checkint(a, 1, a.length()) == 0) {
            throw new VasmException(this.mLineNumber, a + " is not a register");
        }

        if (Vasm.checkint(b, 1, b.length()) == 0) {
            throw new VasmException(this.mLineNumber, b + " is not a register");
        }
        this.mBinary.write(((Vasm.atoi(a.substring(1, a.length())) & 15) << 4) + (Vasm.atoi(b.substring(1, b.length())) & 15));
    }

    private void checkAbs(String label, List<Pair<String, Integer>> inLinks) throws VasmException {
        final int theIndex = this.mBinary.size();
        if (theIndex == 0) {
            throw new VasmException(this.mLineNumber, "fdb error");
        }

        // Ajout de l'offset courant pour écrire l'adresse absolue.
        inLinks.add(new Pair<String, Integer>(label, theIndex));
        this.mBinary.write(0);
        this.mBinary.write(0);
    }

    private Vasm() {
        this.mBinary = new ByteArrayOutputStream();
    }

    private void includebin(int inLine, String name) throws VasmException, IOException {
        final byte[] content = Vasm.getfilebytes(name);
        if (content == null) {
            throw new VasmException(inLine, "unknown file : " + name);
        }
        int x = content[0] & 255;
        x = (x << 8) + (content[1] & 255);
        x = (x << 8) + (content[2] & 255);
        x = (x << 8) + (content[3] & 255);
        for (int i = 0; i < x; i++) {
            this.mBinary.write(content[i + 4]);
        }
    }

    private ByteArrayOutputStream asm(String src, int offset0, VASM_FILE theVasm_file) throws VasmException {
        try {
            int i = 0;
            this.mLineNumber = 1;
            final String[] words = new String[3];
            // Liste des offsets où écrire les adresses absolues des labels
            // (lors de la deuxième passe).
            final List<Pair<String, Integer>> links = new LinkedList<Pair<String, Integer>>();
            final AssoVasm labels = new AssoVasm();
            final AssoVasm equ = new AssoVasm();
            int offset = offset0;
            int rmb_index = 0;
            int src_index = 0;

            int countfile = 0;
            final ByteArrayOutputStream offfile = new ByteArrayOutputStream();
            final ByteArrayOutputStream datafile = new ByteArrayOutputStream();
            final Map<Pair<String, EAddrType>, Integer> theOpCodes = theVasm_file.getOpCodes();
            while (i < src.length()) {
                i = cutline(src, i, words, 3);

                // System.out.println(words[0]+StringShop.SPACE+words[1]+
                // StringShop.SPACE+words[2]);
                if ((words[0].length() == 0) || (words[0].charAt(0) != ';')) {
                    if ((words[0].length() != 0) && (words[0].charAt(0) == '@')) {
                        labels.add(words[0], offset);
                    }

                    if (words[1].compareToIgnoreCase("END") == 0) {
                        // Deuxième passe: on écrit les adresses absolues.
                        final byte[] theBinary = this.mBinary.toByteArray();
                        for (final Pair<String, Integer> theLink : links) {
                            final int theAddress = labels.findi(theLink.getFirst());
                            if (theAddress < 0) {
                                throw new VasmException(this.mLineNumber, "cannot link " + theLink.getFirst() + " : unknown label");
                            }
                            final int theOffset = theLink.getSecond();
                            theBinary[theOffset] = (byte) ((theAddress >> 8) & 255);
                            theBinary[theOffset + 1] = (byte) (theAddress & 255);
                        }
                        final int theCodeSize = theBinary.length;
                        final int theResultSize = theCodeSize + 8 + offfile.size() + datafile.size();
                        final ByteArrayOutputStream theResultStream = new ByteArrayOutputStream(theResultSize);
                        LibBasic.writeIntTo4Bytes(theResultStream, theCodeSize);
                        theResultStream.write(theBinary);
                        LibBasic.writeIntTo4Bytes(theResultStream, countfile);
                        offfile.writeTo(theResultStream);
                        datafile.writeTo(theResultStream);
                        return theResultStream;
                    }
                    if (words[1].compareToIgnoreCase("EQU") == 0) {
                        equ.add(words[0], words[2]);
                    } else if (words[1].compareToIgnoreCase("RMB") == 0) {
                        equ.add(words[0], String.valueOf(rmb_index));
                        rmb_index += Vasm.atoi(words[2]);
                    } else if (words[1].compareToIgnoreCase("FCB") == 0) {
                        final String[] args = Vasm.cutvirg(words[2], equ);
                        for (final String theArg : args) {
                            checkInt(theArg);
                        }
                        offset += args.length;
                    } else if (words[1].compareToIgnoreCase("FDB") == 0) {
                        final String[] args = Vasm.cutvirg(words[2], equ);
                        for (final String theArg : args) {
                            checkAbs(theArg, links);
                        }
                        offset += 2 * args.length;
                    } else if ((words[0].length() != 0) && (words[0].charAt(0) == '/') && (words[1].compareToIgnoreCase("FILE") == 0)) {
                        final byte[] theContent = Vasm.getfilebytes(words[2]);
                        if (theContent == null) {
                            throw new VasmException(this.mLineNumber, "unknown file : " + words[2]);
                        }
                        final int theContentLength = theContent.length;
                        if (theContentLength > Vasm.MAX_FILE_SIZE) {
                            datafile.write(theContent, 0, Vasm.MAX_FILE_SIZE);
                        } else {
                            datafile.write(theContent);
                        }
                        LibBasic.writeIntTo4Bytes(offfile, datafile.size());
                        equ.add(words[0], String.valueOf(countfile));
                        countfile++;
                    } else if ((words[0].length() != 0) && (words[0].charAt(0) == '/') && (words[1].compareToIgnoreCase("FILECUT") == 0)) {
                        final byte[] theContent = Vasm.getfilebytes(words[2]);
                        if (theContent == null) {
                            throw new VasmException(this.mLineNumber, "unknown file : " + words[2]);
                        }
                        final int theContentLength = theContent.length;
                        if (theContentLength > Vasm.MAX_FILECUT_SIZE) {
                            datafile.write(theContent, 0, Vasm.MAX_FILECUT_SIZE);
                        } else {
                            datafile.write(theContent);
                        }

                        LibBasic.writeIntTo4Bytes(offfile, datafile.size());
                        equ.add(words[0], String.valueOf(countfile));
                        countfile++;
                    } else if ((words[0].length() != 0) && (words[0].charAt(0) == '/') && (words[1].compareToIgnoreCase("SRCDEF") == 0)) {
                        equ.add(words[0], String.valueOf(src_index));
                        src_index++;
                    } else if (words[1].compareToIgnoreCase("BINARY") == 0) {
                        includebin(this.mLineNumber, words[2]);
                    } else if (words[1].length() != 0) {
                        final String[] args = Vasm.cutvirg(words[2], equ);
                        final EAddrType theAddrType = Vasm.typeoper(args);
                        if (theAddrType == null) {
                            throw new VasmException(this.mLineNumber, "unknown addressing mode : " + words[2]);
                        }
                        final Pair<String, EAddrType> opcode = new Pair<String, EAddrType>(words[1].toUpperCase(), theAddrType);
                        final Integer theInstructionValue = theOpCodes.get(opcode);
                        if (theInstructionValue == null) {
                            throw new VasmException(this.mLineNumber, "unknown instruction : " + words[1] + " (" + theAddrType + ")");
                        }
                        final int op = theInstructionValue.intValue();
                        offset += 1 + theAddrType.getSize();

                        switch (theAddrType) {
                            case TYPE_ri:
                                checkRegOP(op, args[0]);
                                checkInt(args[1]);
                                break;
                            case TYPE_o:
                                this.mBinary.write(op);
                                break;
                            case TYPE_r:
                                checkReg(op, args[0]);
                                break;
                            case TYPE_rr:
                                checkReg2(op, args[0], args[1]);
                                break;
                            case TYPE_w:
                                this.mBinary.write(op);
                                checkAbs(args[0], links);
                                break;
                            case TYPE_rrw:
                                checkReg2(op, args[0], args[1]);
                                checkAbs(args[2], links);
                                break;
                            case TYPE_rw:
                                checkReg(op, args[0]);
                                checkAbs(args[1], links);
                                break;
                            case TYPE_i:
                                this.mBinary.write(op);
                                checkInt(args[0]);
                                break;
                            case TYPE_rir:
                                checkReg2(op, args[0], args[2]);
                                checkInt(args[1]);
                                break;
                            case TYPE_ii:
                                this.mBinary.write(op);
                                checkInt(args[0]);
                                checkInt(args[1]);
                                break;
                            case TYPE_iiii:
                                this.mBinary.write(op);
                                checkInt(args[0]);
                                checkInt(args[1]);
                                checkInt(args[2]);
                                checkInt(args[3]);
                                break;
                        }
                    }

                }
            }
            throw new VasmException(this.mLineNumber, "end of file");
        } catch (final Throwable t) {
            throw new VasmException(this.mLineNumber, "exception (" + t.getMessage() + ")", t);
        }
    }

    private static int checksum(byte[] buf) {
        try {
            final int l = buf.length;
            int i = 0;
            byte chk = 0;
            for (i = 0; i < l; i++) {
                chk += buf[i];
            }
            return chk;
        } catch (final Throwable t) {
            return -1;
        }
    }

    static String getSource(String inSourceFileName) throws IOException {
        String theResult;
        synchronized (Vasm.SOURCE_FILES) {
            theResult = Vasm.SOURCE_FILES.get(inSourceFileName);
            if (theResult == null) {
                final InputStream theStream = Vasm.class.getClassLoader().getResourceAsStream(Vasm.SOURCE_DIRECTORY + inSourceFileName);
                if (theStream == null) {
                    throw new FileNotFoundException(inSourceFileName);
                }
                final BufferedReader theReader = new BufferedReader(new InputStreamReader(theStream));
                final StringBuilder theResultBuilder = new StringBuilder();
                while (true) {
                    final String theLine = theReader.readLine();
                    if (theLine == null) {
                        break;
                    }
                    theResultBuilder.append(theLine).append("\n");
                }
                theResult = theResultBuilder.toString();
                Vasm.SOURCE_FILES.put(inSourceFileName, theResult);
            }
        }
        return theResult;
    }

    static byte[] getAudio(String inAudioFileName) throws IOException {
        byte[] theResult;
        theResult = Vasm.AUDIO_FILES.get(inAudioFileName);
        if (theResult == null) {
            synchronized (Vasm.AUDIO_FILES) {
                theResult = Vasm.AUDIO_FILES.get(inAudioFileName);
                if (theResult == null) {
                    final InputStream theStream = Vasm.class.getClassLoader().getResourceAsStream(Vasm.AUDIO_DIRECTORY + inAudioFileName);
                    if (theStream == null) {
                        throw new FileNotFoundException(inAudioFileName);
                    }
                    final ByteArrayOutputStream theResultBuilder = new ByteArrayOutputStream();
                    final byte[] theBuffer = new byte[2048];
                    while (true) {
                        final int nbRead = theStream.read(theBuffer);
                        if (nbRead < 0) {
                            break;
                        }
                        theResultBuilder.write(theBuffer, 0, nbRead);
                    }
                    theResult = theResultBuilder.toByteArray();
                    Vasm.AUDIO_FILES.put(inAudioFileName, theResult);
                }
            }
        }
        return theResult;
    }

    static byte[] asmonly(String src, VASM_FILE theVasm_file) throws VasmException {
        final Vasm theVasm = new Vasm();
        final byte[] res = theVasm.asm(src, 0, theVasm_file).toByteArray();
        return res;
    }

    public static ByteArrayOutputStream maketrame(String inSourceFileName, String inData, int id, int timeout, VASM_FILE theVasm_file) throws VasmException {
        final String theCacheKey = inSourceFileName + ":" + inData;
        try {
            final ByteArrayOutputStream theResult = new ByteArrayOutputStream();
            byte[] theCachedData;
            synchronized (Vasm.BINARY_CACHE) {
                theCachedData = Vasm.BINARY_CACHE.get(theCacheKey);
                if (theCachedData == null) {
                    String theSource = Vasm.getSource(inSourceFileName);
                    if (inData != null) {
                        theSource = theSource.replaceAll("<data>", inData);
                    }
                    final Vasm theVasm = new Vasm();
                    final ByteArrayOutputStream theBinary = theVasm.asm(theSource, 17, theVasm_file);
                    theCachedData = theBinary.toByteArray();
                    Vasm.BINARY_CACHE.put(theCacheKey, theCachedData);
                }
            }
            theResult.write((byte) 5);
            LibBasic.writeIntTo3Bytes(theResult, theCachedData.length + 15);
            theResult.write(Vasm.AMBER);
            LibBasic.writeIntTo4Bytes(theResult, id);
            theResult.write((byte) timeout);
            theResult.write(theCachedData);
            theResult.write((byte) (255 - Vasm.checksum(theResult.toByteArray()) - Vasm.checksum(Vasm.MIND)));
            theResult.write(Vasm.MIND);
            return theResult;
        } catch (final IOException anException) {
            throw new VasmException(anException);
        }
    }

    static void emptyCache() {
        synchronized (Vasm.BINARY_CACHE) {
            Vasm.BINARY_CACHE.clear();
        }
    }
}
