package com.asassi.tiwproject.dao;

import com.asassi.tiwproject.beans.DocumentBean;
import com.asassi.tiwproject.beans.FolderBean;
import com.asassi.tiwproject.constants.FolderType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentDAO extends DAO {

    public DocumentDAO(Connection connection) {
        super(connection);
    }

    public List<DocumentBean> findDocument(String username, int documentNumber) throws SQLException {
        List<DocumentBean> documents = new ArrayList<>();
        String query = "SELECT * FROM Documents WHERE OwnerUsername = ? AND DocumentNumber = ? ORDER BY CreationDate";
        PreparedStatement statement = getDbConnection().prepareStatement(query);
        statement.setString(1, username);
        statement.setInt(2, documentNumber);
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            documents.add(new DocumentBean(result.getString("OwnerUsername"), result.getInt("ParentFolderNumber"), result.getInt("DocumentNumber"), result.getString("Name"), result.getString("FileType"), result.getTimestamp("CreationDate").toLocalDateTime(), result.getString("Contents")));
        }
        result.close();
        statement.close();

        return documents;
    }

    public List<DocumentBean> findDocumentsByUserAndFolder(String username, int folderNumber) throws SQLException {
        List<DocumentBean> documents = new ArrayList<>();
        String query = "SELECT * FROM Documents WHERE OwnerUsername = ? AND ParentFolderNumber = ? ORDER BY CreationDate";
        PreparedStatement statement = getDbConnection().prepareStatement(query);
        statement.setString(1, username);
        statement.setInt(2, folderNumber);
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            documents.add(new DocumentBean(result.getString("OwnerUsername"), result.getInt("ParentFolderNumber"), result.getInt("DocumentNumber"), result.getString("Name"), result.getString("FileType"), result.getTimestamp("CreationDate").toLocalDateTime(), result.getString("Contents")));
        }
        result.close();
        statement.close();

        return documents;
    }

    public void addDocument(DocumentBean document) throws SQLException {
        String query = "INSERT INTO Documents VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = getDbConnection().prepareStatement(query);
        statement.setString(1, document.getOwnerUsername());
        statement.setInt(2, document.getParentFolderNumber());
        statement.setInt(3, document.getDocumentNumber());
        statement.setString(4, document.getName());
        statement.setString(5, document.getFileType());
        statement.setTimestamp(6, Timestamp.valueOf(document.getCreationDate()));
        statement.setString(7, document.getContents());
        statement.executeUpdate();
        statement.close();
    }
}
