package edu.asu.giles.web;

import java.io.IOException;
import java.security.Principal;

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

import edu.asu.giles.aspects.access.annotations.AccountCheck;
import edu.asu.giles.aspects.access.annotations.FileAccessCheck;
import edu.asu.giles.core.IFile;
import edu.asu.giles.files.IFilesManager;

@Controller
public class FileContentController {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IFilesManager filesManager;

    @AccountCheck
    @FileAccessCheck
    @RequestMapping(value = "/files/{fileId}/content")
    public ResponseEntity<String> getFile(
            @PathVariable String fileId,
            HttpServletResponse response,
            HttpServletRequest request, Principal principal) {

        IFile file = filesManager.getFile(fileId);
        if (file == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
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
