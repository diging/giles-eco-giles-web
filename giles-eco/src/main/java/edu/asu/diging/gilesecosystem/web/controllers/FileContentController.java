package edu.asu.diging.gilesecosystem.web.controllers;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.web.aspects.access.annotations.AccountCheck;
import edu.asu.diging.gilesecosystem.web.aspects.access.annotations.FileAccessCheck;
import edu.asu.diging.gilesecosystem.web.domain.IFile;
import edu.asu.diging.gilesecosystem.web.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.service.core.ITransactionalFileService;

@Controller
public class FileContentController {
    
    @Autowired
    private IFilesManager filesManager;
    
    @Autowired
    private ITransactionalFileService fileService;

    @Autowired
    private ISystemMessageHandler messageHandler;

    @AccountCheck
    @FileAccessCheck
    @RequestMapping(value = "/files/{fileId}/content")
    public ResponseEntity<String> getFile(
            @PathVariable String fileId,
            HttpServletResponse response,
            HttpServletRequest request, Principal principal) {

        IFile file = fileService.getFileById(fileId);
        if (file == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        byte[] content = filesManager.getFileContent(file);
        response.setContentType(file.getContentType());
        
        response.setHeader("Content-disposition", "filename=\"" + file.getFilename() + "\""); 
        try {
            if (content != null) {
                response.setContentLength(content.length);
                response.getOutputStream().write(content);
                response.getOutputStream().close();
            }
        } catch (IOException e) {
            messageHandler.handleMessage("Could not write to output stream.", e, MessageType.ERROR);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
