package ru.onetwo33.service.impl;

import ru.onetwo33.model.User;
import ru.onetwo33.service.AuthService;
import ru.onetwo33.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthServiceImpl implements AuthService {
    @Override
    public String getNickByLoginPass(String login, String pass) throws SQLException, ClassNotFoundException {
        User user = null;

        Connection conn = DbConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE login = ? AND password = ?");
        stmt.setString(1, login);
        stmt.setString(2, pass);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            user = new User().userBuilder(rs);
        }

        if (user != null) return user.getLogin();
        else
            return null;
    }
}
