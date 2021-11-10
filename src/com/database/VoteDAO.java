package com.database;

import com.pollmanager.Participant;
import com.config.dbConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class VoteDAO {
    private static final String INSERT_VOTE_SQL =
            "INSERT INTO votes" + "(pin, choice_id) VALUES" + " (?, ?);";
    private static final String GET_CHOICE_ID =
            "SELECT choice_id FROM choices WHERE text = ?;";

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
                preparedStatement.setString(1, participants.get(i).getPIN());
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
} //end class
