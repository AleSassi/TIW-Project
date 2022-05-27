package com.asassi.tiwproject.controllers;

import com.asassi.tiwproject.beans.FolderBean;
import com.asassi.tiwproject.beans.responses.NestedFolderBean;
import com.asassi.tiwproject.constants.FolderType;
import com.asassi.tiwproject.constants.SessionConstants;
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
import java.util.List;

@WebServlet("/GetFolderListData")
public class GetFolderListDataController extends JSONResponderServlet {

    @Override
    protected Object handleGet(HttpServletRequest req, HttpServletResponse resp, WebContext ctx, ServletContext servletContext) throws ServletException, IOException {
        // Find the data from the user - the user ID has already been saved in the server session
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute(SessionConstants.Username.getRawValue());
        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().println("Cannot access the service since the user is not logged in");
            return null;
        } else {
            // Fetch the data from the DB
            try {
                FolderDAO folderDAO = new FolderDAO(getDBConnection());
                List<FolderBean> mainFolders = folderDAO.findFoldersByUsername(username, FolderType.Main);
                List<List<FolderBean>> subfolderHierarchy = new ArrayList<>();
                for (FolderBean mainFolder: mainFolders) {
                    List<FolderBean> subfolders = folderDAO.findSubfoldersOfFolder(username, mainFolder.getFolderNumber());
                    subfolderHierarchy.add(subfolders);
                }
                List<NestedFolderBean> result = new ArrayList<>();
                for (int i = 0; i < mainFolders.size(); i++) {
                    result.add(new NestedFolderBean(mainFolders.get(i), subfolderHierarchy.get(i)));
                }
                return result;
            } catch (SQLException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().println("There was an error while getting the data to display");
                return null;
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
