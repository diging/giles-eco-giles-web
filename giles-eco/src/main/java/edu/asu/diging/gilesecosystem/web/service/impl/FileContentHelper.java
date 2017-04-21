package edu.asu.diging.gilesecosystem.web.service.impl;

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
import org.springframework.stereotype.Component;

import edu.asu.diging.gilesecosystem.septemberutil.service.impl.SystemMessageHandler;
import edu.asu.diging.gilesecosystem.web.domain.IFile;
import edu.asu.diging.gilesecosystem.web.files.IFileStorageManager;
import edu.asu.diging.gilesecosystem.web.service.IFileContentHelper;

@Component
public class FileContentHelper implements IFileContentHelper {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SystemMessageHandler messageHandler;

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
            messageHandler.handleError("Could not read file.", e);
            return null;
        }
    }
    
    /* (non-Javadoc)
     * @see edu.asu.giles.service.impl.IFileSystemHelper#getFileContentFromUrl(java.net.URL)
     */
    @Override
    public byte[] getFileContentFromUrl(URL url) throws IOException {
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
}
