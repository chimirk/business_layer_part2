package com.generator;

import java.security.SecureRandom;
import java.util.Random;

public class PollID {
    private static final int LENGTHID = 10;
    private static final Random RANDOM = new SecureRandom();
    private static final String ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ";

    public static String genreateStringID(){
        StringBuilder stringID = new StringBuilder(LENGTHID);
        for (int i = 0; i < LENGTHID; i++) {
            stringID.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return stringID.toString();
    }
}
