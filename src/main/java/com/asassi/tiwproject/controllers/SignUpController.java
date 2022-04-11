package com.asassi.tiwproject.controllers;

import com.asassi.tiwproject.constants.SignupConstants;
import org.thymeleaf.*;
import org.thymeleaf.context.*;
import org.thymeleaf.templatemode.*;
import org.thymeleaf.templateresolver.*;

import java.io.*;
import java.util.Objects;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.*;

@WebServlet("/signup")
public class SignUpController extends HttpServlet {

    private TemplateEngine templateEngine;
    private final String signupPage = "/signup.html";

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();

        ServletContextTemplateResolver templateResolver = new
                ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
        templateEngine.process(signupPage, ctx, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String passwordRepeat = req.getParameter("passwordRepeat");

        System.out.println(username + " " + password + " " + passwordRepeat);

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

        if (error == null) {
            //TODO: Register the user
            //TODO: Forward to the Home Page
            System.out.println("No errors! user " + username + "successfully registered with password " + password);
        } else {
            if (isUsernameValid) {
                ctx.setVariable(SignupConstants.ValidatedUsername.getRawValue(), username);
            }
            // We show the Signup page with the errors
            templateEngine.process(signupPage, ctx, resp.getWriter());
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}