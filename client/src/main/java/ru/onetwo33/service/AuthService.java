package ru.onetwo33.service;

import java.sql.SQLException;

public interface AuthService {
    String getNickByLoginPass(String login, String pass) throws SQLException, ClassNotFoundException;
}
