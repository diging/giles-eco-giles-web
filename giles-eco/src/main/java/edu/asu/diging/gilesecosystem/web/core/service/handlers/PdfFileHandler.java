package edu.asu.diging.gilesecosystem.web.core.service.handlers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.web.core.service.IFileTypeHandler;

@PropertySource("classpath:/config.properties")
@Service
public class PdfFileHandler extends AbstractFileHandler implements IFileTypeHandler {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<String> getHandledFileTypes() {
        List<String> types = new ArrayList<String>();
        types.add(MediaType.APPLICATION_PDF_VALUE);
        return types;
    }
    
    @Override
    public FileType getHandledFileType() {
        return FileType.PDF;
    }
}
