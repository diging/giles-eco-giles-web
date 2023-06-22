package edu.asu.diging.gilesecosystem.web.config.impl;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.UserProfile;

import edu.asu.diging.gilesecosystem.web.config.CitesphereToken;
import edu.asu.diging.gilesecosystem.web.config.IUserHelper;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.impl.Document;
import edu.asu.diging.gilesecosystem.web.core.users.AccountStatus;
import edu.asu.diging.gilesecosystem.web.core.users.CitesphereUser;
import edu.asu.diging.gilesecosystem.web.core.users.IUserManager;
import edu.asu.diging.gilesecosystem.web.core.users.User;

public class UserHelperTest {

    @Mock private IUserManager userManager;
    
    @Mock private Connection<?> connection;
    
    @InjectMocks private IUserHelper helperToTest;
    
    private String USER_ID = "user_id";
    private String DOCUMENT_ID = "documentId";
    private DocumentAccess access = DocumentAccess.PRIVATE;
    private String FILE_ID = "fileId";
    private String UPLOAD_ID = "uploadId";
    private CitesphereToken citesphereToken = new CitesphereToken("71b9cc36-939d-4e28-89e9-e5bfca1b26c3");
    
    @Before
    public void setUp() {
        helperToTest = new UserHelper();
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void test_createUser_userNotExisting() {
        String NAME = "name";
        String FIRST_NAME = "firstname";
        String LAST_NAME = "lastName";
        String EMAIL = "email";
        String USERNAME = "username";
        String PROVIDER_ID = "providerId";
        String PROVIDER_USER_ID = "providerUserId";
        
        UserProfile profile = new UserProfile(USER_ID, NAME, FIRST_NAME, LAST_NAME, EMAIL, USERNAME);
        Mockito.when(connection.fetchUserProfile()).thenReturn(profile);
        Mockito.when(connection.getKey()).thenReturn(new ConnectionKey(PROVIDER_ID, PROVIDER_USER_ID));
        
        User createdUser = helperToTest.createUser(connection);
        
        Assert.assertNotNull(createdUser);
        Assert.assertEquals(NAME, createdUser.getName());
        Assert.assertEquals(FIRST_NAME, createdUser.getFirstname());
        Assert.assertEquals(LAST_NAME, createdUser.getLastname());
        Assert.assertEquals(EMAIL, createdUser.getEmail());
        Assert.assertEquals(USERNAME + "_" + PROVIDER_ID, createdUser.getUsername());
        Assert.assertEquals(PROVIDER_ID, createdUser.getProvider());
        Assert.assertEquals(PROVIDER_USER_ID, createdUser.getUserIdOfProvider());
        Assert.assertEquals(AccountStatus.ADDED, createdUser.getAccountStatus());
    }
    
    @Test
    public void test_createUser_userExists() {
        String NAME = "name";
        String FIRST_NAME = "firstname";
        String LAST_NAME = "lastName";
        String EMAIL = "email";
        String USERNAME = "username";
        String PROVIDER_ID = "providerId";
        String PROVIDER_USER_ID = "providerUserId";
        
        String EXISTING_USERNAME = USERNAME + "_" + PROVIDER_ID;
        String NEW_USERNAME = "newuser_providerId";
        
        UserProfile profile = new UserProfile(USER_ID, NAME, FIRST_NAME, LAST_NAME, EMAIL, USERNAME);
        
        User user = new User();
        user.setUsername(EXISTING_USERNAME);
        
        Mockito.when(userManager.findUser(EXISTING_USERNAME)).thenReturn(user);
        Mockito.when(userManager.getUniqueUsername(PROVIDER_ID)).thenReturn(NEW_USERNAME);
        
        Mockito.when(connection.fetchUserProfile()).thenReturn(profile);
        Mockito.when(connection.getKey()).thenReturn(new ConnectionKey(PROVIDER_ID, PROVIDER_USER_ID));
        
        User createdUser = helperToTest.createUser(connection);
        
        Assert.assertNotNull(createdUser);
        Assert.assertEquals(NAME, createdUser.getName());
        Assert.assertEquals(FIRST_NAME, createdUser.getFirstname());
        Assert.assertEquals(LAST_NAME, createdUser.getLastname());
        Assert.assertEquals(EMAIL, createdUser.getEmail());
        Assert.assertEquals(NEW_USERNAME, createdUser.getUsername());
        Assert.assertEquals(PROVIDER_ID, createdUser.getProvider());
        Assert.assertEquals(PROVIDER_USER_ID, createdUser.getUserIdOfProvider());
        Assert.assertEquals(AccountStatus.ADDED, createdUser.getAccountStatus());
    }
    
    @Test
    public void test_isUserPermittedToAccessDocument_whenUserIsAllowedToAccessDocument() {
        IDocument document = createDocument();
        citesphereToken.setPrincipal(createCitesphereUser("dab"));
        Assert.assertTrue(helperToTest.isUserPermittedToAccessDocument(document, citesphereToken));
    }
    
    @Test
    public void test_isUserPermittedToAccessDocument_whenUserIsNotAllowedToAccessDocument() {
        IDocument document = createDocument();
        citesphereToken.setPrincipal(createCitesphereUser("test1"));
        Assert.assertFalse(helperToTest.isUserPermittedToAccessDocument(document, citesphereToken));
    }
    
    private Document createDocument() {
        Document document = new Document();
        document.setId(DOCUMENT_ID);
        document.setAccess(access);
        document.setFileIds(Arrays.asList(FILE_ID));
        document.setUploadId(UPLOAD_ID);
        document.setUploadedFileId(FILE_ID);
        document.setUsername("dab_citesphere");
        return document;
    }
    
    private CitesphereUser createCitesphereUser(String userName) {
        CitesphereUser user = new CitesphereUser();
        user.setAuthorizingClient("citesphere");
        user.setUsername(userName);
        return user;
    }
}
