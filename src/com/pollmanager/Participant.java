package com.pollmanager;

public class Participant {
    private String sessionID;
    private Choice vote;

    public Participant(String sessionID, Choice vote) {
        this.sessionID = sessionID;
        this.vote = new Choice(vote.getText(), vote.getDescription());
    }

    public String getSessionID() {
        return sessionID;
    }

    public Choice getVote(){
        return new Choice(this.vote.getText(), this.vote.getDescription());
    }

    public void setVote(Choice vote){
        this.vote = new Choice(vote.getText(), vote.getDescription());
    }
}




