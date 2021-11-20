package com.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbConfig {
    private static final String jdbcURL = "jdbc:mysql://localhost:3306/poll_app";
    private static final String jdbcUserName = "root";
    private static final String jdbcPassword = "";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            //Class.forName("com.mysql.jdbc.Driver");
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, jdbcUserName, jdbcPassword);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    private void printSQLException(SQLException sqlException) {
        for (Throwable throwable: sqlException) {
            throwable.printStackTrace(System.err);
            System.err.println("SQL State:" + sqlException.getSQLState());
            System.err.println("Error code: " + sqlException.getErrorCode());
            System.err.println("Message: " + sqlException.getMessage());
            Throwable t = sqlException.getCause();
            while (t != null ) {
                System.out.println("Cause: " + t);
                t = t.getCause();
            }
        }
    }
}
