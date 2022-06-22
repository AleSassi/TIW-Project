package com.asassi.tiwproject.controllers;

import com.asassi.tiwproject.beans.DocumentBean;
import com.asassi.tiwproject.beans.FolderBean;
import com.asassi.tiwproject.constants.*;
import com.asassi.tiwproject.dao.DocumentDAO;
import com.asassi.tiwproject.dao.FolderDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/MoveDocument")
public class MoveDocumentController extends DBConnectedServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute(SessionConstants.Username.getRawValue());
        String documentIDToMove = req.getParameter("docid");
        String srcFolderID = req.getParameter("fid");
        if (documentIDToMove != null && srcFolderID != null) {
            //Validate the parameters
            try {
                FolderDAO folderDAO = new FolderDAO(getDBConnection());
                int documentID = Integer.parseInt(documentIDToMove);
                int folderID = Integer.parseInt(srcFolderID);
                DocumentDAO documentDAO = new DocumentDAO(getDBConnection());
                FolderBean folder = folderDAO.findFolderByUsernameAndFolderNumber(username, folderID, FolderType.Subfolder);
                DocumentBean document = documentDAO.findDocument(username, documentID);
                if (document != null && folder != null) {
                    documentDAO.moveDocument(document, folderID);
                    //Redirect to avoid multiple move operations
                    resp.setStatus(HttpServletResponse.SC_OK);
                } else if (document == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println("Could not perform the operation since you don't own the document to be moved");
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println("Could not perform the operation since you don't own the target folder");
                }
            } catch (SQLException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().println("There was an error while getting the data to display");
            } catch (NumberFormatException ignored) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("The parameters were not in the correct format");
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("You did not specify the required parameters");
        }
    }
}
