package edu.asu.diging.gilesecosystem.web.api.v2;

import java.util.HashMap;
import java.util.Map;

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
import edu.asu.diging.gilesecosystem.web.core.citesphere.ICitesphereConnector;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IUpload;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalUploadService;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.core.service.reprocessing.IReprocessingService;
import edu.asu.diging.gilesecosystem.web.core.users.CitesphereUser;

@Controller
public class V2ReprocessDocumentController {
    @Autowired
    private IReprocessingService reprocessingService;
    
    @Autowired
    private ITransactionalDocumentService documentService;
    
    @Autowired
    private IResponseHelper responseHelper;
    
    @Autowired
    private IPropertiesManager propertyManager;
    
    @Autowired
    private ITransactionalUploadService uploadService;
    
    @Autowired
    private ICitesphereConnector citesphereConnector;
    
    @Value("${giles_check_upload_endpoint_v2}")
    private String uploadEndpoint;
    
    @RequestMapping(value = "/api/v2/resources/documents/{documentId}/reprocess", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> reprocessDocument(@PathVariable("documentId") String documentId, CitesphereToken citesphereToken) {
        IDocument document = documentService.getDocument(documentId);
        Map<String, String> msgs = new HashMap<String, String>();
        if (document == null) {
            msgs.put("errorMsg", "Document with id: " + documentId + " does not exist.");
            msgs.put("errorCode", "404");
            return responseHelper.generateResponse(msgs, HttpStatus.NOT_FOUND);
        }
        if (!citesphereConnector.hasAccess(document.getId(), ((CitesphereUser)citesphereToken.getPrincipal()).getUsername())) {
            Map<String, String> unauthorizedMsgs = new HashMap<String, String>();
            unauthorizedMsgs.put("errorMsg", "User is not authorized to reprocess the document with id " + document.getId());
            unauthorizedMsgs.put("errorCode", "403");
            return responseHelper.generateResponse(unauthorizedMsgs, HttpStatus.FORBIDDEN);
        }
        reprocessingService.reprocessDocument(document);
        IUpload upload = uploadService.getUpload(document.getUploadId());
        msgs.put("id", documentId);
        msgs.put("checkUrl", propertyManager.getProperty(Properties.GILES_URL) + uploadEndpoint + upload.getUploadProgressId());
        return responseHelper.generateResponse(msgs, HttpStatus.OK);
    }
}
