package edu.asu.giles.rest.util.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.giles.core.DocumentAccess;
import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IPage;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.rest.util.IJSONHelper;

@Component
public class JSONHelper implements IJSONHelper {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IFilesManager filesManager;

    /* (non-Javadoc)
     * @see edu.asu.giles.rest.util.IJSONHelper#createDocumentJson(edu.asu.giles.core.IDocument, com.fasterxml.jackson.databind.ObjectMapper, com.fasterxml.jackson.databind.node.ObjectNode)
     */
    @Override
    public void createDocumentJson(IDocument doc, ObjectMapper mapper, ObjectNode docNode) {
        docNode.put("documentId", doc.getDocumentId());
        docNode.put("uploadId", doc.getUploadId());
        docNode.put("uploadedDate", doc.getCreatedDate());
        docNode.put("access", (doc.getAccess() != null ? doc.getAccess()
                .toString() : DocumentAccess.PRIVATE.toString()));
        IFile uploadedFile = filesManager.getFile(doc.getUploadedFileId());
        
        if (uploadedFile != null) {
            ObjectNode uploadedFileNode = createFileJsonObject(mapper, uploadedFile);
            docNode.set("uploadedFile", uploadedFileNode);
        }
        
        if (doc.getExtractedTextFileId() != null) {
            IFile extractedTextFile = filesManager.getFile(doc.getExtractedTextFileId());
            docNode.set("extractedText", createFileJsonObject(mapper, extractedTextFile));
        }
        
        if (!doc.getPages().isEmpty()) {
            ArrayNode pagesArray = docNode.putArray("pages");
            for (IPage page : doc.getPages()) {
                ObjectNode pageNode = pagesArray.addObject();
                pageNode.put("nr", page.getPageNr());
                if (page.getImageFileId() != null) {
                    IFile imageFile = filesManager.getFile(page.getImageFileId());
                    pageNode.set("image", createFileJsonObject(mapper, imageFile));
                }
                if (page.getTextFileId() != null) {
                    IFile textFile = filesManager.getFile(page.getTextFileId());
                    pageNode.set("text", createFileJsonObject(mapper, textFile));
                }
            }
        }
    }
    
    @Override
    public ResponseEntity<String> generateSimpleResponse(Map<String, String> msgs, HttpStatus status) {
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

    private ObjectNode createFileJsonObject(ObjectMapper mapper, IFile file) {
        ObjectNode fileNode = mapper.createObjectNode();
        fileNode.put("filename", file.getFilename());
        fileNode.put("id", file.getId());
        fileNode.put("url", filesManager.getFileUrl(file));
        fileNode.put("path", filesManager.getRelativePathOfFile(file));
        fileNode.put("content-type", file.getContentType());
        fileNode.put("size", file.getSize());
        return fileNode;
    }

}
