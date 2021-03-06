package edu.asu.diging.gilesecosystem.web.api.v1;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.aspects.access.annotations.TokenCheck;
import edu.asu.diging.gilesecosystem.web.core.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.core.users.User;

@Controller
public class ChangeDocumentAccessController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    public final static String DOCUMENT_ID_PLACEHOLDER = "{docId}";
    public final static String GET_DOCUMENT_PATH = "/rest/documents/" + DOCUMENT_ID_PLACEHOLDER;
    
    @Autowired
    private IFilesManager filesManager;
    
    @Autowired
    private ITransactionalDocumentService documentService;

    @Autowired
    private ISystemMessageHandler messageHandler;

    @TokenCheck
    @RequestMapping(value = GET_DOCUMENT_PATH
            + "/access/change", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public ResponseEntity<String> changeDocumentAccess(@RequestParam(defaultValue = "") String accessToken,
            HttpServletRequest request, @PathVariable("docId") String docId, @RequestParam("access") String access,
            User user) {

        IDocument doc = documentService.getDocument(docId);

        if (!doc.getUsername().equals(user.getUsername())) {
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }

        DocumentAccess docAccess = null;
        try {
            docAccess = DocumentAccess.valueOf(access.toUpperCase());
            if (docAccess == null) {
                logger.error("Incorrect access type");
                return new ResponseEntity<String>("{\"error\": \"Incorrect access type.\" }", HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            messageHandler.handleMessage("Incorrect access type.", e, MessageType.ERROR);
            return new ResponseEntity<String>("{\"error\": \"Incorrect access type.\" }", HttpStatus.BAD_REQUEST);
        }

        try {
            boolean isChangeSuccess = filesManager.changeDocumentAccess(doc, docAccess);
            if (!isChangeSuccess) {
                return new ResponseEntity<String>(
                        "{\"warning\": \"Access type successfully updated for document but one or more files could not be updated.\" }",
                        HttpStatus.OK);
            }
        } catch (UnstorableObjectException e) {
            messageHandler.handleMessage("Could not save updated access type.", e, MessageType.ERROR);
            return new ResponseEntity<String>("{\"error\": \"Could not save updated access type.\" }",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>("{\"success\": \"access type changed.\" }", HttpStatus.OK);
    }

}
