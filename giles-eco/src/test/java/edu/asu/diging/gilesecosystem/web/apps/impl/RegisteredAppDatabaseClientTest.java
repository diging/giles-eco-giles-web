package edu.asu.diging.gilesecosystem.web.apps.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.apps.IRegisteredApp;
import edu.asu.diging.gilesecosystem.web.core.apps.IRegisteredAppDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.apps.RegisteredAppRepository;
import edu.asu.diging.gilesecosystem.web.core.apps.impl.RegisteredApp;
import edu.asu.diging.gilesecosystem.web.core.apps.impl.RegisteredAppDatabaseClient;

public class RegisteredAppDatabaseClientTest {

    @Mock
    private RegisteredAppRepository registeredAppRepository;
    
    @InjectMocks private IRegisteredAppDatabaseClient dbClientToTest;
    
    private final String APP_ID_1 = "APP1";
    private final String APP_ID_2 = "APP2";
    
    private RegisteredApp app1;
    
    @Before
    public void setUp() {
        dbClientToTest = new RegisteredAppDatabaseClient(registeredAppRepository);
        MockitoAnnotations.initMocks(this);
        
        app1 = new RegisteredApp();
        app1.setId(APP_ID_1);
        app1.setName("App 1");
        Mockito.when(registeredAppRepository.findById(APP_ID_1)).thenReturn(Optional.of(app1));
    }
    
    @Test
    public void test_getAppById_exists() {
        IRegisteredApp app = dbClientToTest.getAppById(APP_ID_1);
        Assert.assertNotNull(app);
        Assert.assertEquals(APP_ID_1, app.getId());
    }
    
    @Test
    public void test_getAppById_doesNotExists() {
        Mockito.when(registeredAppRepository.findById(APP_ID_2)).thenReturn(Optional.empty());
        IRegisteredApp app = dbClientToTest.getAppById(APP_ID_2);
        Assert.assertNull(app);
    }
    
    @Test
    public void test_storeModifiedApp_success() throws UnstorableObjectException {
        String newName = "New App 1"; 
        app1.setName(newName);
        dbClientToTest.storeModifiedApp(app1);
        
        IRegisteredApp storedApp = dbClientToTest.getAppById(APP_ID_1);
        Assert.assertEquals(storedApp.getName(), newName);
    }
    
    @Test(expected=UnstorableObjectException.class)
    public void test_storeModifiedApp_failed() throws UnstorableObjectException {
        IRegisteredApp app2 = new RegisteredApp();
        app2.setName("New App 2");
        
        dbClientToTest.storeModifiedApp(app2);
    }
    
    @Test
    public void test_getAllRegisteredApps_results() {
        List<RegisteredApp> apps = new ArrayList<RegisteredApp>();
        apps.add(app1);
        
        RegisteredApp app2 = new RegisteredApp();
        app2.setName("New App 2");
        app2.setId(APP_ID_2);
        apps.add(app2);

        Mockito.when(registeredAppRepository.findAll()).thenReturn(apps);
        
        IRegisteredApp[] allApps = dbClientToTest.getAllRegisteredApps();
        Assert.assertEquals(2, allApps.length);
    }
    
    @Test
    public void test_getAllRegisteredApps_noResults() {
        List<IRegisteredApp> apps = new ArrayList<IRegisteredApp>();
        
        Mockito.when(registeredAppRepository.findAll()).thenReturn(null);
       
        IRegisteredApp[] allApps = dbClientToTest.getAllRegisteredApps();
        Assert.assertEquals(0, allApps.length);
    }
    
    @Test
    public void test_getById_exists() {
        IRegisteredApp app = dbClientToTest.getAppById(APP_ID_1);
        Assert.assertNotNull(app);
        Assert.assertEquals(APP_ID_1, app.getId());
    }
    
    @Test
    public void test_getById_doesNotExists() {
        Mockito.when(registeredAppRepository.findById(APP_ID_2)).thenReturn(Optional.empty());
        IRegisteredApp app = dbClientToTest.getAppById(APP_ID_2);
        Assert.assertNull(app);
    }
    
    @Test
    public void test_storeRegisteredApp_suceess() throws IllegalArgumentException, UnstorableObjectException {
        dbClientToTest.storeRegisteredApp(app1);
        Mockito.verify(registeredAppRepository).save(app1);
    }
    
    @Test(expected=UnstorableObjectException.class)
    public void test_storeRegisteredApp_throwsUnstorableObjectException() throws IllegalArgumentException, UnstorableObjectException {
        RegisteredApp app3 = new RegisteredApp();
        dbClientToTest.storeRegisteredApp(app3);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void test_storeRegisteredApp_throwsIllegalArgumentException() throws IllegalArgumentException, UnstorableObjectException {
        Mockito.when(registeredAppRepository.save(app1)).thenThrow(new IllegalArgumentException());
        dbClientToTest.storeRegisteredApp(app1);
    }
    
    @Test
    public void test_deleteRegisteredApp_success() {
        dbClientToTest.deleteRegisteredApp(app1);
        Mockito.verify(registeredAppRepository).delete(app1);
    }
}
