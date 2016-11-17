package edu.asu.diging.gilesecosystem.web.service.handlers;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.files.IFileStorageManager;
import edu.asu.diging.gilesecosystem.web.service.IFileContentHelper;
import edu.asu.diging.gilesecosystem.web.service.IFileTypeHandler;
import edu.asu.diging.gilesecosystem.web.service.properties.IPropertiesManager;

public abstract class AbstractFileHandler implements IFileTypeHandler {
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    protected IPropertiesManager propertyManager;
    
    @Autowired
    protected IFileContentHelper fileContentHelper;
    
    protected byte[] getFileContentFromUrl(URL url) throws IOException {
        URLConnection con = url.openConnection();
        
        InputStream input = con.getInputStream();

        byte[] buffer = new byte[4096];
        
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        BufferedOutputStream output = new BufferedOutputStream(byteOutput);
       
        int n = -1;
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
        }
        input.close();
        output.flush();
        output.close();
        
        byteOutput.flush();
        byte[] bytes = byteOutput.toByteArray();
        byteOutput.close();
        return bytes;
    }
    
    @Override
    public byte[] getFileContent(IFile file) {
        try {
            return fileContentHelper.getFileContentFromUrl(new URL(file.getDownloadUrl()));
        } catch (IOException e) {
            logger.error("Could not download file.", e);
            return null;
        }
    }
    
    public String getFileUrl(IFile file) {
        String gilesUrl = propertyManager.getProperty(IPropertiesManager.GILES_URL).trim();
        String pdfEndpoint = propertyManager.getProperty(IPropertiesManager.GILES_FILE_ENDPOINT).trim();
        String contentSuffix = propertyManager.getProperty(IPropertiesManager.GILES_FILE_CONTENT_SUFFIX).trim();
        
        return gilesUrl + pdfEndpoint + file.getId() + contentSuffix;
    }
    
    protected abstract IFileStorageManager getStorageManager();
    
    protected void sendStorageRequest(String uploadId) {
        
        
    }
}

