package com.asassi.tiwproject.controllers;

import com.asassi.tiwproject.beans.FolderBean;
import com.asassi.tiwproject.beans.responses.NestedFolderBean;
import com.asassi.tiwproject.constants.*;
import com.asassi.tiwproject.dao.FolderDAO;
import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

@WebServlet("/CreateFolder")
@MultipartConfig
public class CreateFolderController extends JSONResponderServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            parseUserFormAndExecuteAction(req, resp);
        } catch (IOException | SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("There was an error while getting the data to display");
        }
    }

    private void parseUserFormAndExecuteAction(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        //The file type is valid, now we need to check whether its data is valid as well
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute(SessionConstants.Username.getRawValue());
        String parentFolderID = StringEscapeUtils.escapeJava(req.getParameter("parentFolderID"));
        String folderName = StringEscapeUtils.escapeJava(req.getParameter("createForm_folderTitle"));
        Random randomizer = new Random();
        Pattern notOnlyWhitespaces = Pattern.compile("[^ ]");

        if (folderName == null || !notOnlyWhitespaces.matcher(folderName).find()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("You must specify a folder name");
        } else {
            FolderDAO folderDAO = new FolderDAO(getDBConnection());
            if (parentFolderID == null) {
                //Interpret as main folder addition
                FolderBean folder = new FolderBean(username, randomizer.nextInt(0, Integer.MAX_VALUE), folderName, LocalDateTime.now(), null);
                if (folderDAO.noFolderWithSameNameAtSameHierarchyLevel(folder)) {
                    folder.setFolderNumber(folderDAO.addFolder(folder));
                    sendAsJSON(new NestedFolderBean(folder, null), resp);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("You already own a folder with the same name. Please choose another name");
                }
            } else {
                //Subfolder addition
                try {
                    int parentFolderNumber = Integer.parseInt(parentFolderID);
                    List<FolderBean> userFolders = folderDAO.findFoldersByUsernameAndFolderNumber(username, parentFolderNumber, FolderType.Main);
                    if (userFolders.isEmpty()) {
                        //Send an error
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().println("The specified Parent Folder could not be found");
                    } else {
                        //Create the subfolder
                        FolderBean folder = new FolderBean(username, randomizer.nextInt(0, Integer.MAX_VALUE), folderName, LocalDateTime.now(), userFolders.get(0).getFolderNumber());
                        if (folderDAO.noFolderWithSameNameAtSameHierarchyLevel(folder)) {
                            folder.setFolderNumber(folderDAO.addFolder(folder));
                            sendAsJSON(folder, resp);
                        } else {
                            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            resp.getWriter().write("You already own a folder with the same name. Please choose another name");
                        }
                    }
                } catch (NumberFormatException e) {
                    //Send an error
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println("You must specify a valid parent folder");
                }
            }
        }
    }

    @Override
    protected Object handleGet(HttpServletRequest req, HttpServletResponse resp, WebContext ctx, ServletContext servletContext) throws ServletException, IOException {
        return null;
    }
}
