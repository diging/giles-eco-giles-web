package edu.asu.diging.gilesecosystem.web.service.handlers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.web.domain.IFile;
import edu.asu.diging.gilesecosystem.web.files.IFilesDatabaseClient;
import edu.asu.diging.gilesecosystem.web.service.IFileTypeHandler;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;

@Service
public class ImageFileHandler extends AbstractFileHandler implements IFileTypeHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IFilesDatabaseClient filesDbClient;

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
            logger.error("Could not encode path.", e);
            return gilesUrl + gilesDigilibEndpoint + "?fn=" + relativePath;
        }
    }

}
