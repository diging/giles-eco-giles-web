package edu.asu.giles.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.service.IMetadataUrlService;
import edu.asu.giles.service.properties.IPropertiesManager;

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
        return propertyManager.getProperty(IPropertiesManager.JARS_URL);
    }
    
    /* (non-Javadoc)
     * @see edu.asu.giles.service.impl.IMetadataUrlService#getFileLink(edu.asu.giles.core.IFile)
     */
    @Override
    public String getFileLink(IFile file) {
        String jarsUrl = propertyManager.getProperty(IPropertiesManager.JARS_URL);
        String jarsFileUrl = propertyManager.getProperty(IPropertiesManager.JARS_FILE_URL);
        String gilesUrl = propertyManager.getProperty(IPropertiesManager.GILES_URL);
        
        return jarsUrl + jarsFileUrl.replace("{giles}", gilesUrl).replace("{fileId}", file.getId());
    }
    
    @Override
    public String getDocumentLink(IDocument doc) {
        String jarsUrl = propertyManager.getProperty(IPropertiesManager.JARS_URL);
        String metadataUrl = propertyManager.getProperty(IPropertiesManager.METADATA_SERVICE_DOC_ENDPOINT);
        String gilesUrl = propertyManager.getProperty(IPropertiesManager.GILES_URL);
        
        return jarsUrl + metadataUrl.replace("{giles}", gilesUrl).replace("{docId}", doc.getId());
    }
}
