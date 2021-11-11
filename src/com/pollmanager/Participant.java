package com.pollmanager;

public class Participant {
    private int pin;
    private Choice vote;

    public Participant(int pin, Choice vote) {
        this.pin = pin;
        this.vote = new Choice(vote.getChoiceID(), vote.getText(), vote.getDescription());
    }

    public int getPIN() {
        return pin;
    }

    public Choice getVote(){
        return new Choice(this.vote.getChoiceID(), this.vote.getText(), this.vote.getDescription());
    }

    public void setVote(Choice vote){
        this.vote = new Choice(vote.getChoiceID(), vote.getText(), vote.getDescription());
    }
}




