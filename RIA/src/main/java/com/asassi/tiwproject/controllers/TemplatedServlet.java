package com.asassi.tiwproject.controllers;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class TemplatedServlet extends DBConnectedServlet {

    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        super.init();
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
        handleGet(req, resp, ctx, servletContext);
        showTemplatePage(ctx, resp);
    }

    protected abstract void handleGet(HttpServletRequest req, HttpServletResponse resp, WebContext ctx, ServletContext servletContext) throws ServletException, IOException;

    protected abstract String getTemplatePage();

    protected TemplateEngine getTemplateEngine() {
        return templateEngine;
    }

    protected void showTemplatePage(WebContext ctx, HttpServletResponse resp) throws IOException {
        templateEngine.process(getTemplatePage(), ctx, resp.getWriter());
    }
}
