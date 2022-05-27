package com.asassi.tiwproject.controllers;

import com.asassi.tiwproject.beans.responses.SignupResponseBean;
import com.asassi.tiwproject.beans.UserBean;
import com.asassi.tiwproject.constants.PageConstants;
import com.asassi.tiwproject.constants.SessionConstants;
import com.asassi.tiwproject.crypto.Hasher;
import com.asassi.tiwproject.dao.UserDAO;
import com.asassi.tiwproject.exceptions.UserAlreadyRegisteredException;
import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.context.*;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.*;

@WebServlet("/signup")
@MultipartConfig
public class SignUpController extends JSONResponderServlet {

    @Override
    protected Object handleGet(HttpServletRequest req, HttpServletResponse resp, WebContext ctx, ServletContext servletContext) throws IOException, ServletException {
        //If the User is already logged in, send the user to the Home Page
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute(SessionConstants.Username.getRawValue());
        if (username != null) {
            resp.sendRedirect(PageConstants.Home.getRawValue());
        } else {
            //Serve the plain HTML - JS will handle the rest
            req.getRequestDispatcher("/signupPage.html").forward(req, resp);
        }
        return null;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = StringEscapeUtils.escapeJava(req.getParameter("username"));
        String email = StringEscapeUtils.escapeJava(req.getParameter("email"));
        String password = StringEscapeUtils.escapeJava(req.getParameter("password"));
        String passwordRepeat = StringEscapeUtils.escapeJava(req.getParameter("passwordRepeat"));

        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._]{1,16}+@[a-z]{1,7}\\.[a-z]{1,3}$");

        SignupResponseBean responseBean = new SignupResponseBean();

        String error = null;
        if (username == null) {
            error = "You have to enter a Username to create an account";
            responseBean.setUsernameError(error);
        }
        if (email == null) {
            error = "You have to enter a valid Email address to create an account";
            responseBean.setEmailError(error);
        }
        if (password == null) {
            error = "You have to enter a Password to create an account";
            responseBean.setPasswordError(error);
        }
        if (passwordRepeat == null) {
            error = "You have to repeat your Password to create an account";
            responseBean.setRepeatPasswordError(error);
        }

        if (username != null && username.contains(" ")) {
            error = "Usernames must not have Spaces";
            responseBean.setUsernameError(error);
        }
        if (email != null) {
            Matcher matcher = emailPattern.matcher(email);
            if (!matcher.find()) {
                error = "Invalid Email address";
                responseBean.setEmailError(error);
            }
        }
        if (password != null && password.length() < 8) {
            error = "Password must be at least 8 chars long";
            responseBean.setPasswordError(error);
        }
        if (passwordRepeat != null && !passwordRepeat.equals(password)) {
            error = "Password and Repeat Password fields do not match";
            responseBean.setRepeatPasswordError(error);
        }

        boolean registrationSucceeded = true;
        String registeredUsername = null;
        if (error == null) {
            try {
                registeredUsername = registerUser(username, password, email);
            } catch (UserAlreadyRegisteredException e) {
                error = "Username already chosen by another user";
                responseBean.setUsernameError(error);
                registrationSucceeded = false;
            } catch (SQLException e) {
                //Send internal server error
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("Internal server error occurred. Please try again later.");
                return;
            }
        } else {
            registrationSucceeded = false;
        }

        if (registrationSucceeded) {
            HttpSession session = req.getSession(true);
            session.setAttribute(registeredUsername, SessionConstants.Username.getRawValue());
            //Forward to the Home Page
            resp.sendRedirect(PageConstants.Home.getRawValue());
        } else {
            //Respond with the appropriate status code and a JSON description of all errors
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendAsJSON(responseBean, resp);
        }
    }

    private String registerUser(String username, String password, String email) throws UserAlreadyRegisteredException, SQLException {
        //Hash the Password to store it in the database
        String passwordHash = Hasher.getHash(password);
        //Check if the User is already in the database
        UserDAO userDAO = new UserDAO(getDBConnection());
        List<UserBean> registeredUsersWithSameName = userDAO.findUsersByName(username);
        if (!registeredUsersWithSameName.isEmpty()) throw new UserAlreadyRegisteredException();
        //Add the User to the database
        userDAO.registerUser(new UserBean(username, passwordHash, email));
        return username;
    }
}