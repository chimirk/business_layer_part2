package com.pollmanager;

import clover.com.google.gson.Gson;
import clover.com.google.gson.GsonBuilder;
import com.database.PollGateway;
import com.database.VoteGateway;
import com.download.PollResults;
import com.generator.PollID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

public class PollManager {

    public void createPoll(String title, String question, ArrayList<Choice> choices, String userID) throws PollManagerException, PollException {
        String pollID = PollID.generateStringID();
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        Poll poll = new Poll();

        if (title.trim().isEmpty()) {
            throw new PollManagerException("Invalid Title! Please enter a proper title.");
        }
        if (question.trim().isEmpty()) {
            throw new PollManagerException("Invalid Question! Please enter a proper question.");
        }

        for (int i = 0; i < choices.size(); i++) {
            if (choices.get(i).getText().trim().isEmpty() || choices.get(i).getDescription().trim().isEmpty()) {
                throw new PollManagerException("Invalid Choice! Please enter a proper choice with text and description.");
            }
        }

        poll = new Poll(pollID, title, question, createdAt, userID);
        poll.setChoices(choices);
        poll.setStatus(PollStatus.CREATED);
        PollGateway.insertPoll(poll);

    }

    public void deletePoll(String userId, String pollID) throws PollManagerException {
        Poll poll = PollGateway.selectPollById(pollID);

        if (Objects.isNull(poll)) {
            throw new PollManagerException("There is no poll with this poll ID.");
        }

        if (!userId.equals(poll.getUserID())){
            throw new PollManagerException("A poll may be deleted only by the user who has created it.");
        }

        //Check if Poll has been voted on using VoteDAO
        if(!VoteGateway.getAllVotesByPoll(pollID).isEmpty()){
            throw new PollManagerException("A poll may be deleted only if it has not been voted on");
        }

        PollGateway.deletePoll(poll.getPollID());
    }

    public Poll accessPoll(String pollID) throws PollManagerException {

        Poll poll = PollGateway.selectPollById(pollID);
        if (Objects.isNull(poll)) {
            throw new PollManagerException("There is no poll with this poll ID.");
        }

        if(poll.getStatus() == PollStatus.CLOSED || poll.getStatus() == PollStatus.CREATED){
            throw new PollManagerException("Currently the poll is in created or closed sated");
        }
        return poll;
    }

    public ArrayList<Poll> getAllPollsByUser(String userID){
        ArrayList<Poll> userPolls = new ArrayList<>();
        PollGateway.selectAllPolls().forEach(poll -> {
            if(poll.getUserID().equals(userID)){
                userPolls.add(poll);
            }
        });
        return userPolls;
    }

    public void updatePoll(String title, String question, ArrayList<Choice> choices, String pollID, String userID) throws PollManagerException, PollException {
        Poll poll = PollGateway.selectPollById(pollID);
        if (Objects.isNull(poll)) {
            throw new PollManagerException("The poll does not exist in our system.");
        }
        if (!userID.equals(poll.getUserID())){
            throw new PollManagerException("A poll may be deleted only by the user who has created it.");
        }
        if (poll.getStatus() == PollStatus.RELEASED) {
            throw new PollManagerException("The poll must be in created or running state.");
        }

        if (title.trim().isEmpty()) {
            throw new PollManagerException("Invalid Title! Please enter a proper title.");
        }
        if (question.trim().isEmpty()) {
            throw new PollManagerException("Invalid Question! Please enter a proper question.");
        }
        for (int i = 0; i < choices.size(); i++) {
            if (choices.get(i).getText().trim().isEmpty() || choices.get(i).getDescription().trim().isEmpty()) {
                throw new PollManagerException("Invalid Choice! Please enter a proper choice with text and description.");
            }
        }

        poll.setTitle(title);
        poll.setQuestion(question);
        poll.setChoices(choices);
        poll.setStatus(PollStatus.CREATED);
        PollGateway.updatePoll(poll);

    }

