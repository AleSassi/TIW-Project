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

@WebServlet("/show")
public class DocumentInfoController extends DBConnectedServlet {

    @Override
    protected void handleGet(HttpServletRequest req, HttpServletResponse resp, WebContext ctx, ServletContext servletContext) throws ServletException, IOException {
        //If the User is not registered (we cannot find the username in the Session), redirect to the Login page
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute(SessionConstants.Username.getRawValue());
        //When the Get is performed, we check if we have a valid document ID, otherwise we redirect to the home page
        String documentID = req.getParameter("document");
        boolean hasErrorFindingDocument = true;
        DocumentDAO documentDAO = new DocumentDAO(getDBConnection());
        try {
            int documentIDInt = Integer.parseInt(documentID);
            int folderIDInt = Integer.parseInt(req.getParameter("fid"));
            DocumentBean document = documentDAO.findDocument(username, documentIDInt);
            if (document != null) {
                FolderDAO folderDAO = new FolderDAO(getDBConnection());
                FolderBean parentFolder = folderDAO.findFolderByUsernameAndFolderNumber(username, document.getParentFolderNumber(), FolderType.Subfolder);
                if (parentFolder != null && document.getParentFolderNumber() == folderIDInt) {
                    ctx.setVariable(DocumentInfoConstants.Username.getRawValue(), username);
                    ctx.setVariable(DocumentInfoConstants.DocumentName.getRawValue(), document.getName());
                    ctx.setVariable(DocumentInfoConstants.DocumentExtension.getRawValue(), document.getFileType());
                    ctx.setVariable(DocumentInfoConstants.DocumentContents.getRawValue(), document.getContents());
                    ctx.setVariable(DocumentInfoConstants.DocumentCreationDate.getRawValue(), document.getCreationDateString());
                    ctx.setVariable(DocumentInfoConstants.DocumentOwner.getRawValue(), document.getOwnerUsername());
                    ctx.setVariable(DocumentInfoConstants.ParentFolder.getRawValue(), parentFolder.getName());
                    ctx.setVariable(DocumentInfoConstants.ParentFolderNumber.getRawValue(), folderIDInt);
                    hasErrorFindingDocument = false;
                }
            }
        } catch (SQLException e) {
            throw new ServletException(e.getMessage());
        } catch (NumberFormatException ignored) {
        }
        ctx.setVariable("previousURL", req.getHeader("referer"));
        if (hasErrorFindingDocument) {
            //Redirect to Home Page
            resp.sendRedirect(PageConstants.Home.getRawValue() + "?error=2");
        }
    }

    @Override
    protected String getTemplatePage() {
        return "/documentPage.html";
    }
}
