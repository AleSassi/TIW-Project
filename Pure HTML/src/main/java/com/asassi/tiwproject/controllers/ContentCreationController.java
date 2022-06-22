package com.asassi.tiwproject.controllers;

import com.asassi.tiwproject.beans.DocumentBean;
import com.asassi.tiwproject.beans.FolderBean;
import com.asassi.tiwproject.constants.*;
import com.asassi.tiwproject.dao.DocumentDAO;
import com.asassi.tiwproject.dao.FolderDAO;
import com.asassi.tiwproject.exceptions.IncorrectFormDataException;
import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.context.WebContext;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.time.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/create")
public class ContentCreationController extends DBConnectedServlet {

    @Override
    protected String getTemplatePage() {
        return "/createPage.html";
    }

    @Override
    protected void handleGet(HttpServletRequest req, HttpServletResponse resp, WebContext ctx, ServletContext servletContext) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute(SessionConstants.Username.getRawValue());
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
            ctx.setVariable(CreateConstants.Hierarchy.getRawValue(), folderDAO.findFolderHierarchy(username));
        } catch (SQLException e) {
            throw new ServletException(e.getMessage());
        }
        if (!req.getHeader("referer").contains("/create")) {
            ctx.setVariable("previousURL", req.getHeader("referer"));
        } else {
            ctx.setVariable("previousURL", "/home");
        }

        //Show the error messages
        String fullErrorCode = req.getParameter("error");
        if (fullErrorCode != null) {
            for (int i = 0; i < fullErrorCode.length(); i++) {
                char errorCode = fullErrorCode.charAt(i);
                if (errorCode == '0') {
                    ctx.setVariable(CreateConstants.InvalidObjectTypeError.getRawValue(), "Invalid object type");
                } else if (errorCode == '1') {
                    ctx.setVariable(CreateConstants.InvalidMainFolderNameError.getRawValue(), "You must specify a valid, non-empty name for the new Folder");
                } else if (errorCode == '2') {
                    ctx.setVariable(CreateConstants.InvalidParentFolderNameError.getRawValue(), "The specified Parent Folder could not be found: it may not exist or you may not have the permission to view it");
                } else if (errorCode == '3') {
                    ctx.setVariable(CreateConstants.InvalidDocNameError.getRawValue(), "You must specify a valid, non-empty Document Name");
                } else if (errorCode == '4') {
                    ctx.setVariable(CreateConstants.InvalidDocTypeError.getRawValue(), "You must specify a valid Document Type (file extension, starting with \".\")");
                } else if (errorCode == '5') {
                    ctx.setVariable(CreateConstants.InvalidDocContentError.getRawValue(), "You must specify a valid Document Content");
                } else if (errorCode == '6') {
                    ctx.setVariable(CreateConstants.InvalidDocNameError.getRawValue(), "You already own a document with the same name in the subfolder. Please choose a new document name");
                } else if (errorCode == '7') {
                    ctx.setVariable(CreateConstants.InvalidMainFolderNameError.getRawValue(), "You already own a folder with the same name. Please choose a new folder name");
                }
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
            resp.sendRedirect(PageConstants.Create.getRawValue() + "?error=0");
        } else {
            try {
                parseUserFormAndExecuteAction(req, resp, ctx, selectedFileType);
                resp.sendRedirect(PageConstants.Create.getRawValue() + "?" + ContentCreationFormField.ContentType.getRawValue() + "=" + doctype);
            } catch (IncorrectFormDataException e) {
                //Show the Template with error strings
                resp.sendRedirect(PageConstants.Create.getRawValue() + "?" + ContentCreationFormField.ContentType.getRawValue() + "=" + doctype + "&error=" + e.getMessage());
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
        Pattern notOnlyWhitespaces = Pattern.compile("[^ ]");
        switch (fileType) {
            case Folder -> {
                //We need to have valid strings from: mainFolderName
                String folderName = StringEscapeUtils.escapeJava(req.getParameter(ContentCreationFormField.FolderName.getRawValue()));
                if (folderName == null || !notOnlyWhitespaces.matcher(folderName).find()) {
                    //Send an error
                    throw new IncorrectFormDataException("1");
                }
                //Create the folder
                FolderDAO folderDAO = new FolderDAO(getDBConnection());
                FolderBean folder = new FolderBean(username, 0, folderName, LocalDateTime.now(), null);
                if (folderDAO.noFolderWithSameNameAtSameHierarchyLevel(folder)) {
                    folderDAO.addFolder(folder);
                } else {
                    throw new IncorrectFormDataException("7");
                }
            }
            case Subfolder -> {
                //We need to have valid strings from: folderName, parentFolder
                String folderName = StringEscapeUtils.escapeJava(req.getParameter(ContentCreationFormField.FolderName.getRawValue()));
                FolderDAO folderDAO = new FolderDAO(getDBConnection());
                String errorCode = "";
                if (folderName == null || !notOnlyWhitespaces.matcher(folderName).find()) {
                    //Send an error
                    errorCode = errorCode + "1";
                } else {
                    try {
                        int parentFolderNumber = Integer.parseInt(req.getParameter(ContentCreationFormField.ParentFolderNumber.getRawValue()));
                        List<FolderBean> userFolders = folderDAO.findFoldersByUsernameAndFolderNumber(username, parentFolderNumber, FolderType.Main);
                        if (userFolders.isEmpty()) {
                            //Send an error
                            errorCode = errorCode + "2";
                        } else {
                            //Create the subfolder
                            FolderBean folder = new FolderBean(username, 0, folderName, LocalDateTime.now(), userFolders.get(0).getFolderNumber());
                            if (folderDAO.noFolderWithSameNameAtSameHierarchyLevel(folder)) {
                                folderDAO.addFolder(folder);
                            } else {
                                errorCode = errorCode + "7";
                            }
                        }
                    } catch (NumberFormatException e) {
                        //Send an error
                        errorCode = errorCode + "2";
                    }
                }
                if (!errorCode.equals("")) {
                    throw new IncorrectFormDataException(errorCode);
                }
            }
            case Document -> {
                //We need to have valid strings from: documentName, documentType, parentFolder, parentSubfolder, documentContent
                Pattern fileExtPattern = Pattern.compile("^[.][^.\s]+");

                String docName = StringEscapeUtils.escapeJava(req.getParameter(ContentCreationFormField.DocumentName.getRawValue()));
                String docExtension = StringEscapeUtils.escapeJava(req.getParameter(ContentCreationFormField.DocumentFileType.getRawValue()));
                String docContent = StringEscapeUtils.escapeJava(req.getParameter(ContentCreationFormField.DocumentContent.getRawValue()));
                String errorCode = "";
                FolderDAO folderDAO = new FolderDAO(getDBConnection());
                DocumentDAO documentDAO = new DocumentDAO(getDBConnection());
                try {
                    int parentSubfolderNumber = Integer.parseInt(req.getParameter(ContentCreationFormField.ParentFolderNumber.getRawValue()));
                    List<FolderBean> userFolders = folderDAO.findFoldersByUsernameAndFolderNumber(username, parentSubfolderNumber, FolderType.Subfolder);
                    if (userFolders.isEmpty()) {
                        //Send an error
                        errorCode += "2";
                    }
                    Matcher matcher = null;
                    if (docName != null) {
                        matcher = notOnlyWhitespaces.matcher(docName);
                    }
                    if (docName == null || !matcher.find()) {
                        //Send an error
                        errorCode += "3";
                    }
                    if (docExtension != null) {
                        matcher = fileExtPattern.matcher(docExtension);
                    }
                    if (docExtension == null || !matcher.find()) {
                        //Send an error
                        errorCode += "4";
                    } else {
                        docExtension = matcher.group();
                    }
                    if (docContent == null) {
                        //Send an error
                        errorCode += "5";
                    }
                    if (errorCode.equals("")) {
                        //Create the Document
                        DocumentBean document = new DocumentBean(username, parentSubfolderNumber, 0, docName, docExtension, LocalDateTime.now(), docContent);
                        if (documentDAO.noDocumentWithSameNameAtSameHierarchyLevel(document)) {
                            documentDAO.addDocument(document);
                        } else {
                            errorCode += "6";
                        }
                    }
                } catch (NumberFormatException e) {
                    //Send an error
                    errorCode += "2";
                }

                if (!errorCode.equals("")) {
                    throw new IncorrectFormDataException(errorCode);
                }
            }
        }
    }
}
