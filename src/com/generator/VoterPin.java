package com.generator;

import com.database.VoteGateway;
import com.pollmanager.Participant;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

public class VoterPin {

    private static final int LENGTHPIN = 6;
    private static final Random RANDOM = new SecureRandom();
    private static final String DIGITS = "0123456789";

    public static int generatePin(String pollID){
        int pinGenerated;
        StringBuilder stringID = new StringBuilder(LENGTHPIN);
        for (int i = 0; i < LENGTHPIN; i++) {
            stringID.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        }
        pinGenerated = Integer.parseInt(String.valueOf(stringID));
        if(!isUnique(pollID,pinGenerated)){
            pinGenerated = generatePin(pollID);
        }
        return pinGenerated;
    }

    private static boolean isUnique(String pollID, int pinGenerated){
        ArrayList<Participant> participants = VoteGateway.getAllVotesByPoll(pollID);
        for (Participant participant : participants) {
            if (participant.getPIN() == pinGenerated) {
                return false;
            }
        }
        return true;
    }
}
