package com.asassi.tiwproject.dao;

import com.asassi.tiwproject.beans.FolderBean;
import com.asassi.tiwproject.beans.responses.NestedFolderBean;
import com.asassi.tiwproject.constants.FolderType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FolderDAO extends DAO {

    public FolderDAO(Connection connection) {
        super(connection);
    }

    public FolderBean findFolderByUsernameAndFolderNumber(String username, int folderNumber, FolderType folderType) throws SQLException {
        FolderBean folder = null;
        String query = "SELECT * FROM Folders WHERE OwnerUsername = ? AND FolderNumber = ? AND ParentFolder_FolderNumber is null ORDER BY CreationDate";
        if (folderType == FolderType.Subfolder) {
            query = "SELECT * FROM Folders WHERE OwnerUsername = ? AND FolderNumber = ? AND ParentFolder_FolderNumber is not null ORDER BY CreationDate";
        } else if (folderType == null) {
            query = "SELECT * FROM Folders WHERE OwnerUsername = ? AND FolderNumber = ? ORDER BY CreationDate";
        }
        PreparedStatement statement = getDbConnection().prepareStatement(query);
        statement.setString(1, username);
        statement.setInt(2, folderNumber);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            folder = new FolderBean(result.getString("OwnerUsername"), result.getInt("FolderNumber"), result.getString("Name"), result.getTimestamp("CreationDate").toLocalDateTime(), result.getInt("ParentFolder_FolderNumber"));
        }
        result.close();
        statement.close();

        return folder;
    }

    public List<NestedFolderBean> findFolderHierarchy(String username) throws SQLException {
        List<NestedFolderBean> nestedFolderBeans = new ArrayList<>();
        String query = "select * from Folders as F1, Folders as F2 where F1.OwnerUsername = ? and F1.ParentFolder_FolderNumber is null and (F1.OwnerUsername = F2.OwnerUsername and (F2.ParentFolder_FolderNumber = F1.FolderNumber or (F1.ParentFolder_FolderNumber is null and F1.Name = F2.Name))) group by F1.FolderNumber, F2.FolderNumber order by F1.CreationDate, F2.CreationDate";

        PreparedStatement statement = getDbConnection().prepareStatement(query);
        statement.setString(1, username);
        ResultSet result = statement.executeQuery();
        FolderBean currentRoot = null;
        List<FolderBean> subfolders = new ArrayList<>();
        while (result.next()) {
            // Add the root folder, then loop to find the subfolders
            int parentID = result.getInt("F2.ParentFolder_FolderNumber");
            if (result.wasNull()) {
                if (currentRoot != null) {
                    nestedFolderBeans.add(new NestedFolderBean(currentRoot, subfolders));
                }
                currentRoot = new FolderBean(result.getString("F1.OwnerUsername"), result.getInt("F1.FolderNumber"), result.getString("F1.Name"), result.getTimestamp("F1.CreationDate").toLocalDateTime(), result.getInt("F1.ParentFolder_FolderNumber"));
                subfolders = new ArrayList<>();
            } else {
                subfolders.add(new FolderBean(result.getString("F2.OwnerUsername"), result.getInt("F2.FolderNumber"), result.getString("F2.Name"), result.getTimestamp("F2.CreationDate").toLocalDateTime(), result.getInt("F2.ParentFolder_FolderNumber")));
            }
        }
        if (currentRoot != null) {
            nestedFolderBeans.add(new NestedFolderBean(currentRoot, subfolders));
        }
        result.close();
        statement.close();

        return nestedFolderBeans;
    }

    public boolean noFolderWithSameNameAtSameHierarchyLevel(FolderBean folderBean) throws SQLException {
        PreparedStatement statement;
        if (folderBean.getParentFolder_folderNumber() == null) {
            String query = "SELECT * FROM Folders WHERE ParentFolder_FolderNumber is null AND OwnerUsername = ? AND Name = ? ORDER BY CreationDate";
            statement = getDbConnection().prepareStatement(query);
            statement.setString(1, folderBean.getUsername());
            statement.setString(2, folderBean.getName());
        } else {
            String query = "SELECT * FROM Folders WHERE ParentFolder_FolderNumber = ? AND OwnerUsername = ? AND Name = ? ORDER BY CreationDate";
            statement = getDbConnection().prepareStatement(query);
            statement.setInt(1, folderBean.getParentFolder_folderNumber());
            statement.setString(2, folderBean.getUsername());
            statement.setString(3, folderBean.getName());
        }
        ResultSet result = statement.executeQuery();
        boolean res = result.next();
        result.close();
        statement.close();

        return !res;
    }

    public int addFolder(FolderBean folder) throws SQLException {
        String query = "INSERT INTO Folders VALUES (default, ?, ?, ?, ?)";

        PreparedStatement statement = getDbConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, folder.getUsername());
        statement.setString(2, folder.getName());
        statement.setTimestamp(3, Timestamp.valueOf(folder.getCreationDate()));
        Integer parentFolder_number = folder.getParentFolder_folderNumber();
        if (parentFolder_number == null) {
            statement.setNull(4, Types.INTEGER);
        } else {
            statement.setInt(4, parentFolder_number);
        }
        statement.executeUpdate();
        int generatedKey = -1;
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            generatedKey = generatedKeys.getInt(1);
        }
        statement.close();
        return generatedKey;
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