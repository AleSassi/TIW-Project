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
        if (username == null) {
            resp.sendRedirect(PageConstants.Default.getRawValue());
        } else {
            //When the Get is performed, we check if we have a valid document ID, otherwise we redirect to the home page
            String folderID = req.getParameter("fid");
            boolean hasErrorFindingFolder = true;
            FolderDAO folderDAO = new FolderDAO(getDBConnection());
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
                    hasErrorFindingFolder = false;
                }
            } catch (SQLException e) {
                throw new ServletException(e.getMessage());
            } catch (NumberFormatException ignored) {
            }

            if (hasErrorFindingFolder) {
                //Redirect to Home Page
                resp.sendRedirect(PageConstants.Home.getRawValue());
            }
        }
    }

    @Override
    protected String getTemplatePage() {
        return "/docListPage.html";
    }
}
