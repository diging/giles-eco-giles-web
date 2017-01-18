package edu.asu.diging.gilesecosystem.web.service.handlers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.FileType;

@PropertySource("classpath:/config.properties")
@Service
public class TextFileHandler extends AbstractFileHandler {

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
}
