package com.asassi.tiwproject.controllers;

import com.asassi.tiwproject.beans.DocumentBean;
import com.asassi.tiwproject.beans.FolderBean;
import com.asassi.tiwproject.beans.responses.DocumentResponseBean;
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

@WebServlet("/getDocumentData")
public class GetDocumentDataController extends JSONResponderServlet {

    @Override
    protected Object handleGet(HttpServletRequest req, HttpServletResponse resp, WebContext ctx, ServletContext servletContext) throws ServletException, IOException {
        //If the User is not registered (we cannot find the username in the Session), redirect to the Login page
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute(SessionConstants.Username.getRawValue());
        //When the Get is performed, we check if we have a valid document ID, otherwise we redirect to the home page
        String documentID = req.getParameter("document");
        boolean hasErrorFindingDocument = true;
        DocumentDAO documentDAO = new DocumentDAO(getDBConnection());

        DocumentResponseBean responseBean = new DocumentResponseBean();

        try {
            int documentIDInt = Integer.parseInt(documentID);
            DocumentBean document = documentDAO.findDocument(username, documentIDInt);
            if (document != null) {
                FolderDAO folderDAO = new FolderDAO(getDBConnection());
                FolderBean folder = folderDAO.findFolderByUsernameAndFolderNumber(username, document.getParentFolderNumber(), FolderType.Subfolder);
                if (folder != null) {
                    responseBean.setDocument(document);
                    responseBean.setFolderName(folder.getName());
                    hasErrorFindingDocument = false;
                }
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("There was an error while getting the data to display");
            return null;
        } catch (NumberFormatException ignored) {
        }

        if (hasErrorFindingDocument) {
            //Keep the Page
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Could not find the specified document");
            return null;
        } else {
            return responseBean;
        }
    }
}
