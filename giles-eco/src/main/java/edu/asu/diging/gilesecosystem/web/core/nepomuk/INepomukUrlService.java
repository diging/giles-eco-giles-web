package edu.asu.diging.gilesecosystem.web.core.nepomuk;

import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.zookeeper.INepomukServiceDiscoverer;

public interface INepomukUrlService {

    /**
     * Returns a download path for a file. This method requests a Nepomuk
     * URL from the {@link INepomukServiceDiscoverer} and then builds a download path 
     * for the provided file.
     * 
     * @param file The file for which a download path is requested.
     * @return Path to download file from Nepomuk.
     */
    String getFileDownloadPath(IFile file);

}