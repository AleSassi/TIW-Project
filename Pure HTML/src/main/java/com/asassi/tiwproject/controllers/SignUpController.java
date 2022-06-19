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
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        //The filter already redirects to the login page
        String usernameErrorID = req.getParameter("usrError");
        String emailErrorID = req.getParameter("emailError");
        String passwordErrorID = req.getParameter("pwdError");
        String passwordRepeatErrorID = req.getParameter("pwdRepError");

        if (Objects.equals(usernameErrorID, "1")) {
            ctx.setVariable(SignupConstants.UsernameErrorInfo.getRawValue(), "You have to enter a Username to create an account");
        } else if (Objects.equals(usernameErrorID, "2")) {
            ctx.setVariable(SignupConstants.UsernameErrorInfo.getRawValue(), "Usernames must not have Spaces");
        } else if (Objects.equals(usernameErrorID, "3")) {
            ctx.setVariable(SignupConstants.UsernameErrorInfo.getRawValue(), "Username already chosen by another user");
        }

        if (Objects.equals(emailErrorID, "1")) {
            ctx.setVariable(SignupConstants.EmailErrorInfo.getRawValue(), "You have to enter a valid Email address to create an account");
        } else if (Objects.equals(emailErrorID, "2")) {
            ctx.setVariable(SignupConstants.EmailErrorInfo.getRawValue(), "Invalid Email address");
        }

        if (Objects.equals(passwordErrorID, "1")) {
            ctx.setVariable(SignupConstants.PasswordErrorInfo.getRawValue(), "You have to enter a Password to create an account");
        } else if (Objects.equals(passwordErrorID, "2")) {
            ctx.setVariable(SignupConstants.PasswordErrorInfo.getRawValue(), "Password must be at least 8 chars long");
        }

        if (Objects.equals(passwordRepeatErrorID, "1")) {
            ctx.setVariable(SignupConstants.RepeatPasswordErrorInfo.getRawValue(), "You have to repeat your Password to create an account");
        } else if (Objects.equals(passwordRepeatErrorID, "2")) {
            ctx.setVariable(SignupConstants.RepeatPasswordErrorInfo.getRawValue(), "Password and Repeat Password fields do not match");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String passwordRepeat = req.getParameter("passwordRepeat");

        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._]{1,16}+@[a-z]{1,7}\\.[a-z]{1,3}$");
        Pattern notOnlyWhitespaces = Pattern.compile("[^ ]");

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());

        String usernameError = null, emailError = null, passwordError = null, repeatPasswordError = null;
        if (username == null || !notOnlyWhitespaces.matcher(username).find()) {
            usernameError = "1";
        }
        if (email == null || !notOnlyWhitespaces.matcher(email).find()) {
            emailError = "1";
        }
        if (password == null || !notOnlyWhitespaces.matcher(password).find()) {
            passwordError = "1";
        }
        if (passwordRepeat == null || !notOnlyWhitespaces.matcher(passwordRepeat).find()) {
            repeatPasswordError = "1";
        }

        if (username != null && username.contains(" ")) {
            usernameError = "2";
        }
        if (email != null) {
            Matcher matcher = emailPattern.matcher(email);
            if (!matcher.find()) {
                emailError = "2";
            }
        }
        if (password != null && password.length() < 8) {
            passwordError = "2";
        }
        if (passwordRepeat != null && !passwordRepeat.equals(password)) {
            repeatPasswordError = "2";
        }

        boolean registrationSucceeded = true;
        String registeredUsername = null;
        if (usernameError != null && emailError != null && passwordError != null && repeatPasswordError != null) {
            try {
                registeredUsername = registerUser(username, password, email);
            } catch (UserAlreadyRegisteredException e) {
                usernameError = "3";
                registrationSucceeded = false;
            } catch (SQLException e) {
                throw new UnavailableException("Couldn't perform command");
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
            // We show the Signup page with the errors
            StringBuilder queryString = new StringBuilder(PageConstants.SignUp.getRawValue());
            boolean addedFirstParam = false;
            if (usernameError != null) {
                queryString.append("?");
                queryString.append("usrError=").append(usernameError);
                addedFirstParam = true;
            }
            if (emailError != null) {
                queryString.append(addedFirstParam ? "&" : "?");
                queryString.append("emailError=").append(emailError);
                addedFirstParam = true;
            }
            if (passwordError != null) {
                queryString.append(addedFirstParam ? "&" : "?");
                queryString.append("pwdError=").append(passwordError);
                addedFirstParam = true;
            }
            if (repeatPasswordError != null) {
                queryString.append(addedFirstParam ? "&" : "?");
                queryString.append("pwdRepError=").append(repeatPasswordError);
                addedFirstParam = true;
            }
            resp.sendRedirect(queryString.toString());
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