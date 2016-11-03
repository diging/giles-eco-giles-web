package edu.asu.giles.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.giles.aspects.access.annotations.AccountCheck;
import edu.asu.giles.aspects.access.annotations.FileAccessCheck;
import edu.asu.giles.core.IFile;
import edu.asu.giles.exceptions.GilesMappingException;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.service.IGilesMappingService;
import edu.asu.giles.service.IMetadataUrlService;
import edu.asu.giles.service.impl.GilesMappingService;
import edu.asu.giles.util.DigilibConnector;
import edu.asu.giles.web.pages.FilePageBean;

@Controller
public class ViewImageController {

    private Logger logger = LoggerFactory.getLogger(ViewImageController.class);

    @Autowired
    private DigilibConnector digilibConnector;

    @Autowired
    private IFilesManager filesManager;
    
    @Autowired
    private IMetadataUrlService metadataService;
    
    @AccountCheck
    @FileAccessCheck
    @RequestMapping(value = "/files/{fileId}/img")
    public ResponseEntity<String> viewImage(
            @PathVariable("fileId") String fileId,
            HttpServletResponse response, HttpServletRequest request) {

        Map<String, String[]> parameters = request.getParameterMap();
        // remove accessToken since Github doesn't care about

        StringBuffer parameterBuffer = new StringBuffer();
        for (String key : parameters.keySet()) {
            if (key.equals("accessToken")) {
                continue;
            }
            for (String value : parameters.get(key)) {
                parameterBuffer.append(key);
                parameterBuffer.append("=");
                parameterBuffer.append(value);
                parameterBuffer.append("&");
            }
        }

        IFile file = filesManager.getFile(fileId);
        parameterBuffer.append("fn=");
        try {
            parameterBuffer.append(URLEncoder.encode(filesManager.getRelativePathOfFile(file), "UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            logger.error("Could not encode path.", e1);
            parameterBuffer.append(filesManager.getRelativePathOfFile(file));
        }

        try {
            digilibConnector.getDigilibImage(parameterBuffer.toString(),
                    response);
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @AccountCheck
    @FileAccessCheck
    @RequestMapping(value = "/files/{fileId}")
    public String showImagePage(Model model,
            @PathVariable("fileId") String fileId) throws GilesMappingException {
        IFile file = filesManager.getFile(fileId);
        IGilesMappingService<IFile, FilePageBean> fileMappingService = new GilesMappingService<>();
        
        FilePageBean fileBean = fileMappingService.convertToT2(file, new FilePageBean());
        fileBean.setMetadataLink(metadataService.getFileLink(file));
        model.addAttribute("file", fileBean);
        
        if (file.getDerivedFrom() != null) {
            model.addAttribute("derivedFrom", filesManager.getFile(file.getDerivedFrom()));
        }

        return "files/file";
    }
}
