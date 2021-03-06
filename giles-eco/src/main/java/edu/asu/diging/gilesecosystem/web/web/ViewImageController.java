package edu.asu.diging.gilesecosystem.web.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.web.core.aspects.access.annotations.AccountCheck;
import edu.asu.diging.gilesecosystem.web.core.aspects.access.annotations.FileAccessCheck;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesMappingException;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.service.IGilesMappingService;
import edu.asu.diging.gilesecosystem.web.core.service.IMetadataUrlService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.core.service.impl.GilesMappingService;
import edu.asu.diging.gilesecosystem.web.core.util.DigilibConnector;
import edu.asu.diging.gilesecosystem.web.web.pages.FilePageBean;

@Controller
public class ViewImageController {

    @Autowired
    private DigilibConnector digilibConnector;

    @Autowired
    private ITransactionalFileService filesService;
    
    @Autowired
    private IMetadataUrlService metadataService;

    @Autowired
    private ISystemMessageHandler messageHandler;
    
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

        IFile file = filesService.getFileById(fileId);
        parameterBuffer.append("fn=");
        try {
            parameterBuffer.append(URLEncoder.encode(file.getFilepath(), "UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            messageHandler.handleMessage("Could not encode path.", e1, MessageType.ERROR);
            parameterBuffer.append(file.getFilepath());
        }

        try {
            digilibConnector.getDigilibImage(parameterBuffer.toString(),
                    response);
        } catch (MalformedURLException e) {
            messageHandler.handleMessage(e.getMessage(), e, MessageType.ERROR);
            return new ResponseEntity<String>(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            messageHandler.handleMessage(e.getMessage(), e, MessageType.ERROR);
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
        IFile file = filesService.getFileById(fileId);
        IGilesMappingService<IFile, FilePageBean> fileMappingService = new GilesMappingService<>();
        
        FilePageBean fileBean = fileMappingService.convertToT2(file, new FilePageBean());
        fileBean.setMetadataLink(metadataService.getFileLink(file));
        model.addAttribute("file", fileBean);
        
        if (file.getDerivedFrom() != null) {
            model.addAttribute("derivedFrom", filesService.getFileById(file.getDerivedFrom()));
        }

        return "files/file";
    }
}
