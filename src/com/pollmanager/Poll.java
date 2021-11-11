package com.pollmanager;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Poll {
    String pollID;
    String title;
    String question;
    Timestamp createdAt;
    Timestamp releasedAt;
    PollStatus status;
    ArrayList<Choice> choices;
    String userID;


    public Poll() {
        this.choices = new ArrayList<>();
    }

    public Poll(String pollID, String title, String question, Timestamp createdAt, String userID) {
        this.pollID = pollID;
        this.title = title;
        this.question = question;
        this.createdAt = createdAt;
        this.userID = userID;
        this.choices = new ArrayList<>();
    }

    public String getPollID() {
        return pollID;
    }

    public void setPollID(String pollID) {
        this.pollID = pollID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public PollStatus getStatus() {
        return status;
    }

    public void setStatus(PollStatus status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getReleasedAt() {
        return releasedAt;
    }

    public void setReleasedAt(Timestamp releasedAt) {
        this.releasedAt = releasedAt;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<Choice> getChoices() {
        return choices;
    }

    public void setChoices(ArrayList<Choice> choices) throws PollException {
        if(choices.size()<2){
            throw new PollException("The number of choices must be at least 2.");
        }
        this.choices = new ArrayList<>();
        choices.forEach( choice -> this.choices.add(new Choice(choice.getText(), choice.getDescription())));
    }

    public boolean isValidChoice(Choice userChoice){
        for(int i=0; i<this.choices.size(); i++){
            if(this.choices.get(i).equals(userChoice)){
                return true;
            }
        }
        return false;
    }
}
