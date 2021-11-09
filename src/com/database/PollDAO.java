package com.database;

import com.config.dbConfig;
import com.pollmanager.Choice;
import com.pollmanager.Poll;
import com.pollmanager.PollException;

import java.sql.*;
import java.util.ArrayList;

public class PollDAO {
    private static final String INSERT_POLL_SQL =
            "INSERT INTO poll" + "(title, question, status) VALUES" + " (?, ?, ?);";
    private static final String INSERT_POLL_CHOICES_SQL =
            "INSERT INTO choices" + "(poll_id, text, description) VALUES" + " (?, ?, ?);";
    private static final String SELECT_POLL_BY_ID_SQL =
            "SELECT * FROM poll WHERE id = ?;";
    private static final String SELECT_CHOICES_BY_ID_SQL =
            "SELECT * FROM choices WHERE poll_id = ?;";
    private static final  String SELECT_ALL_CHOICES_BY_ID =
            "SELECT * from choices WHERE poll_id = ";
    private static final String SELECT_ALL_POLLS_SQL =
            "SELECT * FROM poll;";
    private static final String UPDATE_POLL_BY_ID_SQL =
            "UPDATE poll SET title = ?, question = ?, status = ? WHERE id = ?;";
    private static final String UPDATE_POLL_CHOICES_BY_ID_SQL =
            "UPDATE choices SET text = ?, description = ? WHERE poll_id = ?;";
    private static final String DELETE_POLL_SQL =
            "DELETE from poll WHERE id = ?;";


    public void insertPoll(Poll poll) {
        int pollID = -1;
        try(Connection connection = dbConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_POLL_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, poll.getTitle());
            preparedStatement.setString(2, poll.getQuestion());
            preparedStatement.setString(3, String.valueOf(poll.getStatus()));
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                pollID = generatedKeys.getInt(1);
            } else {
                throw new Exception("Error. poll ID is not available. Error coming from Poll Insertion");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try(Connection connection = dbConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_POLL_CHOICES_SQL)) {
            ArrayList<Choice> choices = poll.getChoices();
            if (pollID != -1) {
                for (Choice choice : choices) {
                    preparedStatement.setInt(1, pollID);
                    preparedStatement.setString(2, choice.getText());
                    preparedStatement.setString(3, choice.getDescription());
                    preparedStatement.executeUpdate();
                }
            } else {
                throw new Exception("Error. poll ID is not available. Error coming from choices insertion");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Poll selectPollById(int id) {
        Poll poll = null;
        try (Connection connection = dbConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_POLL_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    poll = new Poll(
                            resultSet.getString("title"),
                            resultSet.getString("question")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection connection = dbConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CHOICES_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                ArrayList<Choice> choices = new ArrayList<>();
                while (resultSet.next()) {
                    choices.add(new Choice(
                            resultSet.getString("text"),
                            resultSet.getString("description"))
                    );
                }
                if (poll != null) {
                    poll.setChoices(choices);
                }
            } catch (PollException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return poll;
    }

    public ArrayList<Poll> selectAllPolls() {
        ArrayList<Poll> polls = new ArrayList<>();
        try(Connection connection = dbConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_POLLS_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                polls.add(new Poll(
                        resultSet.getString("title"),
                        resultSet.getString("question"))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return polls;
    }

    public boolean updatePoll(Poll poll, int id) {
        boolean rowUpdated = false;
        try(Connection connection = dbConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_POLL_BY_ID_SQL);) {
            preparedStatement.setString(1, poll.getTitle());
            preparedStatement.setString(2, poll.getQuestion());
            preparedStatement.setString(3, String.valueOf(poll.getStatus()));
            preparedStatement.setInt(4, id);
            rowUpdated = preparedStatement.executeUpdate() > 0;
            try(Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet resultSet = statement.executeQuery(SELECT_ALL_CHOICES_BY_ID + id)) {
                resultSet.first();                        // set cursor to the first row in ResultSet
                ArrayList<Choice> choices = poll.getChoices();
                for (Choice choice : choices) {
                    resultSet.updateString("text", choice.getText());
                    resultSet.updateString("description", choice.getDescription());
                    resultSet.updateRow();
                    resultSet.next();                     // move to next row in ResultSet
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowUpdated;
    }


    public boolean deletePoll(int id) {
        boolean rowDeleted = false;
        try(Connection connection = dbConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_POLL_SQL)) {
            preparedStatement.setInt(1, id);
            rowDeleted = preparedStatement.executeUpdate() > 0;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return rowDeleted;
    }
}
