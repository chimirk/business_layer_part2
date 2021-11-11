package com.database;

import com.pollmanager.Choice;
import com.pollmanager.Participant;
import com.config.dbConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class VoteGateway {
    private static final String INSERT_VOTE_SQL =
            "INSERT INTO votes" + "(pin, choice_id) VALUES" + " (?, ?);";
    private static final String UPDATE_VOTE_SQL =
            "UPDATE votes SET choice_id = ? WHERE pin = ? AND choice_id = ?;";
    private  static final String GET_ALL_VOTES_BY_POLLID =
            "SELECT * FROM votes JOIN choices ON votes.choice_id=choices.choice_id WHERE poll_id=?;";
    private  static final String DELETE_VOTES_BY_POLLID =
            "DELETE from votes WHERE pin = ? AND choice_id = ?;";


    public static void insertVotes(int pin, int choiceID) {
        try (Connection connection = dbConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_VOTE_SQL)){
            preparedStatement.setInt(1, pin);
            preparedStatement.setInt(2, choiceID);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

    } //end method

    public static void updateVote(Participant oldVote, int newChoice){
        try (Connection connection = dbConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_VOTE_SQL)){
            preparedStatement.setInt(1, newChoice);
            preparedStatement.setInt(2, oldVote.getPIN());
            preparedStatement.setInt(3, oldVote.getVote().getChoiceID());
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Participant> getAllVotesByPoll(String pollID) {
        ArrayList<Participant> participants = new ArrayList<>();
        try(Connection connection = dbConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_VOTES_BY_POLLID)){
            preparedStatement.setString(1,pollID);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    Choice choice = new Choice(resultSet.getInt("choice_id") ,resultSet.getString("text"), resultSet.getString("description"));
                    participants.add(new Participant(
                            resultSet.getInt("pin"),
                            choice
                    ));

                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return participants;
    }

    public static void DeleteVotes(String pollID){
        ArrayList<Participant> participants = getAllVotesByPoll(pollID);
        try{
            Connection connection = dbConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_VOTES_BY_POLLID);
            connection.setAutoCommit(false);
            participants.forEach(participant -> {
                try {
                    preparedStatement.setInt(1, participant.getPIN());
                    preparedStatement.setInt(2, participant.getVote().getChoiceID());
                    preparedStatement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            });
            try {
                preparedStatement.executeBatch();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            connection.commit();
            connection.setAutoCommit(true);
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
} //end class
