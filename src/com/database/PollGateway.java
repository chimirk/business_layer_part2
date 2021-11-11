package com.database;

import com.config.dbConfig;
import com.pollmanager.Choice;
import com.pollmanager.Poll;
import com.pollmanager.PollException;
import com.pollmanager.PollStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class PollGateway {
    private static final String INSERT_POLL_SQL =
            "INSERT INTO poll" + "(poll_id, title, question, created_at, status, user_id) VALUES" + " (?, ?, ?,?, ?, ?);";
    private static final String INSERT_POLL_CHOICES_SQL =
            "INSERT INTO choices" + "(poll_id, text, description) VALUES" + " (?, ?, ?);";
    private static final String SELECT_POLL_BY_ID_SQL =
            "SELECT * FROM poll WHERE poll_id = ?;";
    private static final String SELECT_CHOICES_BY_ID_SQL =
            "SELECT * FROM choices WHERE poll_id = ?;";
    private static final String SELECT_ALL_POLLS_SQL =
            "SELECT * FROM poll;";
    private static final String UPDATE_POLL_STATUS_BY_ID_SQL =
            "UPDATE poll SET status = ?, released_at = ? WHERE poll_id = ?;";
    private static final String UPDATE_POLL_BY_ID_SQL =
            "UPDATE poll SET title = ?, question = ?, status = ? WHERE poll_id = ?;";

    private static final String DELETE_CHOICES_BY_POLL_SQL =
            "DELETE from choices WHERE poll_id = ?;";
    private static final String DELETE_POLL_SQL =
            "DELETE from poll WHERE poll_id = ?;";


    public static void insertPoll(Poll poll) {
        String pollID = poll.getPollID();

        try(Connection connection = dbConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_POLL_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, pollID);
            preparedStatement.setString(2, poll.getTitle());
            preparedStatement.setString(3, poll.getQuestion());
            preparedStatement.setString(4,String.valueOf(poll.getCreatedAt()));
            preparedStatement.setString(5, String.valueOf(poll.getStatus()));
            preparedStatement.setString(6, poll.getUserID());
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try(Connection connection = dbConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_POLL_CHOICES_SQL)) {
            ArrayList<Choice> choices = poll.getChoices();

                for (Choice choice : choices) {
                    preparedStatement.setString(1, pollID);
                    preparedStatement.setString(2, choice.getText());
                    preparedStatement.setString(3, choice.getDescription());
                    preparedStatement.executeUpdate();
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Poll selectPollById(String pollID) {
        Poll poll = null;
        try (Connection connection = dbConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_POLL_BY_ID_SQL)) {
            preparedStatement.setString(1, pollID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    poll = new Poll(
                            resultSet.getString("poll_id"),
                            resultSet.getString("title"),
                            resultSet.getString("question"),
                            Timestamp.valueOf(resultSet.getString("created_at")),
                            resultSet.getString("user_id")
                    );
                    poll.setStatus(PollStatus.valueOf(resultSet.getString("status")));
                    if(Objects.nonNull(resultSet.getString("released_at"))){
                        poll.setReleasedAt(Timestamp.valueOf(resultSet.getString("released_at")));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection connection = dbConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CHOICES_BY_ID_SQL)) {
            preparedStatement.setString(1, pollID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                ArrayList<Choice> choices = new ArrayList<>();
                while (resultSet.next()) {
                    choices.add(new Choice(
                            resultSet.getInt("choice_id"),
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

    public static ArrayList<Poll> selectAllPolls() {
        ArrayList<Poll> polls = new ArrayList<>();
        try(Connection connection = dbConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_POLLS_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                polls.add(new Poll(
                        resultSet.getString("poll_id"),
                        resultSet.getString("title"),
                        resultSet.getString("question"),
                        Timestamp.valueOf(resultSet.getString("created_at")),
                        resultSet.getString("user_id"))
                );
                polls.get(polls.size()-1).setStatus(PollStatus.valueOf(resultSet.getString("status")));
                if(Objects.nonNull(resultSet.getString("released_at"))){
                    polls.get(polls.size()-1).setReleasedAt(Timestamp.valueOf(resultSet.getString("released_at")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return polls;
    }

    public static boolean updatePollStatus(String pollId, PollStatus status) {
        boolean rowUpdated = false;
        try(Connection connection = dbConfig.getConnection()){
            try(PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_POLL_STATUS_BY_ID_SQL)) {
                preparedStatement.setString(1, String.valueOf(status));
                if(status == PollStatus.RELEASED){
                    preparedStatement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                }else{
                    preparedStatement.setNull(2, Types.TIMESTAMP);
                }
                preparedStatement.setString(3, pollId);
                rowUpdated = preparedStatement.executeUpdate() > 0;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rowUpdated;
    }

    public static boolean updatePoll(Poll poll) {
        boolean rowUpdated = false;
        try(Connection connection = dbConfig.getConnection()){
            try(PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_POLL_BY_ID_SQL)) {
            preparedStatement.setString(1, poll.getTitle());
            preparedStatement.setString(2, poll.getQuestion());
            preparedStatement.setString(3, String.valueOf(poll.getStatus()));
            preparedStatement.setString(4, poll.getPollID());
            rowUpdated = preparedStatement.executeUpdate() > 0;
            }

            try (PreparedStatement deleteChoices = connection.prepareStatement(DELETE_CHOICES_BY_POLL_SQL)){
                deleteChoices.setString(1,poll.getPollID());
                deleteChoices.executeUpdate();
            }catch (Exception e) {
                e.printStackTrace();
            }

            try(PreparedStatement insertChoices = connection.prepareStatement(INSERT_POLL_CHOICES_SQL)) {
                ArrayList<Choice> choices = poll.getChoices();
                for (Choice choice : choices) {
                    insertChoices.setString(1, poll.getPollID());
                    insertChoices.setString(2, choice.getText());
                    insertChoices.setString(3, choice.getDescription());
                    insertChoices.executeUpdate();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowUpdated;
    }


    public static boolean deletePoll(String pollID) {
        boolean rowDeleted = false;
        try(Connection connection = dbConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_POLL_SQL)) {
            preparedStatement.setString(1, pollID);
            rowDeleted = preparedStatement.executeUpdate() > 0;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return rowDeleted;
    }
}
