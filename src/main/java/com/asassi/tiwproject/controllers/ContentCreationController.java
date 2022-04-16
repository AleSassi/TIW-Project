package com.asassi.tiwproject.controllers;

import com.asassi.tiwproject.beans.DocumentBean;
import com.asassi.tiwproject.beans.FolderBean;
import com.asassi.tiwproject.constants.*;
import com.asassi.tiwproject.dao.DocumentDAO;
import com.asassi.tiwproject.dao.FolderDAO;
import com.asassi.tiwproject.exceptions.IncorrectFormDataException;
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
import java.util.Random;

@WebServlet("/create")
public class ContentCreationController extends DBConnectedServlet {

    @Override
    protected String getTemplatePage() {
        return "/createPage.html";
    }

    @Override
    protected void handleGet(HttpServletRequest req, HttpServletResponse resp, WebContext ctx, ServletContext servletContext) throws ServletException, IOException {
        //If the User is not registered (we cannot find the username in the Session), redirect to the Login page
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute(SessionConstants.Username.getRawValue());
        if (username == null) {
            resp.sendRedirect(PageConstants.Default.getRawValue());
        } else {
            //When the Get is performed, we check if we have a doctype selected, otherwise we send the default page parameters
            String doctype = req.getParameter(ContentCreationFormField.ContentType.getRawValue());
            int selectedFileType = UserCreatableFileTypes.Folder.getRawValue();
            if (doctype != null) {
                try {
                    selectedFileType = Integer.parseInt(doctype);
                } catch (NumberFormatException e) {
                    // The number is not an int, we use the default value
                }
            }
            //Sanitize the converted in to be within the range
            selectedFileType = UserCreatableFileTypes.restrictToValidRawValue(selectedFileType);
            FolderDAO folderDAO = new FolderDAO(getDBConnection());
            ctx.setVariable(CreateConstants.Username.getRawValue(), username);
            ctx.setVariable(CreateConstants.FileType.getRawValue(), selectedFileType);
            try {
                HashMap<FolderBean, List<FolderBean>> folderMap = new HashMap<>();
                List<FolderBean> mainFolders = folderDAO.findFoldersByUsername(username, FolderType.Main);
                for (FolderBean mainFolder: mainFolders) {
                    List<FolderBean> subfolders = folderDAO.findSubfoldersOfFolder(username, mainFolder.getFolderNumber());
                    if (!subfolders.isEmpty()) {
                        folderMap.put(mainFolder, subfolders);
                    }
                }
                ctx.setVariable(CreateConstants.ParentFolders.getRawValue(), folderMap);
            } catch (SQLException e) {
                throw new ServletException(e.getMessage());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String doctype = req.getParameter(ContentCreationFormField.ContentType.getRawValue());
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
        int selectedFileType = -1;
        if (doctype != null) {
            try {
                selectedFileType = Integer.parseInt(doctype);
            } catch (NumberFormatException e) {
                // The number is not an int, we use the default value
            }
        }
        //If the file type is incorrect, we reload the page (we might have incorrect parameter fields)
        if (selectedFileType < 0 || selectedFileType > 2) {
            resp.sendRedirect(PageConstants.Create.getRawValue());
        } else {
            try {
                parseUserFormAndExecuteAction(req, resp, ctx, selectedFileType);
                resp.sendRedirect(PageConstants.Create.getRawValue());
            } catch (IncorrectFormDataException e) {
                //Show the Template with error strings
                HttpSession session = req.getSession();
                String username = (String) session.getAttribute(SessionConstants.Username.getRawValue());
                ctx.setVariable(CreateConstants.FileType.getRawValue(), selectedFileType);
                ctx.setVariable(CreateConstants.Username.getRawValue(), username);
                showTemplatePage(ctx, resp);
            } catch (Exception e) {
                throw new ServletException(e.getMessage());
            }
        }
    }

    private void parseUserFormAndExecuteAction(HttpServletRequest req, HttpServletResponse resp, WebContext ctx, int selectedFileType) throws IncorrectFormDataException, SQLException {
        //The file type is valid, now we need to check whether its data is valid as well
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute(SessionConstants.Username.getRawValue());
        UserCreatableFileTypes fileType = UserCreatableFileTypes.getFileType(selectedFileType);
        ServletContext servletContext = getServletContext();
        Random randomizer = new Random();
        switch (fileType) {
            case Folder -> {
                //We need to have valid strings from: mainFolderName
                String folderName = req.getParameter(ContentCreationFormField.FolderName.getRawValue());
                if (folderName == null) {
                    //Send an error
                    ctx.setVariable(CreateConstants.InvalidMainFolderNameError.getRawValue(), "You must specify a name for the new Folder");
                    throw new IncorrectFormDataException();
                }
                //Create the folder
                FolderDAO folderDAO = new FolderDAO(getDBConnection());
                folderDAO.addFolder(new FolderBean(username, randomizer.nextInt(0, Integer.MAX_VALUE), folderName, new java.sql.Date((new java.util.Date()).getTime()), FolderType.Main.getRawValue(), null, null));
            }
            case Subfolder -> {
                //We need to have valid strings from: folderName, parentFolder
                String folderName = req.getParameter(ContentCreationFormField.FolderName.getRawValue());
                FolderDAO folderDAO = new FolderDAO(getDBConnection());
                if (folderName == null) {
                    //Send an error
                    ctx.setVariable(CreateConstants.InvalidMainFolderNameError.getRawValue(), "You must specify a name for the new Folder");
                    throw new IncorrectFormDataException();
                }
                try {
                    int parentFolderNumber = Integer.parseInt(req.getParameter(ContentCreationFormField.ParentFolderNumber.getRawValue()));
                    List<FolderBean> userFolders = folderDAO.findFoldersByUsernameAndFolderNumber(username, parentFolderNumber, FolderType.Main);
                    if (userFolders.isEmpty()) {
                        //Send an error
                        ctx.setVariable(CreateConstants.InvalidMainFolderNameError.getRawValue(), "The specified Parent Folder could not be found");
                        throw new IncorrectFormDataException();
                    }
                    //Create the subfolder
                    folderDAO.addFolder(new FolderBean(username, randomizer.nextInt(0, Integer.MAX_VALUE), folderName, new java.sql.Date((new java.util.Date()).getTime()), FolderType.Subfolder.getRawValue(), username, userFolders.get(0).getFolderNumber()));
                } catch (NumberFormatException e) {
                    //Send an error
                    ctx.setVariable(CreateConstants.InvalidParentFolderNameError.getRawValue(), "You must specify a valid parent folder");
                    throw new IncorrectFormDataException();
                }
            }
            case Document -> {
                //We need to have valid strings from: documentName, documentType, parentFolder, parentSubfolder, documentContent
                String docName = req.getParameter(ContentCreationFormField.DocumentName.getRawValue());
                String docExtension = req.getParameter(ContentCreationFormField.DocumentFileType.getRawValue());
                String docContent = req.getParameter(ContentCreationFormField.DocumentContent.getRawValue());
                FolderDAO folderDAO = new FolderDAO(getDBConnection());
                DocumentDAO documentDAO = new DocumentDAO(getDBConnection());
                try {
                    int parentSubfolderNumber = Integer.parseInt(req.getParameter(ContentCreationFormField.ParentFolderNumber.getRawValue()));
                    List<FolderBean> userFolders = folderDAO.findFoldersByUsernameAndFolderNumber(username, parentSubfolderNumber, FolderType.Subfolder);
                    if (userFolders.isEmpty()) {
                        //Send an error
                        ctx.setVariable(CreateConstants.InvalidMainFolderNameError.getRawValue(), "The specified Parent Folder could not be found");
                        throw new IncorrectFormDataException();
                    }
                    if (docName == null) {
                        //Send an error
                        ctx.setVariable(CreateConstants.InvalidDocNameError.getRawValue(), "You must specify a valid Document Name");
                        throw new IncorrectFormDataException();
                    }
                    if (docExtension == null || !(docExtension.startsWith(".") && docExtension.lastIndexOf(".") == 0) && !docExtension.substring(docExtension.lastIndexOf(".") + 1).isEmpty()) {
                        //Send an error
                        ctx.setVariable(CreateConstants.InvalidDocTypeError.getRawValue(), "You must specify a valid Document Type (file extension, starting with \".\")");
                        throw new IncorrectFormDataException();
                    }
                    if (docContent == null) {
                        //Send an error
                        ctx.setVariable(CreateConstants.InvalidDocContentError.getRawValue(), "You must specify a valid Document Content");
                        throw new IncorrectFormDataException();
                    }
                    //Create the Document
                    documentDAO.addDocument(new DocumentBean(username, parentSubfolderNumber, randomizer.nextInt(0, Integer.MAX_VALUE), docName, docExtension, new java.sql.Date((new java.util.Date()).getTime()), docContent));
                } catch (NumberFormatException e) {
                    //Send an error
                    ctx.setVariable(CreateConstants.InvalidMainFolderNameError.getRawValue(), "You must specify a valid parent Folder");
                    throw new IncorrectFormDataException();
                }
            }
        }

    }
}
