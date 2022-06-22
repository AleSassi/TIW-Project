package com.asassi.tiwproject.controllers;

import com.asassi.tiwproject.beans.DocumentBean;
import com.asassi.tiwproject.beans.FolderBean;
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

@WebServlet("/Delete")
public class DeleteController extends JSONResponderServlet {

    @Override
    protected Object handleGet(HttpServletRequest req, HttpServletResponse resp, WebContext ctx, ServletContext servletContext) throws ServletException, IOException {
        return null;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String folderIDStr = req.getParameter("fid");
        String documentIDStr = req.getParameter("docid");
        String username = (String) session.getAttribute(SessionConstants.Username.getRawValue());
        if (folderIDStr == null && documentIDStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("You must send the folder or document you want to delete");
        } else {
            try {
                if (folderIDStr != null) {
                    //Delete a folder
                    int folderID = Integer.parseInt(folderIDStr);
                    FolderDAO folderDAO = new FolderDAO(getDBConnection());
                    FolderBean folder = folderDAO.findFolderByUsernameAndFolderNumber(username, folderID, null);
                    if (folder == null) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().println("Could not find the folder to delete. It may be because it does not exist, or you don't have the permission to access it");
                    } else {
                        folderDAO.deleteFolderRecursive(username, folderID);
                        resp.setStatus(HttpServletResponse.SC_OK);
                    }
                } else {
                    //Delete a document
                    int documentID = Integer.parseInt(documentIDStr);
                    DocumentDAO documentDAO = new DocumentDAO(getDBConnection());
                    DocumentBean document = documentDAO.findDocument(username, documentID);
                    if (document == null) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().println("Could not find the document to delete. It may be because it does not exist, or you don't have the permission to access it");
                    } else {
                        documentDAO.deleteDocument(username, documentID);
                        resp.setStatus(HttpServletResponse.SC_OK);
                    }
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("Invalid IDs");
            } catch (SQLException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().println("Could not perform the action. Please try again later");
            }
        }
    }
}
