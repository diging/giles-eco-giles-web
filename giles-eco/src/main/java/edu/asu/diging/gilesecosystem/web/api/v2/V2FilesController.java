package edu.asu.diging.gilesecosystem.web.api.v2;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.web.api.util.IJSONHelper;
import edu.asu.diging.gilesecosystem.web.config.CitesphereToken;
import edu.asu.diging.gilesecosystem.web.core.citesphere.ICitesphereConnector;
import edu.asu.diging.gilesecosystem.web.core.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.IUpload;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalUploadService;
import edu.asu.diging.gilesecosystem.web.core.service.reprocessing.IReprocessingService;
import edu.asu.diging.gilesecosystem.web.core.users.CitesphereUser;
import edu.asu.diging.gilesecosystem.web.core.users.User;
import edu.asu.diging.gilesecosystem.web.web.util.DigilibHelper;

@Controller
public class V2FilesController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public final static String DOCUMENT_ID_PLACEHOLDER = "{docId}";
    public final static String GET_DOCUMENT_PATH = "/api/v2/resources/documents/"
            + DOCUMENT_ID_PLACEHOLDER;

    public final static String FILE_ID_PLACEHOLDER = "{fileId}";
    public final static String DOWNLOAD_FILE_URL = "/api/v2/resources/files/"
            + FILE_ID_PLACEHOLDER + "/content";

    public final static String UPLOAD_ID_PLACEHOLDER = "{uploadId}";
    public final static String GET_UPLOAD_PATH = "/api/v2/resources/files/upload/"
            + UPLOAD_ID_PLACEHOLDER;
    
    public final static String POST_REPROCESS_DOCUMENT_PATH = "/api/v2/resources/documents/"
            + DOCUMENT_ID_PLACEHOLDER + "reprocess";

    @Autowired
    private IFilesManager filesManager;
    
    @Autowired
    private ICitesphereConnector citesphereConnector;

    @Autowired
    private ITransactionalDocumentService documentService;

    @Autowired
    private ITransactionalFileService fileService;

    @Autowired
    private ITransactionalUploadService uploadService;

    @Autowired
    private IJSONHelper jsonHelper;

    @Autowired
    private ISystemMessageHandler messageHandler;

    @Autowired
    private DigilibHelper digilibHelper;
    
    @Autowired
    private IReprocessingService reprocessingService;

    @RequestMapping(value = "/api/v2/resources/files/uploads", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getAllUploadsOfUser(
            @RequestParam(defaultValue = "") String accessToken,
            HttpServletRequest request, CitesphereToken citesphereToken) {
        
        // check with citesphere
        Map<String, Map<String, String>> filenames = filesManager
                .getUploadedFilenames(citesphereToken.getName());

        ObjectMapper mapper = new ObjectMapper();
        // mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ArrayNode root = mapper.createArrayNode();

        for (String uploadId : filenames.keySet()) {
            ObjectNode node = root.addObject();
            ArrayNode filenameList = node.putArray(uploadId);
            for (Entry<String, String> fileIdAndName : filenames.get(uploadId)
                    .entrySet()) {
                ObjectNode fileNode = filenameList.addObject();
                fileNode.put("id", fileIdAndName.getKey());
                fileNode.put("filename", fileIdAndName.getValue());
            }
        }

        StringWriter sw = new StringWriter();
        try {
            mapper.writeValue(sw, root);
        } catch (IOException e) {
            messageHandler.handleMessage("Could not write json.", e, MessageType.ERROR);
            return new ResponseEntity<String>(
                    "{\"errorMsg\": \"Could not write json result.\", \"errorCode\": \"errorCode\": \"500\" }",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(sw.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = GET_UPLOAD_PATH, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getUpload(
            @RequestParam(defaultValue = "") String accessToken,
            HttpServletRequest request, @PathVariable("uploadId") String uploadId,
            User user) {

        IUpload upload = uploadService.getUpload(uploadId);
        if (upload == null) {
            return new ResponseEntity<String>("{'error': 'Upload does not exist.'}",
                    HttpStatus.NOT_FOUND);
        }
        if (!upload.getUsername().equals(user.getUsername())) {
            return new ResponseEntity<String>(
                    "{'error': 'Upload id not valid for user.'}", HttpStatus.BAD_REQUEST);
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ArrayNode root = mapper.createArrayNode();

        List<IDocument> docs = filesManager.getDocumentsByUploadId(uploadId);

        // filesManager.getPathOfFile(file)

        for (IDocument doc : docs) {
            ObjectNode docNode = mapper.createObjectNode();
            jsonHelper.createDocumentJson(doc, mapper, docNode);
            root.add(docNode);

        }

        StringWriter sw = new StringWriter();
        try {
            mapper.writeValue(sw, root);
        } catch (IOException e) {
            messageHandler.handleMessage(e.getMessage(), e, MessageType.ERROR);
            return new ResponseEntity<String>(
                    "{\"error\": \"Could not write json result.\" }",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(sw.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = GET_DOCUMENT_PATH, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getDocument(
            @RequestParam(defaultValue = "") String accessToken,
            HttpServletRequest request, @PathVariable("docId") String docId, User user) {

        IDocument doc = documentService.getDocument(docId);

        if (doc == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ObjectNode docNode = mapper.createObjectNode();

        jsonHelper.createDocumentJson(doc, mapper, docNode);

        StringWriter sw = new StringWriter();
        try {
            mapper.writeValue(sw, docNode);
        } catch (IOException e) {
            messageHandler.handleMessage(e.getMessage(), e, MessageType.ERROR);
            return new ResponseEntity<String>(
                    "{\"error\": \"Could not write json result.\" }",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(sw.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = DOWNLOAD_FILE_URL, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getFile(@PathVariable String fileId,
            @RequestParam(defaultValue = "") String accessToken, CitesphereToken citesphereToken,
            HttpServletResponse response, HttpServletRequest request)
            throws UnsupportedEncodingException {
        
        IFile file = fileService.getFileById(fileId);
        if (file == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        
        if (!citesphereConnector.hasAccess(file.getDocumentId(), ((CitesphereUser)citesphereToken.getPrincipal()).getUsername())) {
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }
        
        Map<String, String[]> allParameters = new HashMap<>(request.getParameterMap());
        // if we have an image and have parameters, pass this on to digilib
        if (file.getContentType() != null && file.getContentType().startsWith("image/")) {

            allParameters.remove("accessToken");
            if (!allParameters.isEmpty()) {
                allParameters.put("fn", new String[] { file.getFilepath() });
                digilibHelper.getDigilibResponse(allParameters, response);
            }
        }

        byte[] content = filesManager.getFileContent(file);
        if (content == null) {
            logger.error("Could not retrieve file content.");
            return new ResponseEntity<String>(
                    "{\"error\": \"Could not retrieve file content. Most likely, Nepomuk is down.\" }",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.setContentType(file.getContentType());
        response.setContentLength(content.length);
        response.setHeader("Content-disposition",
                "filename=\"" + file.getFilename() + "\"");
        try {
            response.getOutputStream().write(content);
            response.getOutputStream().close();
        } catch (IOException e) {
            messageHandler.handleMessage("Could not write to output stream.", e,
                    MessageType.ERROR);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "/documents/{documentId}/reprocess", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> reprocessDocument(@PathVariable("documentId") String documentId) {
        IDocument document = documentService.getDocument(documentId);
        if (document == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        reprocessingService.reprocessDocument(document);
        return new ResponseEntity<String>(HttpStatus.OK);
    }

}
