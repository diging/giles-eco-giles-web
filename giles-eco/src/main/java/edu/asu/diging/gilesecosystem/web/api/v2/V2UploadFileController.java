package edu.asu.diging.gilesecosystem.web.api.v2;

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
import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.api.util.IJSONHelper;
import edu.asu.diging.gilesecosystem.web.api.util.IResponseHelper;
import edu.asu.diging.gilesecosystem.web.config.CitesphereToken;
import edu.asu.diging.gilesecosystem.web.config.IUserHelper;
import edu.asu.diging.gilesecosystem.web.core.citesphere.ICitesphereConnector;
import edu.asu.diging.gilesecosystem.web.core.files.impl.StorageStatus;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentType;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IUpload;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.core.service.upload.IUploadService;
import edu.asu.diging.gilesecosystem.web.core.service.upload.impl.UploadService.UploadIds;
import edu.asu.diging.gilesecosystem.web.core.users.AccountStatus;
import edu.asu.diging.gilesecosystem.web.core.users.CitesphereUser;
import edu.asu.diging.gilesecosystem.web.core.users.IUserManager;
import edu.asu.diging.gilesecosystem.web.core.users.User;
import edu.asu.diging.gilesecosystem.web.core.util.IGilesUrlHelper;

@PropertySource("classpath:/config.properties")
@Controller
public class V2UploadFileController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${giles_check_upload_endpoint_v2}")
    private String uploadEndpoint;

    @Autowired
    private IPropertiesManager propertyManager;

    @Autowired
    private IUploadService uploadService;

    @Autowired
    private ITransactionalDocumentService documentService;
    
    @Autowired
    private ICitesphereConnector citesphereConnector;

    @Autowired
    private IJSONHelper jsonHelper;

    @Autowired
    private IGilesUrlHelper urlHelper;

    @Autowired
    private ISystemMessageHandler messageHandler;

    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private IUserHelper userHelper;
    
    @Autowired
    private IResponseHelper responseHelper;

    @RequestMapping(value = "/api/v2/files/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImages(
            @RequestParam(defaultValue = "") String accessToken,
            HttpServletRequest request,
            @RequestParam(value = "access", defaultValue = "PRIVATE") String access,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "document_type", defaultValue = "SINGLE_PAGE") String docType,
            CitesphereToken citesphereToken) {

        DocumentAccess docAccess = DocumentAccess.valueOf(access);
        if (docAccess == null) {
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("errorMsg", "Access type: " + access + " does not exist.");
            msgs.put("errorCode", "400");

            return responseHelper.generateResponse(msgs, HttpStatus.BAD_REQUEST);
        }

        DocumentType documentType = DocumentType.valueOf(docType);
        if (documentType == null) {
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("errorMsg", "Document type: " + docType + " does not exist.");
            msgs.put("errorCode", "400");

            return responseHelper.generateResponse(msgs, HttpStatus.BAD_REQUEST);
        }

        if (files == null || files.length == 0) {
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("errorMsg", "There were no files attached to request.");
            msgs.put("errorCode", "400");

            return responseHelper.generateResponse(msgs, HttpStatus.BAD_REQUEST);
        }

        List<byte[]> fileBytes = new ArrayList<byte[]>();
        for (MultipartFile file : files) {
            try {
                fileBytes.add(file.getBytes());
            } catch (IOException e) {
                messageHandler.handleMessage("Error reading bytes.", e,
                        MessageType.ERROR);
                Map<String, String> msgs = new HashMap<String, String>();
                msgs.put("errorMsg", "File bytes could not be read.");
                msgs.put("errorCode", "500");

                return responseHelper.generateResponse(msgs, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        User user;

        try {
            user = createUser(citesphereToken);
        } catch (UnstorableObjectException e) {
            messageHandler.handleMessage("Could not store non login user.", e,
                    MessageType.ERROR);
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("errorMsg", "An error occurred. Request could not be processed.");
            msgs.put("errorCode", "500");

            return responseHelper.generateResponse(msgs, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        UploadIds ids = uploadService.startUpload(docAccess, documentType, files, fileBytes,
                user);

        Map<String, String> msgs = new HashMap<String, String>();
        msgs.put("id", ids.progressId);
        //msgs.put("documentIds", "[\"" + String.join("\",\"", ids.uploadIds) + "\"]");
        msgs.put("checkUrl",
                propertyManager.getProperty(Properties.GILES_URL) + uploadEndpoint + ids.progressId);

        logger.info("Uploaded file started with id " + ids.progressId);
        return responseHelper.generateResponse(msgs, HttpStatus.OK);
    }

    public User createUser(CitesphereToken citesphereToken)
            throws UnstorableObjectException {
        CitesphereUser citesphereUser = (CitesphereUser) citesphereToken.getPrincipal();
        User user = new User();
        user.setUsername(userHelper.createUsername(citesphereUser.getUsername(), citesphereUser.getAuthorizingClient()));
        user.setProvider(citesphereUser.getAuthorizingClient());
        user.setUserIdOfProvider(citesphereUser.getUsername());

        if (userManager.findUserByProviderUserId(user.getUserIdOfProvider(),
                user.getProvider()) == null) {
            user.setAccountStatus(AccountStatus.NON_LOGIN);
            userManager.addUser(user);
        }

        return user;
    }

    @RequestMapping(value = "/api/v2/files/upload/check/{id}", method = RequestMethod.GET)
    public ResponseEntity<String> checkAndGetResults(HttpServletRequest request,
            @PathVariable String id, CitesphereToken citesphereToken) {

        List<StorageStatus> statusList = uploadService.getUploadStatus(id);
        if (statusList == null || statusList.isEmpty()) {
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("errorMsg", "Upload does not exist.");
            msgs.put("errorCode", "404");

            return responseHelper.generateResponse(msgs, HttpStatus.NOT_FOUND);
        }
        
        // check if user has access to all documents in upload via progress id
        for (StorageStatus status : statusList) {
            if (!citesphereConnector.hasAccessViaProgressId(id, ((CitesphereUser)citesphereToken.getPrincipal()).getUsername())) {
                return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
            }
        }

        boolean complete = true;
        for (StorageStatus status : statusList) {
            complete = complete && (status.getStatus() == RequestStatus.COMPLETE
                    || status.getStatus() == RequestStatus.FAILED);
        }

        if (!complete) {
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("msg", "Upload in progress. Please check back later.");
            msgs.put("msgCode", "010");

            // get upload id from first document
            // there should only be one upload id for a process id
            if (statusList.get(0).getDocument() != null) {
                String uploadId = statusList.get(0).getDocument().getUploadId();
                String uploadUrl = urlHelper.getUrl(V2FilesController.GET_UPLOAD_PATH
                        .replace(V2FilesController.UPLOAD_ID_PLACEHOLDER, uploadId));
                msgs.put("uploadUrl", uploadUrl);
                msgs.put("uploadId", uploadId);
            }

            return responseHelper.generateResponse(msgs, HttpStatus.ACCEPTED);
        }

        Set<String> docIds = new HashSet<String>();
        statusList.forEach(status -> docIds.add(status.getDocument().getId()));

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ArrayNode root = mapper.createArrayNode();

        for (String docId : docIds) {
            IDocument doc = documentService.getDocument(docId);
            ObjectNode docNode = mapper.createObjectNode();
            root.add(docNode);

            jsonHelper.createDocumentJson(doc, mapper, docNode);
        }

        StringWriter sw = new StringWriter();
        try {
            mapper.writeValue(sw, root);
        } catch (IOException e) {
            messageHandler.handleMessage("Could not write json.", e, MessageType.ERROR);
            return new ResponseEntity<String>(
                    "{\"error\": \"Could not write json result.\" }",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(sw.toString(), HttpStatus.OK);
    }
}
