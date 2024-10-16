package com.asassi.tiwproject.controllers;

import com.google.gson.*;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

public abstract class JSONResponderServlet extends DBConnectedServlet {

    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        gson = new GsonBuilder().setDateFormat("dd MMM yyyy").registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
        Object responseObj = handleGet(req, resp, ctx, servletContext);
        if (responseObj != null) {
            //Send the response to the client
            sendAsJSON(responseObj, resp);
        }
    }

    protected void sendAsJSON(Object object, HttpServletResponse resp) throws IOException {
        String json = gson.toJson(object);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }

    protected abstract Object handleGet(HttpServletRequest req, HttpServletResponse resp, WebContext ctx, ServletContext servletContext) throws ServletException, IOException;
}