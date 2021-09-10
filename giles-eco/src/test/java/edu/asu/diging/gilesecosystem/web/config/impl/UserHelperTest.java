package edu.asu.diging.gilesecosystem.web.config.impl;

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

import edu.asu.diging.gilesecosystem.web.config.IUserHelper;
import edu.asu.diging.gilesecosystem.web.core.users.AccountStatus;
import edu.asu.diging.gilesecosystem.web.core.users.IUserManager;
import edu.asu.diging.gilesecosystem.web.core.users.User;

public class UserHelperTest {

    @Mock private IUserManager userManager;
    
    @Mock private Connection<?> connection;
    
    @InjectMocks private IUserHelper helperToTest;
    
    private String USER_ID = "user_id";
    
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
}
