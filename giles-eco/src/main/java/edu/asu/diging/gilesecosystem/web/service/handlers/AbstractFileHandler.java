package edu.asu.diging.gilesecosystem.web.service.handlers;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.beans.factory.annotation.Autowired;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.domain.IFile;
import edu.asu.diging.gilesecosystem.web.service.IFileContentHelper;
import edu.asu.diging.gilesecosystem.web.service.IFileTypeHandler;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;

public abstract class AbstractFileHandler implements IFileTypeHandler {
    
    @Autowired
    protected IPropertiesManager propertyManager;
    
    @Autowired
    protected IFileContentHelper fileContentHelper;

    @Autowired
    private ISystemMessageHandler messageHandler;
    
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
            messageHandler.handleMessage("Could not download file.", e, MessageType.ERROR);
            return null;
        }
    }
    
    @Override
    public String getFileUrl(IFile file) {
        String gilesUrl = propertyManager.getProperty(Properties.GILES_URL).trim();
        String pdfEndpoint = propertyManager.getProperty(Properties.GILES_FILE_ENDPOINT).trim();
        String contentSuffix = propertyManager.getProperty(Properties.GILES_FILE_CONTENT_SUFFIX).trim();
        
        return gilesUrl + pdfEndpoint + file.getId() + contentSuffix;
    }
}

