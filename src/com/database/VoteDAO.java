package com.database;

import com.pollmanager.Choice;
import com.pollmanager.Participant;
import com.config.dbConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class VoteDAO {
    private static final String INSERT_VOTE_SQL =
            "INSERT INTO votes" + "(pin, choice_id) VALUES" + " (?, ?);";
    private static final String GET_CHOICE_ID =
            "SELECT choice_id FROM choices WHERE text = ?;";
    private  static final String GET_ALL_VOTES_BY_POLLID =
            "SELECT * FROM votes JOIN choices ON votes.choice_id=choices.choice_id WHERE poll_id=?;";
    private  static final String DELETE_VOTES_BY_POLLID =
            "DELETE from votes WHERE pin = ? AND choice_id = ?;";

    public void insertVotes(ArrayList<Participant> participants) {
        ArrayList<Integer> choice_ids = new ArrayList();
        try {
            Connection connection = dbConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(GET_CHOICE_ID);
            for (Participant participant : participants) {
                preparedStatement.setString(1, participant.getVote().getText());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    choice_ids.add(resultSet.getInt("choice_id"));
                }
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Connection connection = dbConfig.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_VOTE_SQL);

            for (int i = 0; i < choice_ids.size(); i++ ) {
                preparedStatement.setInt(1, participants.get(i).getPIN());
                preparedStatement.setInt(2, choice_ids.get(i));
                preparedStatement.addBatch();
            }
            try {
                preparedStatement.executeBatch();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            connection.commit();
            connection.setAutoCommit(true);
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    } //end method

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
