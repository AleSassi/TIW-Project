package com.asassi.tiwproject.dao;

import com.asassi.tiwproject.beans.FolderBean;
import com.asassi.tiwproject.constants.FolderType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FolderDAO extends DAO {

    public FolderDAO(Connection connection) {
        super(connection);
    }

    public List<FolderBean> findFoldersByUsername(String username, FolderType folderType) throws SQLException {
        List<FolderBean> folders = new ArrayList<>();
        String query = "SELECT * FROM Folders WHERE OwnerUsername = ? AND ParentFolder_FolderNumber is null ORDER BY CreationDate";
        if (folderType == FolderType.Subfolder) {
            query = "SELECT * FROM Folders WHERE OwnerUsername = ? AND ParentFolder_FolderNumber is not null ORDER BY CreationDate";
        }
        PreparedStatement statement = getDbConnection().prepareStatement(query);
        statement.setString(1, username);
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            folders.add(new FolderBean(result.getString("OwnerUsername"), result.getInt("FolderNumber"), result.getString("Name"), result.getTimestamp("CreationDate").toLocalDateTime(), result.getInt("ParentFolder_FolderNumber")));
        }
        result.close();
        statement.close();

        return folders;
    }

    public List<FolderBean> findFoldersByUsernameAndFolderNumber(String username, int folderNumber, FolderType folderType) throws SQLException {
        List<FolderBean> folders = new ArrayList<>();
        String query = "SELECT * FROM Folders WHERE OwnerUsername = ? AND FolderNumber = ? AND ParentFolder_FolderNumber is null ORDER BY CreationDate";
        if (folderType == FolderType.Subfolder) {
            query = "SELECT * FROM Folders WHERE OwnerUsername = ? AND FolderNumber = ? AND ParentFolder_FolderNumber is not null ORDER BY CreationDate";
        }
        PreparedStatement statement = getDbConnection().prepareStatement(query);
        statement.setString(1, username);
        statement.setInt(2, folderNumber);
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            folders.add(new FolderBean(result.getString("OwnerUsername"), result.getInt("FolderNumber"), result.getString("Name"), result.getTimestamp("CreationDate").toLocalDateTime(), result.getInt("ParentFolder_FolderNumber")));
        }
        result.close();
        statement.close();

        return folders;
    }

    public List<FolderBean> findSubfoldersOfFolder(String username, int parentFolderNumber) throws SQLException {
        List<FolderBean> folders = new ArrayList<>();
        String query = "SELECT * FROM Folders WHERE ParentFolder_FolderNumber = ? AND OwnerUsername = ? ORDER BY CreationDate";
        PreparedStatement statement = getDbConnection().prepareStatement(query);
        statement.setInt(1, parentFolderNumber);
        statement.setString(2, username);
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            folders.add(new FolderBean(result.getString("OwnerUsername"), result.getInt("FolderNumber"), result.getString("Name"), result.getTimestamp("CreationDate").toLocalDateTime(), result.getInt("ParentFolder_FolderNumber")));
        }
        result.close();
        statement.close();

        return folders;
    }

    public void addFolder(FolderBean folder) throws SQLException {
        String query = "INSERT INTO Folders VALUES (?, ?, ?, ?, ?)";

        PreparedStatement statement = getDbConnection().prepareStatement(query);
        statement.setInt(1, folder.getFolderNumber());
        statement.setString(2, folder.getUsername());
        statement.setString(3, folder.getName());
        statement.setTimestamp(4, Timestamp.valueOf(folder.getCreationDate()));
        Integer parentFolder_number = folder.getParentFolder_folderNumber();
        if (parentFolder_number == null) {
            statement.setNull(5, Types.INTEGER);
        } else {
            statement.setInt(5, parentFolder_number);
        }
        statement.executeUpdate();
        statement.close();
    }

    public void deleteFolderRecursive(String username, int folderID) throws SQLException {
        String query = "DELETE FROM Folders WHERE OwnerUsername = ? AND FolderNumber = ?";

        PreparedStatement statement = getDbConnection().prepareStatement(query);
        statement.setString(1, username);
        statement.setInt(2, folderID);
        statement.executeUpdate();
        statement.close();
    }

}