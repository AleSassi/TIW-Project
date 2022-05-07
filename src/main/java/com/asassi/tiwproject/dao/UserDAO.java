package com.asassi.tiwproject.dao;

import com.asassi.tiwproject.beans.UserBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DAO {

    public UserDAO(Connection connection) {
        super(connection);
    }

    public List<UserBean> findUsersByName(String username) throws SQLException {
        List<UserBean> users = new ArrayList<>();
        String query = "SELECT * FROM Users WHERE NameHash = ?";
        PreparedStatement statement = getDbConnection().prepareStatement(query);
        statement.setString(1, username);
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            UserBean user = new UserBean(result.getString("NameHash"), result.getString("PasswordHash"), result.getString("MailAddr"));
            users.add(user);
        }
        result.close();
        statement.close();

        return users;
    }

    public void registerUser(UserBean user) throws SQLException {
        String query = "INSERT INTO Users VALUES (?, ?, ?)";

        PreparedStatement statement = getDbConnection().prepareStatement(query);
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPasswordHash());
        statement.setString(3, user.getEmail());
        statement.executeUpdate();
        statement.close();
    }
    
}
