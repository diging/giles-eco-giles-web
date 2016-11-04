package edu.asu.diging.gilesecosystem.web.rest.processing;

import java.io.IOException;

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

import edu.asu.diging.gilesecosystem.web.aspects.access.annotations.AppTokenOnlyCheck;
import edu.asu.diging.gilesecosystem.web.core.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.service.processing.IDistributedStorageManager;
import edu.asu.diging.gilesecosystem.web.users.User;

@Controller
public class TemporaryFilesController {
    
    public final static String FILE_ID_PLACEHOLDER = "{fileId}";
    public final static String GET_CONTENT_URL = "/rest/processing/files/" + FILE_ID_PLACEHOLDER + "/content";
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IFilesManager filesManager;
    
    @Autowired
    private IDistributedStorageManager storageManager;

    @AppTokenOnlyCheck
    @RequestMapping(value = GET_CONTENT_URL, produces = "application/json;charset=UTF-8")
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

        byte[] content = storageManager.getFileContent(file);
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
