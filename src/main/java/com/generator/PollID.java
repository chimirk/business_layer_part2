package com.generator;

import com.database.PollGateway;
import com.pollmanager.Poll;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

public class PollID {
    private static final int LENGTHID = 10;
    private static final Random RANDOM = new SecureRandom();
    private static final String ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ";

    public static String generateStringID(){
        StringBuilder stringID = new StringBuilder(LENGTHID);
        for (int i = 0; i < LENGTHID; i++) {
            stringID.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        if(idExists(stringID.toString())){
            stringID = new StringBuilder(LENGTHID);
            stringID.append(generateStringID());
        }
        return stringID.toString();
    }

    public static boolean idExists(String pollIDGenerated){
        ArrayList<Poll> polls = PollGateway.selectAllPolls();
        for (Poll poll : polls) {
            if (poll.getPollID().equalsIgnoreCase(pollIDGenerated)) {
                return true;
            }
        }
        return false;
    }
}
