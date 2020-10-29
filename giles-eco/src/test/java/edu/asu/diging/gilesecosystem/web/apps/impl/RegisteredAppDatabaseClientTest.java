package edu.asu.diging.gilesecosystem.web.apps.impl;

import java.util.ArrayList;
import java.util.List;

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
import edu.asu.diging.gilesecosystem.web.core.apps.impl.RegisteredApp;
import edu.asu.diging.gilesecosystem.web.core.apps.impl.RegisteredAppDatabaseClient;

public class RegisteredAppDatabaseClientTest {

    @Mock private EntityManager em;
    
    @Mock private TypedQuery<IRegisteredApp> query;
    
    @InjectMocks private IRegisteredAppDatabaseClient dbClientToTest;
    
    private final String APP_ID_1 = "APP1";
    private final String APP_ID_2 = "APP2";
    
    private RegisteredApp app1;
    
    @Before
    public void setUp() {
        dbClientToTest = new RegisteredAppDatabaseClient();
        MockitoAnnotations.initMocks(this);
        
        app1 = new RegisteredApp();
        app1.setId(APP_ID_1);
        app1.setName("App 1");
        
        Mockito.when(em.find(RegisteredApp.class, APP_ID_1)).thenReturn(app1);
    }
    
    @Test
    public void test_getAppById_exists() {
        IRegisteredApp app = dbClientToTest.getAppById(APP_ID_1);
        Assert.assertNotNull(app);
        Assert.assertEquals(APP_ID_1, app.getId());
    }
    
    @Test
    public void test_getAppById_doesNotExists() {
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
        List<IRegisteredApp> apps = new ArrayList<IRegisteredApp>();
        apps.add(app1);
        
        IRegisteredApp app2 = new RegisteredApp();
        app2.setName("New App 2");
        app2.setId(APP_ID_2);
        apps.add(app2);

        Mockito.when(em.createQuery("SELECT a FROM RegisteredApp a", IRegisteredApp.class)).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(apps);
        
        IRegisteredApp[] allApps = dbClientToTest.getAllRegisteredApps();
        Assert.assertEquals(2, allApps.length);
    }
    
    @Test
    public void test_getAllRegisteredApps_noResults() {
        List<IRegisteredApp> apps = new ArrayList<IRegisteredApp>();
        
        Mockito.when(em.createQuery("SELECT a FROM RegisteredApp a", IRegisteredApp.class)).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(apps);
       
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
        IRegisteredApp app = dbClientToTest.getAppById(APP_ID_2);
        Assert.assertNull(app);
    }
    
}
