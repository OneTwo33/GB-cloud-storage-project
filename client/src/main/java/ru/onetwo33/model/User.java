package ru.onetwo33.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private int id;
    private String login;
    private String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public User userBuilder(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.login = set.getString("login");
        this.password = set.getString("password");
        return this;
    }
}
