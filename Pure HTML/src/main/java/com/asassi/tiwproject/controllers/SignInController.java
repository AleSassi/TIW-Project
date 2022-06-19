package com.asassi.tiwproject.controllers;

import com.asassi.tiwproject.beans.UserBean;
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
import java.util.Objects;
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
        //The filter already redirects to the login page
        String usernameErrorID = req.getParameter("usrError");
        String passwordErrorID = req.getParameter("pwdError");

        if (Objects.equals(usernameErrorID, "1")) {
            ctx.setVariable(SignupConstants.UsernameErrorInfo.getRawValue(), "You have to enter a Username to log in");
        } else if (Objects.equals(usernameErrorID, "2")) {
            ctx.setVariable(SignupConstants.UsernameErrorInfo.getRawValue(), "The Username could not be found");
        }

        if (Objects.equals(passwordErrorID, "1")) {
            ctx.setVariable(SignupConstants.PasswordErrorInfo.getRawValue(), "You have to enter a Password to log in");
        } else if (Objects.equals(passwordErrorID, "2")) {
            ctx.setVariable(SignupConstants.PasswordErrorInfo.getRawValue(), "The Password is incorrect");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        String userError = null, passwordError = null;
        if (username == null) {
            userError = "1";
        }
        if (password == null) {
            passwordError = "1";
        }

        boolean loginSucceeded = false;
        String registeredUsername = null;
        if (userError == null || passwordError == null) {
            try {
                registeredUsername = findRegisteredUser(username, password);
                loginSucceeded = true;
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (UserNotRegisteredException e) {
                userError = "2";
            } catch (IncorrectPasswordException e) {
                passwordError = "2";
            }
        }

        if (loginSucceeded) {
            HttpSession session = req.getSession(true);
            session.setAttribute(SessionConstants.Username.getRawValue(), registeredUsername);
            //Forward to the Home Page
            resp.sendRedirect(PageConstants.Home.getRawValue());
        } else {
            // We show the Signup page with the errors
            StringBuilder queryString = new StringBuilder(PageConstants.Default.getRawValue());
            if (userError != null || passwordError != null) {
                queryString.append("?");
                if (userError != null) {
                    queryString.append("usrError=").append(userError);
                    if (passwordError != null) {
                        queryString.append("&");
                    }
                } else {
                    queryString.append("pwdError=").append(passwordError);
                }
            }
            resp.sendRedirect(queryString.toString());
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
