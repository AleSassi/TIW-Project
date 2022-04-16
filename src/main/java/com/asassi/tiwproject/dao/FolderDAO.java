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
        String query = "SELECT * FROM Folders WHERE OwnerUsername = ? AND FolderType = ? ORDER BY CreationDate";
        PreparedStatement statement = getDbConnection().prepareStatement(query);
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

    public List<FolderBean> findFoldersByUsernameAndFolderNumber(String username, int folderNumber, FolderType folderType) throws SQLException {
        List<FolderBean> folders = new ArrayList<>();
        String query = "SELECT * FROM Folders WHERE OwnerUsername = ? AND FolderNumber = ? AND FolderType = ? ORDER BY CreationDate";
        PreparedStatement statement = getDbConnection().prepareStatement(query);
        statement.setString(1, username);
        statement.setInt(2, folderNumber);
        statement.setInt(3, folderType.getRawValue());
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            folders.add(new FolderBean(result.getString("OwnerUsername"), result.getInt("FolderNumber"), result.getString("Name"), result.getDate("CreationDate"), result.getInt("FolderType"), result.getString("ParentFolder_OwnerUsername"), result.getInt("ParentFolder_FolderNumber")));
        }
        result.close();
        statement.close();

        return folders;
    }

    public List<FolderBean> findSubfoldersOfFolder(String username, int parentFolderNumber) throws SQLException {
        List<FolderBean> folders = new ArrayList<>();
        String query = "SELECT * FROM Folders WHERE ParentFolder_OwnerUsername = ? AND ParentFolder_FolderNumber = ? ORDER BY CreationDate";
        PreparedStatement statement = getDbConnection().prepareStatement(query);
        statement.setString(1, username);
        statement.setInt(2, parentFolderNumber);
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

        PreparedStatement statement = getDbConnection().prepareStatement(query);
        statement.setString(1, folder.getUsername());
        statement.setInt(2, folder.getFolderNumber());
        statement.setString(3, folder.getName());
        statement.setTimestamp(4, new Timestamp(folder.getCreationDate().getTime()));
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