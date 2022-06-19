package com.asassi.tiwproject.controllers;

import com.asassi.tiwproject.beans.DocumentBean;
import com.asassi.tiwproject.beans.FolderBean;
import com.asassi.tiwproject.constants.FolderType;
import com.asassi.tiwproject.constants.HomeConstants;
import com.asassi.tiwproject.constants.PageConstants;
import com.asassi.tiwproject.constants.SessionConstants;
import com.asassi.tiwproject.dao.DocumentDAO;
import com.asassi.tiwproject.dao.FolderDAO;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/moveDoc")
public class MoveDocumentController extends DBConnectedServlet {
    @Override
    protected void handleGet(HttpServletRequest req, HttpServletResponse resp, WebContext ctx, ServletContext servletContext) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute(SessionConstants.Username.getRawValue());
        String targetFolderID = req.getParameter("fid");
        String documentToMoveID = req.getParameter("documentMV");
        DocumentDAO documentDAO = new DocumentDAO(getDBConnection());
        try {
            int documentID = Integer.parseInt(documentToMoveID);
            int folderIDInt = Integer.parseInt(targetFolderID);
            List<DocumentBean> documents = documentDAO.findDocument(username, documentID);
            if (!documents.isEmpty()) {
                DocumentBean document = documents.get(0);
                documentDAO.moveDocument(document, folderIDInt);
                //Redirect to avoid multiple move operations
                resp.sendRedirect(PageConstants.FolderDetail.getRawValue() + "?fid=" + folderIDInt);
                return;
            }
        } catch (SQLException e) {
            throw new ServletException(e.getMessage());
        } catch (NumberFormatException ignored) {
        }
        resp.sendRedirect(PageConstants.Home.getRawValue() + "?error=3");
    }

    @Override
    protected String getTemplatePage() {
        return "homePage.html";
    }
}
