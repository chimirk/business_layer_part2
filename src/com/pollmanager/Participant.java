package com.pollmanager;

public class Participant {
    private String PIN;
    private Choice vote;

    public Participant(String sessionID, Choice vote) {
        this.PIN = sessionID;
        this.vote = new Choice(vote.getText(), vote.getDescription());
    }

    public String getPIN() {
        return PIN;
    }

    public Choice getVote(){
        return new Choice(this.vote.getText(), this.vote.getDescription());
    }

    public void setVote(Choice vote){
        this.vote = new Choice(vote.getText(), vote.getDescription());
    }
}




