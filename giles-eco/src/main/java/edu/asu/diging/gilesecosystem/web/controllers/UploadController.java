package edu.asu.diging.gilesecosystem.web.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.gilesecosystem.web.aspects.access.annotations.AccountCheck;
import edu.asu.diging.gilesecosystem.web.controllers.util.StatusHelper;
import edu.asu.diging.gilesecosystem.web.core.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.DocumentType;
import edu.asu.diging.gilesecosystem.web.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.files.impl.StorageStatus;
import edu.asu.diging.gilesecosystem.web.users.User;
import edu.asu.diging.gilesecosystem.web.util.FileUploadHelper;

@Controller
public class UploadController {

    private static Logger logger = LoggerFactory
            .getLogger(UploadController.class);

    @Autowired
    private IFilesManager filesManager;
    
    @Autowired
    private FileUploadHelper uploadHelper;
    
    @Autowired
    private StatusHelper statusHelper;


    @AccountCheck
    @RequestMapping(value = "/files/upload", method = RequestMethod.GET)
    public String showUploadPage(Principal principal, Model model) {
        return "files/upload";
    }

    @AccountCheck
    @RequestMapping(value = "/files/upload", method = RequestMethod.POST)
    public ResponseEntity<String> uploadFiles(Principal principal,
            @RequestParam("file") MultipartFile[] files,
            @RequestParam("access") String access, Locale locale) {

        User user = null;
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            user = (User) ((UsernamePasswordAuthenticationToken) principal)
                    .getPrincipal();
        }

        
        DocumentAccess docAccess = DocumentAccess.valueOf(access);
        if (docAccess == null) {
            return new ResponseEntity<String>("Access type: " + access
                    + " does not exist.", HttpStatus.BAD_REQUEST);
        }
        
        List<StorageStatus> statuses = uploadHelper.processUpload(docAccess, DocumentType.SINGLE_PAGE, files, null, user);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        ArrayNode filesNode = root.putArray("files");

        for (StorageStatus status : statuses) {
            ObjectNode fileNode = mapper.createObjectNode();
            fileNode.put("name", status.getFile().getFilename());
            fileNode.put("uploadId", status.getFile().getUploadId());
            fileNode.put("status", statusHelper.getLabelText(status.getStatus(), locale));
            filesNode.add(fileNode);
        }

        return new ResponseEntity<String>(root.toString(), HttpStatus.OK);
    }
}