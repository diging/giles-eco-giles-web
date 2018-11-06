package edu.asu.diging.gilesecosystem.web.nepomuk.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.domain.IFile;
import edu.asu.diging.gilesecosystem.web.domain.impl.File;
import edu.asu.diging.gilesecosystem.web.exceptions.NoNepomukFoundException;
import edu.asu.diging.gilesecosystem.web.nepomuk.INepomukUrlService;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.zookeeper.INepomukServiceDiscoverer;

public class NepomukUrlServiceTest {

    @Mock
    protected INepomukServiceDiscoverer nepomukDiscoverer;
    
    @Mock
    protected IPropertiesManager propertyManager;
    
    @Mock
    private ISystemMessageHandler messageHandler;
    
    @InjectMocks
    private INepomukUrlService serviceToTest;
    
    private final String NEPOMUK_URL = "http://nepomuk/";
    private final String FILES_ENDPOINT = "/rest/files/";
    private final String ENDPOINT_PLACEHOLDER = "{0}";
    
    @Before
    public void setUp() throws NoNepomukFoundException {
        serviceToTest = new NepomukUrlService();
        MockitoAnnotations.initMocks(this);
        
        Mockito.when(nepomukDiscoverer.getRandomNepomukInstance()).thenReturn(NEPOMUK_URL);
        Mockito.when(propertyManager.getProperty(Properties.NEPOMUK_FILES_ENDPOINT)).thenReturn(FILES_ENDPOINT + ENDPOINT_PLACEHOLDER);
        
    }
    
    @Test
    public void test_getFileDownloadPath_success() {
        IFile file = new File();
        String ID = "ID";
        file.setStorageId(ID);
        
        String url=null;
        try {
            url = serviceToTest.getFileDownloadPath(file);
        } catch (NoNepomukFoundException e) {
            messageHandler.handleMessage("Nepomuk not available.", e, MessageType.ERROR);
        }
        Assert.assertEquals(NEPOMUK_URL + FILES_ENDPOINT + ID, url);
    }
    
    @Test
    public void test_getFileDownloadPath_exception() throws NoNepomukFoundException {
        Mockito.when(nepomukDiscoverer.getRandomNepomukInstance()).thenThrow(new NoNepomukFoundException());
        
        IFile file = new File();
        String ID = "ID";
        file.setStorageId(ID);
        
        String url = serviceToTest.getFileDownloadPath(file);
        Assert.assertNull(url);
    }
}
