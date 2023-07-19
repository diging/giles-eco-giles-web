package edu.asu.diging.gilesecosystem.web.users;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.users.User;
import edu.asu.diging.gilesecosystem.web.core.users.UserDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.users.UserRepository;

public class UserDatabaseClientTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks 
    private UserDatabaseClient userDatabaseClient = new UserDatabaseClient();
    
    private User user;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        user = createUser("test_github", "test@gmail.com", "github", "test123", "37469232");
    }
    
    @Test
    public void test_getUser() {
        Mockito.when(userRepository.findByUsernameAndPassword("test_github", "test123")).thenReturn(user);
        User resUser = userDatabaseClient.getUser("test_github", "test123");
        Assert.assertEquals(user, resUser);
    }
    
    @Test
    public void test_findUser() {
        Mockito.when(userRepository.findByUsername("test_github")).thenReturn(user);
        User resUser = userDatabaseClient.findUser("test_github");
        Assert.assertEquals(user, resUser);
    }
    
    @Test
    public void test_findUserByProviderUserId() {
        Mockito.when(userRepository.findByUserIdOfProviderAndProvider("37469232", "github")).thenReturn(user);
        User resUser = userDatabaseClient.findUserByProviderUserId("37469232", "github");
        Assert.assertEquals(user, resUser);
    }
    
    @Test
    public void test_findUsersByEmail() {
        List<User> users = new ArrayList();
        users.add(user);
        Mockito.when(userRepository.findByEmail("test@gmail.com")).thenReturn(users);
        List<User> resUsers = userDatabaseClient.findUsersByEmail("test@gmail.com");
        Assert.assertEquals(users, resUsers);
    }
    
    @Test
    public void test_getAllUser() {
        List<User> users = new ArrayList();
        users.add(user);
        User user2 = createUser("test2_github", "test2@gmail.com", "github", "test123", "37469232");
        users.add(user2);
        Mockito.when(userRepository.findAll()).thenReturn(users);
        User[] usersList = users.toArray(new User[users.size()]);
        User[] resUsersList = userDatabaseClient.getAllUser();
        Assert.assertArrayEquals(usersList, resUsersList);
    }
    
    @Test
    public void test_addUser_success() throws UnstorableObjectException {
        User user = createUser("test2_github", "test2@gmail.com", "github", "test123", "37469232");
        Mockito.when(userRepository.save(user)).thenReturn(user);
        User resUser = userDatabaseClient.addUser(user);
        Assert.assertEquals(user, resUser);
    }
    
    @Test(expected=UnstorableObjectException.class)
    public void test_addUser_throwsException() throws UnstorableObjectException {
        User user = createUser(null, "test2@gmail.com", "github", "test123", "37469232");
        userDatabaseClient.addUser(user);
    }

    private User createUser(String username, String email, String provider, String password, String userIdOfProvider) {
        User user = new User();
        user.setUsername(username);
        user.setProvider(provider);
        user.setEmail(email);
        user.setPassword(password);
        user.setUserIdOfProvider(userIdOfProvider);
        return user;
    }
}
