package com.asassi.tiwproject.controllers;

import com.asassi.tiwproject.beans.responses.LoginResponseBean;
import com.asassi.tiwproject.beans.UserBean;
import com.asassi.tiwproject.constants.PageConstants;
import com.asassi.tiwproject.constants.SessionConstants;
import com.asassi.tiwproject.crypto.Hasher;
import com.asassi.tiwproject.dao.UserDAO;
import com.asassi.tiwproject.exceptions.IncorrectPasswordException;
import com.asassi.tiwproject.exceptions.UserNotRegisteredException;
import org.thymeleaf.context.WebContext;
import org.apache.commons.text.StringEscapeUtils;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.*;

@WebServlet("")
@MultipartConfig
public class SignInController extends JSONResponderServlet {

    @Override
    protected Object handleGet(HttpServletRequest req, HttpServletResponse resp, WebContext ctx, ServletContext servletContext) throws IOException, ServletException {
        //Serve the plain HTML - JS will handle the rest
        req.getRequestDispatcher("/loginPage.html").forward(req, resp);
        return null;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = StringEscapeUtils.escapeJava(req.getParameter("username"));
        String password = StringEscapeUtils.escapeJava(req.getParameter("password"));

        LoginResponseBean responseBean = new LoginResponseBean();

        String error = null;
        if (username == null) {
            error = "You have to enter a Username to log in";
            responseBean.setUsernameError(error);
        }
        if (password == null) {
            error = "You have to enter a Password to log in";
            responseBean.setPasswordError(error);
        }

        boolean loginSucceeded = false;
        String registeredUsername = null;
        if (error == null) {
            try {
                registeredUsername = findRegisteredUser(username, password);
                loginSucceeded = true;
            } catch (SQLException e) {
                //Send internal server error
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("Internal server error occurred. Please try again later.");
                return;
            } catch (UserNotRegisteredException | IncorrectPasswordException e) {
                error = "Incorrect username or password";
                responseBean.setUsernameError(error);
            }
        }

        if (loginSucceeded) {
            HttpSession session = req.getSession(true);
            session.setAttribute(SessionConstants.Username.getRawValue(), registeredUsername);
            //Forward to the Home Page
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            // Send the JSON with errors
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendAsJSON(responseBean, resp);
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
