package com.asassi.tiwproject.controllers;

import com.asassi.tiwproject.beans.DocumentBean;
import com.asassi.tiwproject.beans.FolderBean;
import com.asassi.tiwproject.constants.*;
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

@WebServlet("/folder")
public class FolderContentController extends DBConnectedServlet {
    @Override
    protected void handleGet(HttpServletRequest req, HttpServletResponse resp, WebContext ctx, ServletContext servletContext) throws ServletException, IOException {
        //If the User is not registered (we cannot find the username in the Session), redirect to the Login page
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute(SessionConstants.Username.getRawValue());
        //When the Get is performed, we check if we have a valid folder ID, otherwise we redirect to the home page
        String folderID = req.getParameter("fid");
        boolean hasErrorFindingFolder = true;
        FolderDAO folderDAO = new FolderDAO(getDBConnection());
        ctx.setVariable("previousURL", req.getHeader("referer"));
        try {
            int folderIDInt = Integer.parseInt(folderID);
            List<FolderBean> folders = folderDAO.findFoldersByUsernameAndFolderNumber(username, folderIDInt, FolderType.Subfolder);
            if (!folders.isEmpty()) {
                FolderBean folder = folders.get(0);
                DocumentDAO documentDAO = new DocumentDAO(getDBConnection());
                List<DocumentBean> documents = documentDAO.findDocumentsByUserAndFolder(username, folderIDInt);
                ctx.setVariable(SubfolderDetailConstants.Username.getRawValue(), username);
                ctx.setVariable(SubfolderDetailConstants.FolderName.getRawValue(), folder.getName());
                ctx.setVariable(SubfolderDetailConstants.Documents.getRawValue(), documents);
                ctx.setVariable(SubfolderDetailConstants.FolderID.getRawValue(), folderIDInt);
                hasErrorFindingFolder = false;
            }
        } catch (SQLException e) {
            throw new ServletException(e.getMessage());
        } catch (NumberFormatException ignored) {
        }

        if (hasErrorFindingFolder) {
            //Redirect to Home Page
            resp.sendRedirect(PageConstants.Home.getRawValue());
        } else {
            // If the query string contains an additional Document parameter, and this parameter is valid, forward to the move servlet which will then give control back to here
            String documentToMoveID = req.getParameter("documentMV");
            DocumentDAO documentDAO = new DocumentDAO(getDBConnection());
            try {
                int documentID = Integer.parseInt(documentToMoveID);
                int folderIDInt = Integer.parseInt(folderID);
                List<DocumentBean> documents = documentDAO.findDocument(username, documentID);
                if (!documents.isEmpty()) {
                    DocumentBean document = documents.get(0);
                    documentDAO.moveDocument(document, folderIDInt);
                    //Redirect to avoid multiple move operations
                    resp.sendRedirect(PageConstants.FolderDetail.getRawValue() + "?fid=" + folderIDInt);
                }
            } catch (SQLException e) {
                throw new ServletException(e.getMessage());
            } catch (NumberFormatException ignored) {
            }
        }
    }

    @Override
    protected String getTemplatePage() {
        return "/docListPage.html";
    }
}
