package edu.asu.giles.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import edu.asu.giles.service.properties.IPropertiesManager;

@Service
public class DigilibConnector {

    private Logger logger = LoggerFactory.getLogger(DigilibConnector.class);

    @Autowired
    private IPropertiesManager propertyManager;

    public Map<String, List<String>> getDigilibImage(String parameters,
            HttpServletResponse response) throws IOException {
        String digilibUrl = propertyManager.getProperty(IPropertiesManager.DIGILIB_SCALER_URL);
        
        URL url = new URL(digilibUrl + "?" + parameters);
        logger.debug("Getting: " + url.toString());

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        
        con = (HttpURLConnection) url.openConnection();
        
        logger.debug("Set content type for response: " + con.getHeaderField(HttpHeaders.CONTENT_TYPE));
        response.setContentType(con.getHeaderField(HttpHeaders.CONTENT_TYPE));
        
        InputStream input = con.getInputStream();

        byte[] buffer = new byte[4096];
        int n = -1;
        
        OutputStream output = response.getOutputStream();

        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
        }
        input.close();

        return con.getHeaderFields();
    }
}
