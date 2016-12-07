package edu.asu.diging.gilesecosystem.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.service.IMetadataUrlService;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;

/**
 * Class that generate callback urls to Jars (or potentially any other
 * metadata repository.
 * The following properties can be used:
 * <ul>
 *  <li>{giles} - Giles url</li>
 *  <li>{fileId} - Id of a provided file</li>
 * </ul>
 * @author jdamerow
 *
 */
@PropertySource("classpath:/config.properties")
@Service
public class MetadataUrlService implements IMetadataUrlService {
    
    @Autowired
    private IPropertiesManager propertyManager;
    
    /* (non-Javadoc)
     * @see edu.asu.giles.service.impl.IMetadataUrlService#getUploadCallback()
     */
    @Override
    public String getUploadCallback() {
        return propertyManager.getProperty(Properties.JARS_URL);
    }
    
    /* (non-Javadoc)
     * @see edu.asu.giles.service.impl.IMetadataUrlService#getFileLink(edu.asu.giles.core.IFile)
     */
    @Override
    public String getFileLink(IFile file) {
        String jarsUrl = propertyManager.getProperty(Properties.JARS_URL);
        String jarsFileUrl = propertyManager.getProperty(Properties.JARS_FILE_URL);
        String gilesUrl = propertyManager.getProperty(Properties.GILES_URL);
        
        return jarsUrl + jarsFileUrl.replace("{giles}", gilesUrl).replace("{fileId}", file.getId());
    }
    
    @Override
    public String getDocumentLink(IDocument doc) {
        String jarsUrl = propertyManager.getProperty(Properties.JARS_URL);
        String metadataUrl = propertyManager.getProperty(Properties.METADATA_SERVICE_DOC_ENDPOINT);
        String gilesUrl = propertyManager.getProperty(Properties.GILES_URL);
        
        return jarsUrl + metadataUrl.replace("{giles}", gilesUrl).replace("{docId}", doc.getId());
    }
}
