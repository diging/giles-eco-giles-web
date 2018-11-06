package edu.asu.diging.gilesecosystem.web.nepomuk.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.domain.IFile;
import edu.asu.diging.gilesecosystem.web.exceptions.NoNepomukFoundException;
import edu.asu.diging.gilesecosystem.web.nepomuk.INepomukUrlService;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.zookeeper.INepomukServiceDiscoverer;

/**
 * Helper class for Nepomuk URL.
 * 
 * @author jdamerow
 *
 */
@Service
public class NepomukUrlService implements INepomukUrlService {

    @Autowired
    protected INepomukServiceDiscoverer nepomukDiscoverer;

    @Autowired
    protected IPropertiesManager propertyManager;

    @Autowired
    private ISystemMessageHandler messageHandler;

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.gilesecosystem.web.nepomuk.impl.INepomukUrlService#
     * getFileDownloadPath(edu.asu.diging.gilesecosystem.web.domain.IFile)
     */
    @Override
    public String getFileDownloadPath(IFile file) {
        String nepomukUrl;
        String downloadPath = "";
        try {
            nepomukUrl = nepomukDiscoverer.getRandomNepomukInstance();
        } catch (NoNepomukFoundException e) {
            messageHandler.handleMessage("Could not download file. Nepomuk could not be reached.", e,
                    MessageType.ERROR);
            return null;
        }
        downloadPath = nepomukUrl
                + propertyManager.getProperty(Properties.NEPOMUK_FILES_ENDPOINT).replace("{0}", file.getStorageId());

        return downloadPath;
    }
}
