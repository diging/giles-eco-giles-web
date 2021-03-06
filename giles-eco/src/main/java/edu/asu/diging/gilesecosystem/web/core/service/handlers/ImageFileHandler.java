package edu.asu.diging.gilesecosystem.web.core.service.handlers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.service.IFileTypeHandler;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;

@Service
public class ImageFileHandler extends AbstractFileHandler implements IFileTypeHandler {

    @Autowired
    private ISystemMessageHandler messageHandler;

    @Override
    public List<String> getHandledFileTypes() {
        List<String> fileTypes = new ArrayList<String>();
        fileTypes.add(MediaType.IMAGE_GIF_VALUE);
        fileTypes.add(MediaType.IMAGE_JPEG_VALUE);
        fileTypes.add(MediaType.IMAGE_PNG_VALUE);
        fileTypes.add(com.google.common.net.MediaType.TIFF.toString());
        return fileTypes;
    }
    
    @Override
    public FileType getHandledFileType() {
        return FileType.IMAGE;
    }

    @Override
    public String getFileUrl(IFile file) {
        String relativePath = file.getFilepath();
        if (relativePath == null) {
            return null;
        }
        String gilesUrl = propertyManager.getProperty(Properties.GILES_URL);
        String gilesDigilibEndpoint = propertyManager.getProperty(Properties.GILES_DIGILIB_ENDPOINT);
        
        try {
            return gilesUrl + gilesDigilibEndpoint + "?fn=" + URLEncoder.encode(relativePath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            messageHandler.handleMessage("Could not encode path.", e, MessageType.ERROR);
            return gilesUrl + gilesDigilibEndpoint + "?fn=" + relativePath;
        }
    }

}
