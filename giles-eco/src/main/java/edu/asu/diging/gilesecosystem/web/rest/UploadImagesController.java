package edu.asu.diging.gilesecosystem.web.rest;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.aspects.access.annotations.TokenCheck;
import edu.asu.diging.gilesecosystem.web.core.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.DocumentType;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.files.impl.StorageStatus;
import edu.asu.diging.gilesecosystem.web.rest.util.IJSONHelper;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.service.upload.IUploadService;
import edu.asu.diging.gilesecosystem.web.users.User;
import edu.asu.diging.gilesecosystem.web.util.FileUploadHelper;

@PropertySource("classpath:/config.properties")
@Controller
public class UploadImagesController {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${giles_check_upload_endpoint}")
    private String uploadEndpoint;
    
    @Autowired
    private IPropertiesManager propertyManager;
    
    @Autowired
    private FileUploadHelper uploadHelper;
    
    @Autowired
    private IUploadService uploadService;

    @Autowired
    private IFilesManager filesManager;
    
    @Autowired
    private IJSONHelper jsonHelper;

    @TokenCheck
    @RequestMapping(value = "/rest/files/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImages(
            @RequestParam(defaultValue = "") String accessToken,
            HttpServletRequest request,
            @RequestParam(value = "access", defaultValue = "PRIVATE") String access,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "document_type", defaultValue = "SINGLE_PAGE") String docType, User user) {

        DocumentAccess docAccess = DocumentAccess.valueOf(access);
        if (docAccess == null) {
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("errorMsg", "Access type: " + access + " does not exist.");
            msgs.put("errorCode", "400");
            
            return generateResponse(msgs, HttpStatus.BAD_REQUEST);
        }
        
        DocumentType documentType = DocumentType.valueOf(docType);
        if (documentType == null) {
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("errorMsg", "Document type: " + docType + " does not exist.");
            msgs.put("errorCode", "400");
            
            return generateResponse(msgs, HttpStatus.BAD_REQUEST);
        }
        
        List<byte[]> fileBytes = new ArrayList<byte[]>();
        for (MultipartFile file : files) {
            try {
                fileBytes.add(file.getBytes());
            } catch (IOException e) {
                logger.error("Error reading bytes.", e);
                Map<String, String> msgs = new HashMap<String, String>();
                msgs.put("errorMsg", "File bytes could not be read.");
                msgs.put("errorCode", "500");
                
                return generateResponse(msgs, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        String id = uploadService.startUpload(docAccess, documentType, files, fileBytes, user);
       
        Map<String, String> msgs = new HashMap<String, String>();
        msgs.put("id", id);
        msgs.put("checkUrl", propertyManager.getProperty(Properties.GILES_URL) + uploadEndpoint + id);
        
        return generateResponse(msgs, HttpStatus.OK);
    }
    
    @TokenCheck
    @RequestMapping(value = "/rest/files/upload/check/{id}", method = RequestMethod.GET)
    public ResponseEntity<String> checkAndGetResults(
            @RequestParam(defaultValue = "") String accessToken,
            HttpServletRequest request,
            @PathVariable String id, 
            User user) {
        
        List<StorageStatus> statusList = uploadService.getUpload(id);
        if (statusList == null) {
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("errorMsg", "Upload does not exist.");
            msgs.put("errorCode", "404");
            
            return generateResponse(msgs, HttpStatus.NOT_FOUND);
        }
        
        boolean complete = true;
        for (StorageStatus status : statusList) {
            complete = complete && status.getStatus() == RequestStatus.COMPLETE;
        }
        
        if (!complete) {
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("msg", "Upload in progress. Please check back later.");
            msgs.put("msgCode", "010");
            
            return generateResponse(msgs, HttpStatus.ACCEPTED);
        }
        
        Set<String> docIds = new HashSet<String>();
        statusList.forEach(status -> docIds.add(status.getDocument().getDocumentId()));

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ArrayNode root = mapper.createArrayNode();

        
        for (String docId : docIds) {
            IDocument doc = filesManager.getDocument(docId);
            ObjectNode docNode = mapper.createObjectNode();
            root.add(docNode);

            jsonHelper.createDocumentJson(doc, mapper, docNode);  
        }
        
        StringWriter sw = new StringWriter();
        try {
            mapper.writeValue(sw, root);
        } catch (IOException e) {
            logger.error("Could not write json.", e);
            return new ResponseEntity<String>(
                    "{\"error\": \"Could not write json result.\" }",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(sw.toString(), HttpStatus.OK);
    }
    
    private ResponseEntity<String> generateResponse(Map<String, String> msgs, HttpStatus status) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ObjectNode root = mapper.createObjectNode();
        for (String key : msgs.keySet()) {
            root.put(key, msgs.get(key));
        }
        
        StringWriter sw = new StringWriter();
        try {
            mapper.writeValue(sw, root);
        } catch (IOException e) {
            logger.error("Could not write json.", e);
            return new ResponseEntity<String>(
                    "{\"errorMsg\": \"Could not write json result.\", \"errorCode\": \"errorCode\": \"500\" }",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return new ResponseEntity<String>(sw.toString(), status);
    }
}
