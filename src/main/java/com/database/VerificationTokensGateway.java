package com.database;

import com.config.dbConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

public class VerificationTokensGateway {
    private static final String INSERT_TOKEN_SQL ="INSERT INTO poll_app.verification_tokens" +
            "(verification_token, username) VALUES" + "(?,?);";

    public static void saveToken(UUID token, String userName) {
        try(Connection connection = dbConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TOKEN_SQL)) {
            preparedStatement.setString(1, token.toString());
            preparedStatement.setString(2, userName);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
