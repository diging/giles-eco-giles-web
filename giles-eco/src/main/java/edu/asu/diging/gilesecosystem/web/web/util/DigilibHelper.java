package edu.asu.diging.gilesecosystem.web.web.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.web.core.util.DigilibConnector;

@Service
public class DigilibHelper {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DigilibConnector digilibConnector;
    
    @Autowired
    private ISystemMessageHandler messageHandler;

    public ResponseEntity<String> getDigilibResponse(Map<String, String[]> parameters, 
            HttpServletResponse response) throws UnsupportedEncodingException {
        MultiValueMap<String, String> headers = new HttpHeaders();        
        
        StringBuffer parameterBuffer = new StringBuffer();
        for (String key : parameters.keySet()) {
            for (String value : parameters.get(key)) {
                parameterBuffer.append(key);
                parameterBuffer.append("=");

                parameterBuffer.append(URLEncoder.encode(value, "UTF-8"));
                parameterBuffer.append("&");
            }
        }

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
            messageHandler.handleMessage(e.getMessage(), e, MessageType.ERROR);
            return new ResponseEntity<String>(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            messageHandler.handleMessage(e.getMessage(), e, MessageType.ERROR);
            return new ResponseEntity<String>(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.debug("Setting headers: " + headers);
        logger.debug("Response headers: " + response.getContentType());
        try {
            response.getOutputStream().close();
        } catch (IOException e) {
            messageHandler.handleMessage(e.getMessage(), e, MessageType.ERROR);
            return new ResponseEntity<String>(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }
}
