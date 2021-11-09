package com.pollmanager;

public class Choice {
    private String text;
    private String description;

    public Choice(String text, String description) {
        this.text = text;
        this.description = description;
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
        return this.text.equals(compareChoice.getText()) && this.description.equals(compareChoice.getDescription());
    }


}
