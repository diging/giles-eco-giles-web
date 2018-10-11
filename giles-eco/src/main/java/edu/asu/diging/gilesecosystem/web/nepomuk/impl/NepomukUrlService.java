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
	public String getFileDownloadPath(IFile file) throws NoNepomukFoundException {
		String nepomukUrl;
		String downloadPath="";
		try {
			nepomukUrl = nepomukDiscoverer.getRandomNepomukInstance();
			System.out.println("nepomukUrl**"+nepomukUrl);
		} catch (NoNepomukFoundException e) {
			messageHandler.handleMessage("Could not download file. Nepomuk could not be reached.", e,
					MessageType.ERROR);
			System.out.println("nepomukUrl**@@");
			throw new NoNepomukFoundException();
		} 
		if(nepomukUrl == null) {
			throw new NoNepomukFoundException();
		}
		
		try {
			downloadPath = nepomukUrl + propertyManager.getProperty(Properties.NEPOMUK_FILES_ENDPOINT).replace("{0}",
					file.getStorageId());
		} catch (Exception e) {
			messageHandler.handleMessage("Nepomuk Download URL could not be validated.", e, MessageType.ERROR);
		}
		
		return downloadPath;
	}
}
