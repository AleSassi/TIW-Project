package com.asassi.tiwproject.controllers;

import com.asassi.tiwproject.beans.DocumentBean;
import com.asassi.tiwproject.beans.FolderBean;
import com.asassi.tiwproject.beans.responses.CreateDocumentResponseBean;
import com.asassi.tiwproject.constants.ContentCreationFormField;
import com.asassi.tiwproject.constants.CreateConstants;
import com.asassi.tiwproject.constants.FolderType;
import com.asassi.tiwproject.constants.SessionConstants;
import com.asassi.tiwproject.dao.DocumentDAO;
import com.asassi.tiwproject.dao.FolderDAO;
import com.asassi.tiwproject.exceptions.IncorrectFormDataException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/CreateDocument")
@MultipartConfig
public class CreateDocumentController extends JSONResponderServlet {

    @Override
    protected Object handleGet(HttpServletRequest req, HttpServletResponse resp, WebContext ctx, ServletContext servletContext) throws ServletException, IOException {
        return null;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        parseUserFormAndExecuteAction(req, resp);
    }

    private void parseUserFormAndExecuteAction(HttpServletRequest req, HttpServletResponse resp) {
        //The file type is valid, now we need to check whether its data is valid as well
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute(SessionConstants.Username.getRawValue());
        Random randomizer = new Random();

        //We need to have valid strings from: documentName, documentType, parentFolder, parentSubfolder, documentContent
        String docName = StringEscapeUtils.escapeJava(req.getParameter(ContentCreationFormField.DocumentName.getRawValue()));
        String docExtension = StringEscapeUtils.escapeJava(req.getParameter(ContentCreationFormField.DocumentFileType.getRawValue()));
        String docContent = StringEscapeUtils.escapeJava(req.getParameter(ContentCreationFormField.DocumentContent.getRawValue()));
        FolderDAO folderDAO = new FolderDAO(getDBConnection());
        DocumentDAO documentDAO = new DocumentDAO(getDBConnection());
        CreateDocumentResponseBean responseBean = new CreateDocumentResponseBean();
        Pattern notOnlyWhitespaces = Pattern.compile("[^ ]");
        Pattern fileExtPattern = Pattern.compile("^[.][^.\s]+");
        try {
            int parentSubfolderNumber = Integer.parseInt(req.getParameter(ContentCreationFormField.ParentFolderNumber.getRawValue()));
            List<FolderBean> userFolders = folderDAO.findFoldersByUsernameAndFolderNumber(username, parentSubfolderNumber, FolderType.Subfolder);
            if (userFolders.isEmpty()) {
                //Send an error
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseBean.setDocumentNameError("The specified Parent Folder could not be found");
            } else {
                Matcher matcher = null;
                if (docName == null || !notOnlyWhitespaces.matcher(docName).find()) {
                    //Send an error
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    responseBean.setDocumentNameError("You must specify a valid Document Name");
                }
                if (docExtension != null) {
                    matcher = fileExtPattern.matcher(docExtension);
                }
                if (docExtension == null || !matcher.find() || !(docExtension.startsWith(".") && docExtension.lastIndexOf(".") == 0) && !docExtension.substring(docExtension.lastIndexOf(".") + 1).isEmpty()) {
                    //Send an error
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    responseBean.setDocumentExtensionError("You must specify a valid Document Type (file extension, starting with \\\".\\\")");
                }
                if (docContent == null || !notOnlyWhitespaces.matcher(docContent).find()) {
                    //Send an error
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    responseBean.setDocumentExtensionError("You must specify a valid Document Content");
                }
                if (resp.getStatus() != HttpServletResponse.SC_BAD_REQUEST) {
                    //Create the Document
                    DocumentBean document = new DocumentBean(username, parentSubfolderNumber, randomizer.nextInt(0, Integer.MAX_VALUE), docName, docExtension, LocalDateTime.now(), docContent);
                    if (documentDAO.noDocumentWithSameNameAtSameHierarchyLevel(document)) {
                        document.setDocumentNumber(documentDAO.addDocument(document));
                    } else {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        responseBean.setDocumentNameError("You already own a document in the same folder with this name. Please choose a unique name");
                    }
                }
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseBean.setDocumentNameError("You must specify a valid parent subfolder");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseBean.setDocumentNameError("There was an error while getting the data to display");
        }
        try {
            sendAsJSON(responseBean, resp);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
