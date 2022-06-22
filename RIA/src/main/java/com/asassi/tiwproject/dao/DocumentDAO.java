package com.asassi.tiwproject.dao;

import com.asassi.tiwproject.beans.DocumentBean;
import com.asassi.tiwproject.beans.FolderBean;
import com.asassi.tiwproject.constants.FolderType;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentDAO extends DAO {

    public DocumentDAO(Connection connection) {
        super(connection);
    }

    public DocumentBean findDocument(String username, int documentNumber) throws SQLException {
        String query = "SELECT * FROM Documents WHERE OwnerUsername = ? AND DocumentNumber = ? ORDER BY CreationDate";
        DocumentBean document = null;
        PreparedStatement statement = getDbConnection().prepareStatement(query);
        statement.setString(1, username);
        statement.setInt(2, documentNumber);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            document = new DocumentBean(result.getString("OwnerUsername"), result.getInt("ParentFolderNumber"), result.getInt("DocumentNumber"), result.getString("Name"), result.getString("FileType"), result.getTimestamp("CreationDate").toLocalDateTime(), result.getString("Contents"));
        }
        result.close();
        statement.close();

        return document;
    }

    public List<DocumentBean> findDocumentsByUserAndFolder(String username, int folderNumber) throws SQLException {
        String query = "SELECT * FROM Documents WHERE OwnerUsername = ? AND ParentFolderNumber = ? ORDER BY CreationDate";
        List<DocumentBean> documents = new ArrayList<>();

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

    public boolean noDocumentWithSameNameAtSameHierarchyLevel(DocumentBean documentBean) throws SQLException {
        List<DocumentBean> folders = new ArrayList<>();
        String query = "SELECT * FROM Documents WHERE ParentFolderNumber = ? AND OwnerUsername = ? AND Name = ? ORDER BY CreationDate";
        PreparedStatement statement = getDbConnection().prepareStatement(query);
        statement.setInt(1, documentBean.getParentFolderNumber());
        statement.setString(2, documentBean.getOwnerUsername());
        statement.setString(3, documentBean.getName());
        ResultSet result = statement.executeQuery();
        boolean res = result.next();
        result.close();
        statement.close();

        return !res;
    }

    public int addDocument(DocumentBean document) throws SQLException {
        String query = "INSERT INTO Documents VALUES (default, ?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = getDbConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, document.getParentFolderNumber());
        statement.setString(2, document.getOwnerUsername());
        statement.setString(3, document.getName());
        statement.setString(4, document.getFileType());
        statement.setTimestamp(5, Timestamp.valueOf(document.getCreationDate()));
        statement.setString(6, document.getContents());
        statement.executeUpdate();
        int generatedKey = -1;
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            generatedKey = generatedKeys.getInt(1);
        }
        statement.close();
        return generatedKey;
    }

    public void moveDocument(DocumentBean document, int targetFolderNumber) throws SQLException {
        String query = "UPDATE Documents SET ParentFolderNumber = ? WHERE DocumentNumber = ?";

        PreparedStatement statement = getDbConnection().prepareStatement(query);
        statement.setInt(1, targetFolderNumber);
        statement.setInt(2, document.getDocumentNumber());
        statement.executeUpdate();
        statement.close();
    }

    public void deleteDocument(String username, int documentID) throws SQLException {
        String deleteQuery = "DELETE FROM Documents WHERE DocumentNumber = ? AND OwnerUsername = ?";
        PreparedStatement deleteStatement = getDbConnection().prepareStatement(deleteQuery);

        deleteStatement.setInt(1, documentID);
        deleteStatement.setString(2, username);
        deleteStatement.executeUpdate();
        deleteStatement.close();
    }
}
