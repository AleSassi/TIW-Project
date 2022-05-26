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

@WebServlet("/home")
public class HomeController extends TemplatedServlet {

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
        if (username == null) {
            resp.sendRedirect(PageConstants.Default.getRawValue());
        } else {
            ctx.setVariable(HomeConstants.Username.getRawValue(), username);
            //Find the Folders of the User
            try {
                FolderDAO folderDAO = new FolderDAO(getDBConnection());
                List<FolderBean> mainFolders = folderDAO.findFoldersByUsername(username, FolderType.Main);
                List<List<FolderBean>> subfolderHierarchy = new ArrayList<>();
                for (FolderBean mainFolder: mainFolders) {
                    List<FolderBean> subfolders = folderDAO.findSubfoldersOfFolder(username, mainFolder.getFolderNumber());
                    subfolderHierarchy.add(subfolders);
                }
                ctx.setVariable(HomeConstants.MainFolders.getRawValue(), mainFolders);
                ctx.setVariable(HomeConstants.Subfolders.getRawValue(), subfolderHierarchy);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new ServletException("Could not perform query");
            }
            //Check the additional parameters for the Move operation
            String documentIDToMove = req.getParameter("document");
            String srcFolderID = req.getParameter("fid");
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
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new ServletException("Could not perform query");
                } catch (NumberFormatException ignored) {
                }
            }
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
