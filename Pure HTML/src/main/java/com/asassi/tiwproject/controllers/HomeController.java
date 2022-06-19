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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@WebServlet("/home")
public class HomeController extends DBConnectedServlet {

    @Override
    protected String getTemplatePage() {
        return "/homePage.html";
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void handleGet(HttpServletRequest req, HttpServletResponse resp, WebContext ctx, ServletContext servletContext) throws ServletException, IOException {
        //If the User is not registered (we cannot find the username in the Session), redirect to the Login page
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute(SessionConstants.Username.getRawValue());
        ctx.setVariable(HomeConstants.Username.getRawValue(), username);
        //Find the Folders of the User
        try {
            FolderDAO folderDAO = new FolderDAO(getDBConnection());
            ctx.setVariable(HomeConstants.Hierarchy.getRawValue(), folderDAO.findFolderHierarchy(username));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Could not perform query");
        }
        //Check the additional parameters for the Move operation
        String documentIDToMove = req.getParameter("document");
        String srcFolderID = req.getParameter("fid");
        String errorCode = req.getParameter("error");
        if (documentIDToMove != null && srcFolderID != null) {
            //Validate the parameters
            try {
                int documentID = Integer.parseInt(documentIDToMove);
                int folderID = Integer.parseInt(srcFolderID);
                FolderDAO folderDAO = new FolderDAO(getDBConnection());
                DocumentDAO documentDAO = new DocumentDAO(getDBConnection());
                List<DocumentBean> ownedDocsWithSameID = documentDAO.findDocument(username, documentID);
                List<FolderBean> parentFolders = folderDAO.findFoldersByUsernameAndFolderNumber(username, folderID, FolderType.Subfolder);
                if (!ownedDocsWithSameID.isEmpty() && !parentFolders.isEmpty()) {
                    //Check that the folder is the same
                    DocumentBean document = ownedDocsWithSameID.get(0);
                    FolderBean parentFolder = parentFolders.get(0);
                    if (document.getParentFolderNumber() == folderID) {
                        ctx.setVariable(HomeConstants.DocToMoveID.getRawValue(), documentID);
                        ctx.setVariable(HomeConstants.DocToMoveName.getRawValue(), document.getName());
                        ctx.setVariable(HomeConstants.DocToMoveSrcFolderID.getRawValue(), folderID);
                        ctx.setVariable(HomeConstants.DocToMoveSrcFolderName.getRawValue(), parentFolder.getName());
                    }
                } else {
                    errorCode = "4";
                }
            } catch (SQLException e) {
                throw new ServletException("Could not perform query");
            } catch (NumberFormatException ignored) {
                errorCode = "4";
            }
        }
        //Add error message for error codes
        if (Objects.equals(errorCode, "1")) {
            ctx.setVariable(HomeConstants.ErrorMessage.getRawValue(), "An error occurred while opening the folder. The folder you attempted to open does not exist, or you don't have permission to view it");
        } else if (Objects.equals(errorCode, "2")) {
            ctx.setVariable(HomeConstants.ErrorMessage.getRawValue(), "An error occurred while opening the document. The document you attempted to open does not exist, or you don't have permission to view it");
        } else if (Objects.equals(errorCode, "3")) {
            ctx.setVariable(HomeConstants.ErrorMessage.getRawValue(), "An error occurred while moving the document. Either the target folder or the document you attempted to move does not exist, or you don't have permission to view it");
        } else if (Objects.equals(errorCode, "4")) {
            ctx.setVariable(HomeConstants.ErrorMessage.getRawValue(), "An error occurred while moving the document. Either the source folder or the document you attempted to move does not exist, or you don't have permission to view it");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
