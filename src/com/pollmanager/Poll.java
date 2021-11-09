package com.pollmanager;

import java.util.ArrayList;

public class Poll {
    String title;
    String question;
    PollStatus status;
    ArrayList<Choice> choices;

    public Poll() {
        this.choices = new ArrayList<>();
    }

    public Poll(String title, String question) {
        this.title = title;
        this.question = question;
        this.choices = new ArrayList<>();
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
