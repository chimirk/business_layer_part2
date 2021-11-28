package com.database;

import com.config.dbConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;


public class UserGateway {


    private static final String INSERT_USER_INFO_SQL ="INSERT INTO poll_app.users" +
            "(username, email, full_name) VALUES" + "(?,?,?);";

    private static final String UPDATE_USER_PASSWORD_SQL ="UPDATE poll_app.users SET password = ? WHERE username = ?;";
    private static final String UPDATE_USER_ACTIVATION_STATUS_SQL ="UPDATE poll_app.users SET isValidated = ? WHERE username = ?;";


    public static void saveUser(String userName, String userEmail, String userFullName) {
        try(Connection connection = dbConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_INFO_SQL)) {
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, userEmail);
            preparedStatement.setString(3, userFullName);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void savePassword(String userPassword, String userName) {
        try(Connection connection = dbConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_PASSWORD_SQL)) {
            preparedStatement.setString(1, userPassword);
            preparedStatement.setString(2, userName);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateActivationStatus(String newStatus, String userName) {
        try(Connection connection = dbConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_ACTIVATION_STATUS_SQL)) {
            preparedStatement.setString(1, newStatus);
            preparedStatement.setString(2, userName);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

