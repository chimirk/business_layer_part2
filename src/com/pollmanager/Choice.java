package com.pollmanager;

public class Choice {
    private int choiceID;
    private String text;
    private String description;

    public Choice(String text, String description) {
        this.text = text;
        this.description = description;
    }
    public Choice(int choiceID, String text, String description) {
        this.choiceID = choiceID;
        this.text = text;
        this.description = description;
    }

    public int getChoiceID() {
        return choiceID;
    }

    public void setChoiceID(int choiceID) {
        this.choiceID = choiceID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        Choice compareChoice = (Choice) obj;
        return this.choiceID == compareChoice.getChoiceID() &&this.text.equals(compareChoice.getText()) && this.description.equals(compareChoice.getDescription());
    }


}
