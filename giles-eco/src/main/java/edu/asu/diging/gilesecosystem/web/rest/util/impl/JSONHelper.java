package edu.asu.diging.gilesecosystem.web.rest.util.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.web.domain.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.domain.IDocument;
import edu.asu.diging.gilesecosystem.web.domain.IFile;
import edu.asu.diging.gilesecosystem.web.domain.IPage;
import edu.asu.diging.gilesecosystem.web.domain.ITask;
import edu.asu.diging.gilesecosystem.web.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.rest.util.IJSONHelper;
import edu.asu.diging.gilesecosystem.web.service.core.ITransactionalFileService;

@Component
public class JSONHelper implements IJSONHelper {
    
    @Autowired
    private IFilesManager filesManager;
    
    @Autowired
    private ITransactionalFileService fileService;

    @Autowired
    private ISystemMessageHandler messageHandler;

    /* (non-Javadoc)
     * @see edu.asu.giles.rest.util.IJSONHelper#createDocumentJson(edu.asu.giles.core.IDocument, com.fasterxml.jackson.databind.ObjectMapper, com.fasterxml.jackson.databind.node.ObjectNode)
     */
    @Override
    public void createDocumentJson(IDocument doc, ObjectMapper mapper, ObjectNode docNode) {
        
        Map<String, List<ITask>> tasksByDerivedFrom = new HashMap<>();
        doc.getTasks().forEach(t -> {
            if (tasksByDerivedFrom.get(t.getFileId()) == null) {
                tasksByDerivedFrom.put(t.getFileId(), new ArrayList<>());
            }
            tasksByDerivedFrom.get(t.getFileId()).add(t);
        });
        
        IFile uploadedFile = fileService.getFileById(doc.getUploadedFileId());
        docNode.put("documentId", doc.getId());
        docNode.put("documentStatus", uploadedFile.getProcessingStatus().toString());
        docNode.put("uploadId", doc.getUploadId());
        docNode.put("uploadedDate", doc.getCreatedDate());
        docNode.put("access", (doc.getAccess() != null ? doc.getAccess()
                .toString() : DocumentAccess.PRIVATE.toString()));
        
        if (uploadedFile != null) {
            ObjectNode uploadedFileNode = createFileJsonObject(mapper, uploadedFile);
            docNode.set("uploadedFile", uploadedFileNode);
        }
        
        IFile extractedTextFile = null;
        if (doc.getExtractedTextFileId() != null) {
            extractedTextFile = fileService.getFileById(doc.getExtractedTextFileId());
            docNode.set("extractedText", createFileJsonObject(mapper, extractedTextFile));
        }
        
        ArrayNode additionalFiles = docNode.putArray("additionalFiles");
        addFiletoArray(mapper, tasksByDerivedFrom, uploadedFile, additionalFiles);
        addFiletoArray(mapper, tasksByDerivedFrom, extractedTextFile,
                    additionalFiles);
        
        if (!doc.getPages().isEmpty()) {
            ArrayNode pagesArray = docNode.putArray("pages");
            for (IPage page : doc.getPages()) {
                ObjectNode pageNode = pagesArray.addObject();
                pageNode.put("nr", page.getPageNr());
                ArrayNode additionalPageFiles = mapper.createArrayNode();
                if (page.getImageFileId() != null) {
                    IFile imageFile = fileService.getFileById(page.getImageFileId());
                    pageNode.set("image", createFileJsonObject(mapper, imageFile));
                    addFiletoArray(mapper, tasksByDerivedFrom, imageFile, additionalPageFiles);
                }
                if (page.getTextFileId() != null) {
                    IFile textFile = fileService.getFileById(page.getTextFileId());
                    pageNode.set("text", createFileJsonObject(mapper, textFile));
                    addFiletoArray(mapper, tasksByDerivedFrom, textFile, additionalPageFiles);
                }
                if (page.getOcrFileId() != null) {
                    IFile ocrFile = fileService.getFileById(page.getOcrFileId());
                    pageNode.set("ocr", createFileJsonObject(mapper, ocrFile));
                    addFiletoArray(mapper, tasksByDerivedFrom, ocrFile, additionalPageFiles);
                }
                pageNode.set("additionalFiles", additionalPageFiles);
            }
        }
    }

    public void addFiletoArray(ObjectMapper mapper,
            Map<String, List<ITask>> tasksByDerivedFrom, IFile derivedFrom,
            ArrayNode additionalFiles) {
        if (tasksByDerivedFrom.get(derivedFrom.getId()) == null || derivedFrom == null) {
            return;
        }
        tasksByDerivedFrom.get(derivedFrom.getId()).forEach(t -> {
            IFile file = fileService.getFileById(t.getResultFileId());
            if (file != null) {
                additionalFiles.add(createFileJsonObject(mapper, file));
            }
        });
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
            messageHandler.handleMessage("Could not write json.", e, MessageType.ERROR);
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
        fileNode.put("path", file.getFilepath());
        fileNode.put("content-type", file.getContentType());
        fileNode.put("size", file.getSize());
        return fileNode;
    }

}
