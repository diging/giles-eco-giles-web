package edu.asu.diging.gilesecosystem.web.api.processing;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.web.core.aspects.access.annotations.AppTokenOnlyCheck;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.core.service.processing.IDistributedStorageManager;
import edu.asu.diging.gilesecosystem.web.core.users.User;

@Controller
public class TemporaryFilesController {
    
    public final static String FILE_ID_PLACEHOLDER = "{fileId}";
    public final static String GET_CONTENT_URL = "/rest/processing/files/" + FILE_ID_PLACEHOLDER + "/content";
    
    @Autowired
    private ITransactionalFileService filesService;
    
    @Autowired
    private IDistributedStorageManager storageManager;

    @Autowired
    private ISystemMessageHandler messageHandler;

    @AppTokenOnlyCheck
    @RequestMapping(value = GET_CONTENT_URL, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<String> getFile(
            @PathVariable String fileId,
            @RequestParam(defaultValue="") String accessToken, 
            User user,
            HttpServletResponse response,
            HttpServletRequest request) {

        IFile file = filesService.getFileById(fileId);
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
            messageHandler.handleMessage("Could not write to output stream.", e, MessageType.ERROR);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
