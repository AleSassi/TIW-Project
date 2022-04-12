package com.asassi.tiwproject.controllers;

import com.asassi.tiwproject.beans.UserBean;
import com.asassi.tiwproject.constants.PageConstants;
import com.asassi.tiwproject.constants.SessionConstants;
import com.asassi.tiwproject.constants.SignupConstants;
import com.asassi.tiwproject.crypto.Hasher;
import com.asassi.tiwproject.dao.UserDAO;
import com.asassi.tiwproject.exceptions.UserAlreadyRegisteredException;
import org.thymeleaf.context.*;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.*;

@WebServlet("/signup")
public class SignUpController extends DBConnectedServlet {

    @Override
    protected String getTemplatePage() {
        return "/signupPage.html";
    }

    @Override
    protected void handleGet(HttpServletRequest req, HttpServletResponse resp, WebContext ctx, ServletContext servletContext) throws IOException {
        //If the User is already logged in, send the user to the Home Page
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute(SessionConstants.UserHash.getRawValue());
        if (username != null) {
            resp.sendRedirect(PageConstants.Home.getRawValue());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String passwordRepeat = req.getParameter("passwordRepeat");

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());

        String error = null;
        boolean isUsernameValid = false;
        if (username == null) {
            error = "You have to enter a Username to create an account";
            ctx.setVariable(SignupConstants.UsernameErrorInfo.getRawValue(), error);
        }
        if (password == null) {
            error = "You have to enter a Password to create an account";
            ctx.setVariable(SignupConstants.PasswordErrorInfo.getRawValue(), error);
        }
        if (passwordRepeat == null) {
            error = "You have to repeat your Password to create an account";
            ctx.setVariable(SignupConstants.RepeatPasswordErrorInfo.getRawValue(), error);
        }

        if (username != null && username.contains(" ")) {
            error = "Usernames must not have Spaces";
            ctx.setVariable(SignupConstants.UsernameErrorInfo.getRawValue(), error);
        } else if (username != null) {
            isUsernameValid = true;
        }
        if (password != null && password.length() < 8) {
            error = "Password must be at least 8 chars long";
            ctx.setVariable(SignupConstants.PasswordErrorInfo.getRawValue(), error);
        }
        if (passwordRepeat != null && !passwordRepeat.equals(password)) {
            error = "Password and Repeat Password fields do not match";
            ctx.setVariable(SignupConstants.RepeatPasswordErrorInfo.getRawValue(), error);
        }

        boolean registrationSucceeded = true;
        String registeredUsername = null;
        if (error == null) {
            try {
                registeredUsername = registerUser(username, password);
            } catch (UserAlreadyRegisteredException e) {
                error = "Username already chosen by another user";
                ctx.setVariable(SignupConstants.UsernameErrorInfo.getRawValue(), error);
                registrationSucceeded = false;
            } catch (SQLException e) {
                throw new UnavailableException("Couldn't perform command");
            }
        } else {
            registrationSucceeded = false;
        }

        if (registrationSucceeded) {
            HttpSession session = req.getSession(true);
            session.setAttribute(registeredUsername, SessionConstants.UserHash.getRawValue());
            //Forward to the Home Page
            resp.sendRedirect(PageConstants.Home.getRawValue());
        } else {
            if (isUsernameValid) {
                ctx.setVariable(SignupConstants.ValidatedUsername.getRawValue(), username);
            }
            // We show the Signup page with the errors
            showTemplatePage(ctx, resp);
        }
    }

    private String registerUser(String username, String password) throws UserAlreadyRegisteredException, SQLException {
        //Hash the Password to store it in the database
        String passwordHash = Hasher.getHash(password);
        //Check if the User is already in the database
        UserDAO userDAO = new UserDAO(getDBConnection());
        List<UserBean> registeredUsersWithSameName = userDAO.findUsersByName(username);
        if (!registeredUsersWithSameName.isEmpty()) throw new UserAlreadyRegisteredException();
        //Add the User to the database
        userDAO.registerUser(new UserBean(username, passwordHash));
        return username;
    }
}