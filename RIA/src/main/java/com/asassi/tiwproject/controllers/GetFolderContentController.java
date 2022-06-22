package com.asassi.tiwproject.controllers;

import com.asassi.tiwproject.beans.DocumentBean;
import com.asassi.tiwproject.beans.FolderBean;
import com.asassi.tiwproject.beans.responses.FolderContentResponseBean;
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

@WebServlet("/getFolderDetailData")
public class GetFolderContentController extends JSONResponderServlet {

    @Override
    protected Object handleGet(HttpServletRequest req, HttpServletResponse resp, WebContext ctx, ServletContext servletContext) throws ServletException, IOException {
        //If the User is not registered (we cannot find the username in the Session), redirect to the Login page
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute(SessionConstants.Username.getRawValue());
        //When the Get is performed, we check if we have a valid folder ID, otherwise we redirect to the home page
        String folderID = req.getParameter("fid");
        boolean hasErrorFindingFolder = true;
        FolderDAO folderDAO = new FolderDAO(getDBConnection());

        FolderContentResponseBean responseBean = new FolderContentResponseBean();
        try {
            int folderIDInt = Integer.parseInt(folderID);
            FolderBean folder = folderDAO.findFolderByUsernameAndFolderNumber(username, folderIDInt, FolderType.Subfolder);
            if (folder != null) {
                DocumentDAO documentDAO = new DocumentDAO(getDBConnection());
                List<DocumentBean> documents = documentDAO.findDocumentsByUserAndFolder(username, folderIDInt);
                responseBean.setFolderName(folder.getName());
                responseBean.setDocuments(documents);
                hasErrorFindingFolder = false;
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("There was an error while getting the data to display");
            return null;
        } catch (NumberFormatException ignored) {
        }

        if (hasErrorFindingFolder) {
            //Keep the Page
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Could not find the specified folder");
            return null;
        } else {
            return responseBean;
        }
    }
}
