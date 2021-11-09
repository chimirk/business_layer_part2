package com.pollmanager;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

public class PollManager {
    private Poll poll;
    private ArrayList<Participant> participants;
    private Timestamp pollReleasedTime;

    public Poll getPoll() {
        return poll;
    }

    public void createPoll(String title, String question, ArrayList<Choice> choices) throws PollManagerException, PollException {

        if (Objects.nonNull(this.poll)) {
            throw new PollManagerException("There is currently a poll in the system.");
        }
        if(title.trim().isEmpty()){
            throw new PollManagerException("Invalid Title! Please enter a proper title.");
        }
        if(question.trim().isEmpty()){
            throw new PollManagerException("Invalid Question! Please enter a proper question.");
        }
        for (int i = 0; i < choices.size(); i++) {
            if(choices.get(i).getText().trim().isEmpty() || choices.get(i).getDescription().trim().isEmpty()){
                throw new PollManagerException("Invalid Choice! Please enter a proper choice with text and description.");
            }
        }

        this.poll = new Poll(title, question);
        this.poll.setChoices(choices);
        this.poll.setStatus(PollStatus.CREATED);
        this.participants = new ArrayList<>();


    }

    public void updatePoll(String title, String question, ArrayList<Choice> choices) throws PollManagerException, PollException {

        if (Objects.isNull(this.poll)) {
            throw new PollManagerException("There is no poll in the system.");
        }
        if (this.poll.getStatus() == PollStatus.RELEASED) {
            throw new PollManagerException("The poll must be in created or running state.");
        }

        if(title.trim().isEmpty()){
            throw new PollManagerException("Invalid Title! Please enter a proper title.");
        }
        if(question.trim().isEmpty()){
            throw new PollManagerException("Invalid Question! Please enter a proper question.");
        }
        for (int i = 0; i < choices.size(); i++) {
            if(choices.get(i).getText().trim().isEmpty() || choices.get(i).getDescription().trim().isEmpty()){
                throw new PollManagerException("Invalid Choice! Please enter a proper choice with text and description.");
            }
        }

        this.poll.setTitle(title);
        this.poll.setQuestion(question);
        this.poll.setChoices(choices);
        this.poll.setStatus(PollStatus.CREATED);
        this.participants = new ArrayList<>();

    }

    public void clearPoll() throws PollManagerException {

        if (Objects.isNull(this.poll)) {
            throw new PollManagerException("There is no poll in the system.");
        }
        if (this.poll.getStatus() == PollStatus.CREATED) {
            throw new PollManagerException("The poll must be in a running or released state.");
        }
        this.participants = new ArrayList<>();
        if (this.poll.getStatus() == PollStatus.RELEASED) {
            this.poll.setStatus(PollStatus.CREATED);
            this.pollReleasedTime = null;
        }

    }

    public void closePoll() throws PollManagerException {

        if (Objects.isNull(this.poll)) {
            throw new PollManagerException("There is no poll in the system.");
        }
        if (this.poll.getStatus() != PollStatus.RELEASED) {
            throw new PollManagerException("The poll must be in a released state to be closed.");
        }

        this.poll = null;
        this.participants = null;
        pollReleasedTime = null;
    }

    public void runPoll() throws PollManagerException {
        if (Objects.isNull(this.poll)) {
            throw new PollManagerException("There is no poll in the system.");
        }
        if (this.poll.getStatus() != PollStatus.CREATED) {
            throw new PollManagerException("The poll must be in created state.");
        }

        this.poll.setStatus(PollStatus.RUNNING);
    }

    public void releasePoll() throws PollManagerException {
        if (Objects.isNull(this.poll)) {
            throw new PollManagerException("There is no poll in the system.");
        }
        if (this.poll.getStatus() != PollStatus.RUNNING) {
            throw new PollManagerException("The poll must be in a running state to be released.");
        }

        this.poll.setStatus(PollStatus.RELEASED);
        this.pollReleasedTime = new Timestamp(System.currentTimeMillis());
    }

    public void unreleasePoll() throws PollManagerException {
        if (Objects.isNull(this.poll)) {
            throw new PollManagerException("There is no poll in the system.");
        }
        if (this.poll.getStatus() != PollStatus.RELEASED) {
            throw new PollManagerException("The poll must be in a released state to be unreleased.");
        }

        this.poll.setStatus(PollStatus.RUNNING);
        this.pollReleasedTime = null;
    }

    public void vote(String participant, Choice choice) throws PollManagerException {

        if (this.poll.getStatus() != PollStatus.RUNNING) {
            throw new PollManagerException("Failed to save vote since the poll is not in a running state.");
        } else if (!this.poll.isValidChoice(choice)) {
            throw new PollManagerException("This is not a valid choice.");
        } else {
            Participant participant1 = new Participant(participant, choice);

            for(int i = 0; i < this.participants.size(); ++i) {
                if (((Participant)this.participants.get(i)).getSessionID().compareTo(participant) == 0) {
                    ((Participant)this.participants.get(i)).setVote(choice);
                    return;
                }
            }

            this.participants.add(participant1);
        }
    }

    public Hashtable<Choice, Integer> getPollResults() throws PollManagerException {

        if (this.poll.getStatus() != PollStatus.RELEASED) {
            throw new PollManagerException("Failed to retrieve poll results since the poll is not in a release state.");
        } else {
            ArrayList<Choice> availableChoices = this.poll.getChoices();
            Hashtable<Choice, Integer> results = new Hashtable();
            availableChoices.forEach((choice) -> {
                results.put(choice, 0);
            });
            this.participants.forEach((participant) -> {
                Choice vote = participant.getVote();
                Choice key = this.getRealKey(results, vote);
                Integer value = (Integer)results.get(key);
                results.put(key, value + 1);
            });
            this.poll.setStatus(PollStatus.RELEASED);
            return results;
        }
    }

    public void downloadPollDetails(PrintWriter output, StringBuilder filename) throws PollManagerException {

        if (Objects.isNull(this.poll)) {
            throw new PollManagerException("There is no poll in the system.");
        }
        if(this.poll.getStatus() != PollStatus.RELEASED){
            throw new PollManagerException("The poll must be released to download poll details.");
        }
        //Edit filename
        String pollTitle = poll.getTitle();

        filename.append(pollTitle).append("-").append(this.pollReleasedTime).append(".txt");

        //Edit .txt file info
        StringBuilder pollInfo = new StringBuilder();
        pollInfo.append("Poll Title: ").append(this.poll.title).append("\n");
        pollInfo.append("Poll Question: ").append(this.poll.question).append("\n");

        Hashtable<Choice, Integer> results = getPollResults();

        pollInfo.append("\n\nNumber of Votes for Each Choice \n\n");
        results.forEach((s, integer) -> {
            pollInfo.append(s.getText()).append("\t -").append(s.getDescription()).append("\t ----> \t").append(integer.toString()).append("\n");
        });
        pollInfo.append("\n\nVotes \n\n");
        participants.forEach(participant -> {
            pollInfo.append(participant.getSessionID()).append("\t ---> \t").append(participant.getVote().getText()).append("\n");
        });
        output.write(String.valueOf(pollInfo));

    }

    private Choice getRealKey(Hashtable<Choice,Integer> hashtable, Choice someChoice) {
        for (Map.Entry<Choice, Integer> entry : hashtable.entrySet()) {
            Choice choice = entry.getKey();
            Integer integer = entry.getValue();
            if ((choice.getText().compareTo(someChoice.getText()) == 0)
                    && (choice.getDescription().compareTo(someChoice.getDescription()) == 0)) {
                return choice;
            };
        }
        return null;
    }


}