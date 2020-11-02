package edu.asu.diging.gilesecosystem.web.api.v1;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.web.core.aspects.access.annotations.ImageAccessCheck;
import edu.asu.diging.gilesecosystem.web.core.aspects.access.annotations.InjectImagePath;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.core.users.User;
import edu.asu.diging.gilesecosystem.web.core.util.DigilibConnector;
import edu.asu.diging.gilesecosystem.web.web.util.DigilibHelper;

@Controller
public class DigilibPassthroughController {

    @Autowired
    private ITransactionalFileService filesService;

    @Autowired
    private DigilibHelper digilibHelper;
    
    @ImageAccessCheck
    @RequestMapping(value = "/rest/digilib")
    public ResponseEntity<String> passthroughToDigilib(
            @RequestParam(defaultValue = "") String accessToken,
            HttpServletRequest request, 
            HttpServletResponse response,
            User user,
            @RequestParam(defaultValue = "") @InjectImagePath String fn)
            throws UnsupportedEncodingException {
        
        IFile file = filesService.getFileByPath(fn);
        
        if (file == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        if (file.getAccess() != DocumentAccess.PUBLIC
                && !file.getUsername().equals(user.getUsername())) {
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }

        Map<String, String[]> parameters = new HashMap<>(request.getParameterMap());
        parameters.remove("accessToken");
        
        return digilibHelper.getDigilibResponse(parameters, response);
    }
    
    @RequestMapping(value = "/rest/digilib/public")
    public ResponseEntity<String> passthroughToDigilibPublic(
            HttpServletRequest request, HttpServletResponse response,
            User user)
            throws UnsupportedEncodingException {
        Map<String, String[]> parameters = new HashMap<>(request.getParameterMap());
        parameters.remove("accessToken");
        
        String[] fnParameter = parameters.get("fn");
        String fn = null;
        // get the first entry for fn
        if (fnParameter != null && fnParameter.length > 0) {
            fn = fnParameter[0];
        }
        
        if (fn == null) {
            return new ResponseEntity<String>("{ 'error': 'Please provide fn parameter' }", HttpStatus.BAD_REQUEST);
        }
        
        IFile file = filesService.getFileByPath(fn);

        if (file == null) {
            fn = fn.startsWith(File.separator) ? fn.substring(1) : File.separator + fn;
            file = filesService.getFileByPath(fn);
        }

        if (file == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        if (file.getAccess() != DocumentAccess.PUBLIC) {
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }
        
        return digilibHelper.getDigilibResponse(parameters, response);
    }
}
