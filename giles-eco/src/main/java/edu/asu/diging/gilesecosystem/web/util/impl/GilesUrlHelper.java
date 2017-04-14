package edu.asu.diging.gilesecosystem.web.util.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.util.IGilesUrlHelper;

@Service
public class GilesUrlHelper implements IGilesUrlHelper {
    
    @Autowired
    private IPropertiesManager propertiesManager;

    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.util.impl.IGilesUrlHelper#getUrl(java.lang.String)
     */
    @Override
    public String getUrl(String relativePath) {
        String gilesUrl = propertiesManager.getProperty(Properties.GILES_URL).trim();
        String separator = "";
        if (!gilesUrl.endsWith("/") && !relativePath.startsWith("/")) {
            separator = "/";
        }
        return gilesUrl + separator + relativePath;
    }
}
