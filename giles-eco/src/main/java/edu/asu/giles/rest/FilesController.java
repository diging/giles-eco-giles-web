package edu.asu.giles.rest;

import java.io.IOException;
import java.io.StringWriter;
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
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.giles.aspects.access.annotations.DocumentAccessCheck;
import edu.asu.giles.aspects.access.annotations.FileTokenAccessCheck;
import edu.asu.giles.aspects.access.annotations.TokenCheck;
import edu.asu.giles.core.DocumentAccess;
import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IUpload;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.rest.util.IJSONHelper;
import edu.asu.giles.users.User;

@Controller
public class FilesController {
    
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IFilesManager filesManager;
    
    @Autowired
    private IJSONHelper jsonHelper;
    
    @TokenCheck
    @RequestMapping(value = "/rest/files/uploads", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getAllUploadsOfUser(@RequestParam(defaultValue = "") String accessToken, 
            HttpServletRequest request, User user) {
        
        Map<String, Map<String, String>> filenames = filesManager.getUploadedFilenames(user.getUsername());
        
        ObjectMapper mapper = new ObjectMapper();
        //mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ArrayNode root = mapper.createArrayNode();
        
        for (String uploadId : filenames.keySet()) {
            ObjectNode node = root.addObject();
            ArrayNode filenameList = node.putArray(uploadId);
            for (Entry<String, String> fileIdAndName : filenames.get(uploadId).entrySet()) {
                ObjectNode fileNode = filenameList.addObject();
                fileNode.put("id", fileIdAndName.getKey());
                fileNode.put("filename", fileIdAndName.getValue());
                System.out.println(fileIdAndName.getValue());
            }
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
        
        return new ResponseEntity<String>(sw.toString(), HttpStatus.OK);
    }

    @TokenCheck
    @RequestMapping(value = "/rest/files/upload/{uploadId}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getFilePathsForUpload(
            @RequestParam(defaultValue = "") String accessToken, 
            HttpServletRequest request,
            @PathVariable("uploadId") String uploadId,
            User user) {

        IUpload upload = filesManager.getUpload(uploadId);
        if (upload == null) {
            return new ResponseEntity<String>(
                    "{'error': 'Upload does not exist.'}", HttpStatus.NOT_FOUND);
        }
        if (!upload.getUsername().equals(user.getUsername())) {
            return new ResponseEntity<String>(
                    "{'error': 'Upload id not valid for user.'}",
                    HttpStatus.BAD_REQUEST);
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
            return new ResponseEntity<String>(
                    "{\"error\": \"Could not write json result.\" }",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(sw.toString(), HttpStatus.OK);
    }
    
    @DocumentAccessCheck
    @RequestMapping(value = "rest/documents/{docId}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getDocument(
            @RequestParam(defaultValue = "") String accessToken,
            HttpServletRequest request,
            @PathVariable("docId") String docId,
            User user) {
        
        IDocument doc = filesManager.getDocument(docId);
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ObjectNode docNode = mapper.createObjectNode();

        jsonHelper.createDocumentJson(doc, mapper, docNode);
        
        StringWriter sw = new StringWriter();
        try {
            mapper.writeValue(sw, docNode);
        } catch (IOException e) {
            return new ResponseEntity<String>(
                    "{\"error\": \"Could not write json result.\" }",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(sw.toString(), HttpStatus.OK);
    }

    
    @FileTokenAccessCheck
    @RequestMapping(value = "/rest/files/{fileId}/content", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getFile(
            @PathVariable String fileId,
            @RequestParam(defaultValue="") String accessToken, 
            User user,
            HttpServletResponse response,
            HttpServletRequest request) {

        IFile file = filesManager.getFile(fileId);
        if (file == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        if (file.getAccess() != DocumentAccess.PUBLIC
                && !file.getUsername().equals(user.getUsername())) {
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }

        byte[] content = filesManager.getFileContent(file);
        response.setContentType(file.getContentType());
        response.setContentLength(content.length);
        response.setHeader("Content-disposition", "filename=\"" + file.getFilename() + "\""); 
        try {
            response.getOutputStream().write(content);
            response.getOutputStream().close();
        } catch (IOException e) {
            logger.error("Could not write to output stream.", e);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(HttpStatus.OK);
    }

}
