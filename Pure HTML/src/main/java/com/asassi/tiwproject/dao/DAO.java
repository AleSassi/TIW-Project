package com.asassi.tiwproject.dao;

import java.sql.Connection;

public abstract class DAO {

    private final Connection dbConnection;

    public DAO(Connection connection) {
        this.dbConnection = connection;
    }

    protected Connection getDbConnection() {
        return dbConnection;
    }

}
