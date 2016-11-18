package edu.asu.diging.gilesecosystem.web.service.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.files.IFileStorageManager;

@PropertySource("classpath:/config.properties")
@Service
public class TextFileHandler extends AbstractFileHandler {

    @Autowired
    @Qualifier("textStorageManager")
    private IFileStorageManager textStorageManager;
    
    @Override
    public List<String> getHandledFileTypes() {
        List<String> types = new ArrayList<String>();
        types.add(MediaType.TEXT_PLAIN_VALUE);
        return types;
    }
    
    @Override
    public FileType getHandledFileType() {
        return FileType.TEXT;
    }

    @Override
    public String getRelativePathOfFile(IFile file) {
        String directory = textStorageManager.getFileFolderPath(file.getUsername(), file.getUploadId(), file.getDocumentId());
        return directory + File.separator + file.getFilename();
    }

    @Override
    protected IFileStorageManager getStorageManager() {
        return textStorageManager;
    }

}
