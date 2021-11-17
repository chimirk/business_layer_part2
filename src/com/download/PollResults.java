package com.download;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.Hashtable;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PollResults {
    @jakarta.xml.bind.annotation.XmlElement
    String title;
    @jakarta.xml.bind.annotation.XmlElement
    String question;
    @jakarta.xml.bind.annotation.XmlElement
    Hashtable<String, Integer> choiceAndTotalVote;
    @jakarta.xml.bind.annotation.XmlElement
    Hashtable<Integer, String> pinAndChoice;

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

    public Hashtable<String, Integer> getChoiceAndTotalVote() {
        return choiceAndTotalVote;
    }

    public void setChoiceAndTotalVote(Hashtable<String, Integer> choiceAndTotalVote) {
        this.choiceAndTotalVote = choiceAndTotalVote;
    }

    public Hashtable<Integer, String> getPinAndChoice() {
        return pinAndChoice;
    }

    public void setPinAndChoice(Hashtable<Integer, String> pinAndChoice) {
        this.pinAndChoice = pinAndChoice;
    }
}


