package com.asassi.tiwproject.dao;

import com.asassi.tiwproject.beans.FolderBean;
import com.asassi.tiwproject.constants.FolderType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FolderDAO {

    private final Connection dbConnection;

    public FolderDAO(Connection connection) {
        this.dbConnection = connection;
    }

    public List<FolderBean> findFoldersByUsername(String username, FolderType folderType) throws SQLException {
        List<FolderBean> folders = new ArrayList<>();
        String query = "SELECT * FROM Folders WHERE OwnerUsername = ? AND FolderType = ? ORDER BY (FolderNumber)";
        PreparedStatement statement = dbConnection.prepareStatement(query);
        statement.setString(1, username);
        statement.setInt(2, folderType.getRawValue());
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            folders.add(new FolderBean(result.getString("OwnerUsername"), result.getInt("FolderNumber"), result.getString("Name"), result.getDate("CreationDate"), result.getInt("FolderType"), result.getString("ParentFolder_OwnerUsername"), result.getInt("ParentFolder_FolderNumber")));
        }
        result.close();
        statement.close();

        return folders;
    }

    public List<FolderBean> findFoldersByUsernameAndFolderName(String username, String folderName, FolderType folderType) throws SQLException {
        List<FolderBean> folders = new ArrayList<>();
        String query = "SELECT * FROM Folders WHERE OwnerUsername = ? AND Name = ? AND FolderType = ? ORDER BY (FolderNumber)";
        PreparedStatement statement = dbConnection.prepareStatement(query);
        statement.setString(1, username);
        statement.setString(2, folderName);
        statement.setInt(3, folderType.getRawValue());
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            folders.add(new FolderBean(result.getString("OwnerUsername"), result.getInt("FolderNumber"), result.getString("Name"), result.getDate("CreationDate"), result.getInt("FolderType"), result.getString("ParentFolder_OwnerUsername"), result.getInt("ParentFolder_FolderNumber")));
        }
        result.close();
        statement.close();

        return folders;
    }

    public void addFolder(FolderBean folder) throws SQLException {
        String query = "INSERT INTO Folders VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = dbConnection.prepareStatement(query);
        statement.setString(1, folder.getUsername());
        statement.setInt(2, folder.getFolderNumber());
        statement.setString(3, folder.getName());
        statement.setDate(4, folder.getCreationDate());
        statement.setInt(5, folder.getFolderType().getRawValue());
        statement.setString(6, folder.getParentFolder_username());
        Integer parentFolder_number = folder.getParentFolder_folderNumber();
        if (parentFolder_number == null) {
            statement.setNull(7, Types.INTEGER);
        } else {
            statement.setInt(7, parentFolder_number);
        }
        statement.executeUpdate();
        statement.close();
    }

}