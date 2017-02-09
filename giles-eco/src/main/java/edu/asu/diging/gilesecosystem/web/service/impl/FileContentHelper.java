package edu.asu.diging.gilesecosystem.web.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.files.IFileStorageManager;
import edu.asu.diging.gilesecosystem.web.service.IFileContentHelper;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;

@Component
public class FileContentHelper implements IFileContentHelper {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IPropertiesManager propertiesManager;

    /* (non-Javadoc)
     * @see edu.asu.giles.service.impl.IFileSystemHelper#getFileContent(edu.asu.giles.core.IFile, edu.asu.giles.files.IFileStorageManager)
     */
    @Override
    public byte[] getFileContent(IFile file, IFileStorageManager storageManager) {
        String folderPath = storageManager.getAndCreateStoragePath(file.getUsernameForStorage(), file.getUploadId(), file.getDocumentId());
        File fileObject = new File(folderPath + File.separator + file.getFilename());
        try {
            return getFileContentFromUrl(fileObject.toURI().toURL());
        } catch (IOException e) {
            logger.error("Could not read file.", e);
            return null;
        } catch (RestClientResponseException e) {
            logger.error("REST Exception", e);
            return null;
        }
    }
    
    /* (non-Javadoc)
     * @see edu.asu.giles.service.impl.IFileSystemHelper#getFileContentFromUrl(java.net.URL)
     */
    @Override
    public byte[] getFileContentFromUrl(URL url) throws IOException, RestClientResponseException {
        
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());    
        
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
        headers.set("Authorization", "token " + propertiesManager.getProperty(Properties.NEPOMUK_ACCESS_TOKEN));
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(url.toString(), HttpMethod.GET, entity, byte[].class);
        
        if(response.getStatusCode().equals(HttpStatus.OK)) {    
            return response.getBody();
        }
        
        return null;
    }
}