    public void clearPoll(String pollID) throws PollManagerException {
        Poll poll = PollGateway.selectPollById(pollID);
        if (Objects.isNull(poll)) {
            throw new PollManagerException("The poll does not exist in our system.");
        }
        if (poll.getStatus() == PollStatus.CREATED) {
            throw new PollManagerException("The poll must be in a running or released state.");
        }
        VoteGateway.DeleteVotes(pollID);
        if (poll.getStatus() == PollStatus.RELEASED) {
            PollGateway.updatePollStatus(poll.getPollID(),PollStatus.CREATED);
        }

    }

    public void closePoll(String pollID) throws PollManagerException {
        Poll poll = PollGateway.selectPollById(pollID);
        if (Objects.isNull(poll)) {
            throw new PollManagerException("The poll does not exist in our system.");
        }
        if (poll.getStatus() != PollStatus.RELEASED) {
            throw new PollManagerException("The poll must be in a released state to be closed.");
        }

        if (poll.getStatus() == PollStatus.RELEASED) {
            PollGateway.updatePollStatus(poll.getPollID(), PollStatus.CLOSED);
        }

    }

    public void runPoll(String pollID) throws PollManagerException {
        Poll poll = PollGateway.selectPollById(pollID);
        if (Objects.isNull(poll)) {
            throw new PollManagerException("The poll does not exist in our system.");
        }
        if (poll.getStatus() != PollStatus.CREATED) {
            throw new PollManagerException("The poll must be in created state.");
        }

        PollGateway.updatePollStatus(poll.getPollID(), PollStatus.RUNNING);
    }

    public void releasePoll(String pollID) throws PollManagerException {
        Poll poll = PollGateway.selectPollById(pollID);
        if (Objects.isNull(poll)) {
            throw new PollManagerException("The poll does not exist in our system.");
        }
        if (poll.getStatus() != PollStatus.RUNNING) {
            throw new PollManagerException("The poll must be in a running state to be released.");
        }
        PollGateway.updatePollStatus(poll.getPollID(), PollStatus.RELEASED);
    }

    public void unreleasePoll(String pollID) throws PollManagerException {
        Poll poll = PollGateway.selectPollById(pollID);
        if (Objects.isNull(poll)) {
            throw new PollManagerException("The poll does not exist in our system.");
        }
        if (poll.getStatus() != PollStatus.RELEASED) {
            throw new PollManagerException("The poll must be in a released state to be unreleased.");
        }

        PollGateway.updatePollStatus(poll.getPollID(), PollStatus.RUNNING);
    }

    public void vote(String pollID, int pin, int choiceID) throws PollManagerException {
        Poll poll = PollGateway.selectPollById(pollID);
        if (Objects.isNull(poll)) {
            throw new PollManagerException("The poll does not exist in our system.");
        }
        if (poll.getStatus() != PollStatus.RUNNING) {
            throw new PollManagerException("Failed to save vote since the poll is not in a running state.");
        } else if (!poll.isValidChoice(choiceID)) {
            throw new PollManagerException("This is not a valid choice.");
        } else {
            ArrayList<Participant> participants = VoteGateway.getAllVotesByPoll(poll.getPollID());
            for(int i = 0; i < participants.size(); ++i) {
                if (participants.get(i).getPIN()==pin) {
                    VoteGateway.updateVote(participants.get(i), choiceID);
                    return;
                }
            }

            VoteGateway.insertVotes(pin, choiceID);
        }
    }

    public Hashtable<Choice, Integer> getPollResults(String pollID) throws PollManagerException {
        Poll poll = PollGateway.selectPollById(pollID);
        if (Objects.isNull(poll)) {
            throw new PollManagerException("The poll does not exist in our system.");
        }
        if (poll.getStatus() != PollStatus.RELEASED) {
            throw new PollManagerException("Failed to retrieve poll results since the poll is not in a release state.");
        } else {
            ArrayList<Participant> participants= VoteGateway.getAllVotesByPoll(poll.getPollID());
            ArrayList<Choice> availableChoices = poll.getChoices();
            Hashtable<Choice, Integer> results = new Hashtable();
            availableChoices.forEach((choice) -> {
                results.put(choice, 0);
            });
            participants.forEach((participant) -> {
                Choice vote = participant.getVote();
                Choice key = this.getRealKey(results, vote);
                Integer value = (Integer)results.get(key);
                results.put(key, value + 1);
            });
            return results;
        }
    }

