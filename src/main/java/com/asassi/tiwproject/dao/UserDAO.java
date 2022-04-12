package com.asassi.tiwproject.dao;

import com.asassi.tiwproject.beans.UserBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private final Connection dbConnection;

    public UserDAO(Connection connection) {
        this.dbConnection = connection;
    }

    public List<UserBean> findUsersByName(String username) throws SQLException {
        List<UserBean> users = new ArrayList<>();
        String query = "SELECT * FROM Users WHERE NameHash = ?";
        PreparedStatement statement = dbConnection.prepareStatement(query);
        statement.setString(1, username);
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            UserBean user = new UserBean(result.getString("NameHash"), result.getString("PasswordHash"));
            users.add(user);
        }
        result.close();
        statement.close();

        return users;
    }

    public void registerUser(UserBean user) throws SQLException {
        String query = "INSERT INTO Users VALUES (?, ?)";

        PreparedStatement statement = dbConnection.prepareStatement(query);
        statement.setString(1, user.getName());
        statement.setString(2, user.getPasswordHash());
        statement.executeUpdate();
        statement.close();
    }
    
}
