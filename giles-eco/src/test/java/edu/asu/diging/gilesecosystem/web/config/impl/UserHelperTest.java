package edu.asu.diging.gilesecosystem.web.config.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.UserProfile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.gilesecosystem.web.api.util.IResponseHelper;
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
    
    @Mock
    private IResponseHelper responseHelper;
    
    @InjectMocks private IUserHelper helperToTest;
    
    private String USER_ID = "user_id";
    private CitesphereToken citesphereToken = new CitesphereToken("71b9cc36-939d-4e28-89e9-e5bfca1b26c3");
    private String NAME = "name";
    private String FIRST_NAME = "firstname";
    private String LAST_NAME = "lastName";
    private String EMAIL = "email";
    private String USERNAME = "username";
    private String PROVIDER_ID = "providerId";
    private String PROVIDER_USER_ID = "providerUserId";
    private String EXISTING_USERNAME = USERNAME + "_" + PROVIDER_ID;
    private String NEW_USERNAME = "newuser_providerId";
    private String DOCUMENT_ID = "documentId";
    private DocumentAccess access = DocumentAccess.PRIVATE;
    private String FILE_ID = "fileId";
    private String UPLOAD_ID = "uploadId";
    
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
    public void test_checkUserPermission_whenUserHasPermission() {
        CitesphereUser user = new CitesphereUser();
        user.setUsername("newuser");
        user.setAuthorizingClient("providerId");
        citesphereToken.setPrincipal(user);
        IDocument document = createDocument(NEW_USERNAME);
        Assert.assertTrue(helperToTest.checkUserPermission(document, citesphereToken));
    }
    
    @Test
    public void test_checkUserPermission_whenUserDoesNotHavePermission() {
        CitesphereUser user = new CitesphereUser();
        user.setUsername("wrongUser");
        user.setAuthorizingClient("providerId");
        citesphereToken.setPrincipal(user);
        IDocument document = createDocument(NEW_USERNAME);
        Assert.assertFalse(helperToTest.checkUserPermission(document, citesphereToken));
    }
    
    @Test
    public void test_generateUnauthorizedUserResponse() {
        Map<String, String> unauthorizedMsgs = new HashMap<String, String>();
        unauthorizedMsgs.put("errorMsg", "User is not authorized to check status.");
        unauthorizedMsgs.put("errorCode", "401");
        Mockito.when(responseHelper.generateResponse(Mockito.anyMap(), Mockito.any(HttpStatus.class))).thenReturn(generateResponse(unauthorizedMsgs, HttpStatus.UNAUTHORIZED));
        ResponseEntity<String> response = helperToTest.generateUnauthorizedUserResponse();
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    
    private Document createDocument(String username) {
        Document document = new Document();
        document.setId(DOCUMENT_ID);
        document.setAccess(access);
        document.setFileIds(Arrays.asList(FILE_ID));
        document.setUploadId(UPLOAD_ID);
        document.setUploadedFileId(FILE_ID);
        document.setUsername(username);
        return document;
    }
    
    private ResponseEntity<String> generateResponse(Map<String, String> msgs,
            HttpStatus status) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ObjectNode root = mapper.createObjectNode();
        for (String key : msgs.keySet()) {
            root.put(key, msgs.get(key));
        }

        StringWriter sw = new StringWriter();
        try {
            mapper.writeValue(sw, root);
        } catch (IOException e) {
            return new ResponseEntity<String>(
                    "{\"errorMsg\": \"Could not write json result.\", \"errorCode\": \"errorCode\": \"500\" }",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(sw.toString(), status);
    }
}
