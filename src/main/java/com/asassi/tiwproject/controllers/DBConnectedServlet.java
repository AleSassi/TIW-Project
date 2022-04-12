package com.asassi.tiwproject.controllers;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DBConnectedServlet extends TemplatedServlet {

    private Connection dbConnection;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext servletContext = getServletContext();
        try {
            String driver = servletContext.getInitParameter("dbDriver");
            String url = servletContext.getInitParameter("dbUrl");
            String user = servletContext.getInitParameter("dbUser");
            String password = servletContext.getInitParameter("dbPassword");
            Class.forName(driver);
            dbConnection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new UnavailableException("Can't load database driver");
        } catch (SQLException e) {
            throw new UnavailableException("Couldn't get db connection");
        }
    }

    protected Connection getDBConnection() {
        return dbConnection;
    }

    @Override
    public void destroy() {
        try {
            dbConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.destroy();
    }
}