    public Hashtable<Choice, Integer> getPollResults(String pollID, String userID) throws PollManagerException {
        Poll poll = PollGateway.selectPollById(pollID);
        if (Objects.isNull(poll)) {
            throw new PollManagerException("The poll does not exist in our system.");
        }
        if (!poll.getUserID().equals(userID)) {
            throw new PollManagerException("Not the person who created the poll");
        } else {
            ArrayList<Participant> participants= VoteGateway.getAllVotesByPoll(poll.getPollID());
            ArrayList<Choice> availableChoices = poll.getChoices();
            Hashtable<Choice, Integer> results = new Hashtable();
            availableChoices.forEach((choice) -> {
                results.put(choice, 0);
            });
            participants.forEach((participant) -> {
                Choice vote = participant.getVote();
                Choice key = this.getRealKey(results, vote);
                Integer value = (Integer)results.get(key);
                results.put(key, value + 1);
            });
            return results;
        }
    }

    public void downloadPollDetails(PrintWriter output, StringBuilder filename, String pollID, String format) throws PollManagerException {
        Poll poll = PollGateway.selectPollById(pollID);
        if (Objects.isNull(poll)) {
            throw new PollManagerException("The poll does not exist in our system.");
        }
        if (poll.getStatus() != PollStatus.RELEASED) {
            throw new PollManagerException("The poll must be released to download poll details.");
        }
        //Edit filename ------------------------------------------------------
        String pollTitle = poll.getTitle();
        filename.append(pollTitle).append("-").append(poll.getReleasedAt());
        if (format.equals("txt")) {
            filename.append(".txt");
        } else if (format.equals("xml")) {
            filename.append(".xml");
        } else if (format.equals("json")) {
            filename.append(".json");
        }

        //Edit .txt file info -------------------------------------------------
        Hashtable<Choice, Integer> results = getPollResults(poll.getPollID());
        ArrayList<Participant> participants = VoteGateway.getAllVotesByPoll(poll.getPollID());

        PollResults toWrite = new PollResults();
        toWrite.setTitle(poll.getTitle());
        toWrite.setQuestion(poll.getQuestion());

        Hashtable<String, Integer> totals = new Hashtable<>();
        results.forEach((s, integer) -> {
            totals.put(s.getText(), integer);
        });
        toWrite.setChoiceAndTotalVote(totals);;

        Hashtable<Integer, String> votes = new Hashtable<>();
        participants.forEach(participant -> {
            votes.put(participant.getPIN(), participant.getVote().getText());
        });
        toWrite.setPinAndChoice(votes);

        if (format.equals("txt")) {
            StringBuilder pollInfo = new StringBuilder();
            pollInfo.append("Poll Title: ").append(toWrite.getTitle()).append("\n");
            pollInfo.append("Poll Question: ").append(toWrite.getQuestion()).append("\n");
            pollInfo.append("\n\nNumber of Votes for Each Choice \n\n");
            results.forEach((s, integer) -> {
                pollInfo.append(s.getText()).append("\t -").append(s.getDescription()).append("\t ----> \t").append(integer.toString()).append("\n");
            });
            pollInfo.append("\n\nVotes \n\n");
            participants.forEach(participant -> {
                pollInfo.append(participant.getPIN()).append("\t ---> \t").append(participant.getVote().getText()).append("\n");
            });
            output.write(String.valueOf(pollInfo));
        } else if (format.equals("xml")) {
            JAXBContext jaxbContext = null;
            try {
                jaxbContext = org.eclipse.persistence.jaxb.JAXBContextFactory
                        .createContext(new Class[]{PollResults.class}, null);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                jaxbMarshaller.marshal(toWrite, output);
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        } else if (format.equals("json")) {
            Gson g = new GsonBuilder().setPrettyPrinting().create();
            g.toJson(toWrite, output);

        }


    }

    private Choice getRealKey(Hashtable<Choice, Integer> hashtable, Choice someChoice) {
        for (Map.Entry<Choice, Integer> entry : hashtable.entrySet()) {
            Choice choice = entry.getKey();
            Integer integer = entry.getValue();
            if ((choice.getText().compareTo(someChoice.getText()) == 0)
                    && (choice.getDescription().compareTo(someChoice.getDescription()) == 0)) {
                return choice;
            }
        }
        return null;
    }
}