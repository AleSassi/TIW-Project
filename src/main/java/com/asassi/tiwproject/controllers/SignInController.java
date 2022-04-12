package com.asassi.tiwproject.controllers;

import com.asassi.tiwproject.beans.UserBean;
import com.asassi.tiwproject.constants.HomeConstants;
import com.asassi.tiwproject.constants.PageConstants;
import com.asassi.tiwproject.constants.SessionConstants;
import com.asassi.tiwproject.constants.SignupConstants;
import com.asassi.tiwproject.crypto.Hasher;
import com.asassi.tiwproject.dao.UserDAO;
import com.asassi.tiwproject.exceptions.IncorrectPasswordException;
import com.asassi.tiwproject.exceptions.UserNotRegisteredException;
import org.thymeleaf.context.WebContext;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.*;

@WebServlet("")
public class SignInController extends DBConnectedServlet {

    @Override
    protected String getTemplatePage() {
        return "/loginPage.html";
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

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());

        String error = null;
        boolean isUsernameValid = true;
        if (username == null) {
            error = "You have to enter a Username to log in";
            ctx.setVariable(SignupConstants.UsernameErrorInfo.getRawValue(), error);
            isUsernameValid = false;
        }
        if (password == null) {
            error = "You have to enter a Password to log in";
            ctx.setVariable(SignupConstants.PasswordErrorInfo.getRawValue(), error);
        }

        boolean loginSucceeded = false;
        String registeredUsername = null;
        if (error == null) {
            try {
                registeredUsername = findRegisteredUser(username, password);
                loginSucceeded = true;
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (UserNotRegisteredException e) {
                error = "The Username could not be found";
                ctx.setVariable(SignupConstants.UsernameErrorInfo.getRawValue(), error);
            } catch (IncorrectPasswordException e) {
                error = "The Password is incorrect";
                ctx.setVariable(SignupConstants.PasswordErrorInfo.getRawValue(), error);
            }
        }

        if (loginSucceeded) {
            HttpSession session = req.getSession(true);
            session.setAttribute(SessionConstants.UserHash.getRawValue(), registeredUsername);
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

    private String findRegisteredUser(String username, String password) throws SQLException, UserNotRegisteredException, IncorrectPasswordException {
        //Hash the Password to store it in the database
        String passwordHash = Hasher.getHash(password);
        //Check if the User is already in the database
        UserDAO userDAO = new UserDAO(getDBConnection());
        List<UserBean> registeredUsersWithSameName = userDAO.findUsersByName(username);
        if (registeredUsersWithSameName.isEmpty()) throw new UserNotRegisteredException();
        //Check the user password
        if (!registeredUsersWithSameName.get(0).getPasswordHash().equals(passwordHash)) throw new IncorrectPasswordException();
        return username;
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
