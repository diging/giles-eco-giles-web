package edu.asu.giles.rest;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
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

import edu.asu.giles.aspects.access.annotations.TokenCheck;
import edu.asu.giles.core.DocumentAccess;
import edu.asu.giles.core.IFile;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.users.User;
import edu.asu.giles.util.DigilibConnector;

@Controller
public class DigilibPassthroughController {

    private static Logger logger = LoggerFactory
            .getLogger(DigilibPassthroughController.class);

    @Autowired
    private IFilesManager filesManager;

    @Autowired
    private DigilibConnector digilibConnector;

    @TokenCheck
    @RequestMapping(value = "/rest/digilib")
    public ResponseEntity<String> passthroughToDigilib(
            @RequestParam(defaultValue = "") String accessToken,
            HttpServletRequest request, 
            HttpServletResponse response,
            User user)
            throws UnsupportedEncodingException {

        Map<String, String[]> parameters = request.getParameterMap();
        // remove accessToken since Github doesn't care about

        String fn = null;
        StringBuffer parameterBuffer = new StringBuffer();
        for (String key : parameters.keySet()) {
            if (key.equals("accessToken")) {
                continue;
            }
            for (String value : parameters.get(key)) {
                parameterBuffer.append(key);
                parameterBuffer.append("=");

                parameterBuffer.append(URLEncoder.encode(value, "UTF-8"));
                parameterBuffer.append("&");

                if (key.equals("fn")) {
                    fn = value;
                }
            }
        }

        if (fn.startsWith(File.separator)) {
            fn = fn.substring(1);
        }
        IFile file = filesManager.getFileByPath(fn);
        if (file == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        if (file.getAccess() != DocumentAccess.PUBLIC
                && !file.getUsername().equals(user.getUsername())) {
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }

        MultiValueMap<String, String> headers = new HttpHeaders();
        try {
            Map<String, List<String>> digilibHeaders = digilibConnector
                    .getDigilibImage(parameterBuffer.toString(),
                            response);
            for (String key : digilibHeaders.keySet()) {
                if (key != null) {
                    headers.put(key, digilibHeaders.get(key));
                }
            }
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.debug("Setting headers: " + headers);
        logger.debug("Response headers: " + response.getContentType());
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/rest/digilib/public")
    public ResponseEntity<String> passthroughToDigilibPublic(
            HttpServletRequest request, HttpServletResponse response,
            User user)
            throws UnsupportedEncodingException {
        Map<String, String[]> parameters = request.getParameterMap();
        // remove accessToken since Github doesn't care about

        String fn = null;
        StringBuffer parameterBuffer = new StringBuffer();
        for (String key : parameters.keySet()) {
            for (String value : parameters.get(key)) {
                parameterBuffer.append(key);
                parameterBuffer.append("=");

                parameterBuffer.append(URLEncoder.encode(value, "UTF-8"));
                parameterBuffer.append("&");

                if (key.equals("fn")) {
                    fn = value;
                }
            }
        }
        
        if (fn.startsWith(File.separator)) {
            fn = fn.substring(1);
        }
        
        IFile file = filesManager.getFileByPath(fn);
        if (file == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        if (file.getAccess() != DocumentAccess.PUBLIC) {
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }
        
        MultiValueMap<String, String> headers = new HttpHeaders();
        try {
            Map<String, List<String>> digilibHeaders = digilibConnector
                    .getDigilibImage(parameterBuffer.toString(),
                            response);
            for (String key : digilibHeaders.keySet()) {
                if (key != null) {
                    headers.put(key, digilibHeaders.get(key));
                }
            }
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }
}
