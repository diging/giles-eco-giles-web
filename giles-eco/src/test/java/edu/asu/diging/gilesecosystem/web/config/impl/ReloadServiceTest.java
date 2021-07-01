package edu.asu.diging.gilesecosystem.web.config.impl;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.asu.diging.gilesecosystem.web.config.IAdjustableConnectionFactory;
import edu.asu.diging.gilesecosystem.web.config.IReloadService;
import edu.asu.diging.gilesecosystem.web.core.exceptions.FactoryDoesNotExistException;

public class ReloadServiceTest {
    
    @Mock private Map<String, IAdjustableConnectionFactory> factories;
    
    @Mock private IAdjustableConnectionFactory<?> factory1;

    @InjectMocks private IReloadService serviceToTest;
    
    private final String FACT_1 = "FACT_1";
    private final String CLIENT_ID = "client";
    private final String SECRET = "secret";
    
    @Before
    public void setUp() {
        serviceToTest = new ReloadService();
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void test_updateFactory_success() throws FactoryDoesNotExistException {
        Mockito.when(factories.get(FACT_1)).thenReturn(factory1);
        
        serviceToTest.updateFactory(FACT_1,CLIENT_ID, SECRET);
        Mockito.verify(factory1).update(CLIENT_ID, SECRET);
    }
    
    @Test(expected=FactoryDoesNotExistException.class)
    public void test_updateFactory_failed() throws FactoryDoesNotExistException {
        serviceToTest.updateFactory(FACT_1,CLIENT_ID, SECRET);
    }
    
    
}
