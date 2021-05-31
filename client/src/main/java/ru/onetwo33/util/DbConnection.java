package ru.onetwo33.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static final String DRIVER = "org.sqlite.JDBC";
    private static final String DB = "";
    private static final String USER = "";
    private static final String PASSWORD = "";
    private static Connection connection;

    public DbConnection() {
    }

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if (connection == null) {
            connection = initConnection();
        }

        return connection;
    }

    private static Connection initConnection() throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER);
        return DriverManager.getConnection(DB, USER, PASSWORD);
    }
}
