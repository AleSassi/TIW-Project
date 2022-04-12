package com.asassi.tiwproject.controllers;

import com.asassi.tiwproject.constants.HomeConstants;
import com.asassi.tiwproject.constants.PageConstants;
import com.asassi.tiwproject.constants.SessionConstants;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@WebServlet("/home")
public class HomeController extends DBConnectedServlet {

    @Override
    protected String getTemplatePage() {
        return "/homePage.html";
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void handleGet(HttpServletRequest req, HttpServletResponse resp, WebContext ctx, ServletContext servletContext) throws ServletException, IOException {
        //If the User is not registered (we cannot find the username in the Session), redirect to the Login page
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute(SessionConstants.UserHash.getRawValue());
        if (username == null) {
            resp.sendRedirect(PageConstants.Default.getRawValue());
        } else {
            ctx.setVariable(HomeConstants.Username.getRawValue(), username);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
