package edu.asu.diging.gilesecosystem.web.api.v2;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.api.util.IResponseHelper;
import edu.asu.diging.gilesecosystem.web.config.CitesphereToken;
import edu.asu.diging.gilesecosystem.web.config.IUserHelper;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.delete.IDeleteDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.core.users.CitesphereUser;

@Controller
public class V2DeleteDocumentController {

    @Autowired
    private IDeleteDocumentService deleteDocumentService;
    
    @Autowired
    private ITransactionalDocumentService documentService;
    
    @Autowired
    private IResponseHelper responseHelper;
    
    @Autowired
    private IPropertiesManager propertyManager;
    
    @Autowired
    private IUserHelper userHelper;
    
    @Value("${giles_check_deletion_endpoint_v2}")
    private String deleteEndpoint;
    
    @RequestMapping(value = "/api/v2/resources/documents/{documentId}", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> deleteDocument(@PathVariable("documentId") String documentId) {
        IDocument document = documentService.getDocument(documentId);
        Map<String, String> msgs = new HashMap<String, String>();
        if (document == null) {
            msgs.put("errorMsg", "Document Id: " + documentId + " does not exist.");
            msgs.put("errorCode", "404");
            return responseHelper.generateResponse(msgs, HttpStatus.NOT_FOUND);
        }
        deleteDocumentService.deleteDocument(document);
        msgs.put("checkUrl", propertyManager.getProperty(Properties.GILES_URL) + deleteEndpoint + documentId);
        
        return responseHelper.generateResponse(msgs, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/api/v2/files/deletion/check/{documentId}", method = RequestMethod.GET)
    public ResponseEntity<String> checkDocumentDeletion(HttpServletRequest request,
            @PathVariable String documentId, CitesphereToken citesphereToken) {
        IDocument document = documentService.getDocument(documentId);
        String username = document.getUsername();
        Map<String, String> msgs = new HashMap<String, String>();
        CitesphereUser user = (CitesphereUser) citesphereToken.getPrincipal();
        String usernameInSystem = userHelper.createUsername(user.getUsername(), user.getAuthorizingClient());
        if (!usernameInSystem.equals(username)) {
            msgs.put("errorMsg", "User is not authorized to check status.");
            msgs.put("errorCode", "401");

            return responseHelper.generateResponse(msgs, HttpStatus.UNAUTHORIZED);
        } else if (document == null) {
            msgs.put("successMessage", "Document Id: " + documentId + " is deleted.");
            return responseHelper.generateResponse(msgs, HttpStatus.OK);
        }
        msgs.put("progressInfo", "Deletion in progress. Please check back later.");
        return responseHelper.generateResponse(msgs, HttpStatus.OK);
    }
}
