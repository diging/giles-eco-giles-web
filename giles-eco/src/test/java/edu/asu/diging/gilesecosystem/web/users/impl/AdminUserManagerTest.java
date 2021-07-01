package edu.asu.diging.gilesecosystem.web.users.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import edu.asu.diging.gilesecosystem.web.core.exceptions.BadPasswordException;
import edu.asu.diging.gilesecosystem.web.core.exceptions.UnauthorizedException;
import edu.asu.diging.gilesecosystem.web.core.users.IAdminUserDetailsService;
import edu.asu.diging.gilesecosystem.web.core.users.impl.AdminUserManager;

public class AdminUserManagerTest {

    @Mock
    private IAdminUserDetailsService adminDetailsService;
    
    @InjectMocks
    private AdminUserManager adminManagerToTest;
    
    final private String USERNAME = "username";
    final private String OLD_PW = "old_pw";
    final private String NEW_PW = "new_pw";
    
    
    @Before
    public void setUp() {
        adminManagerToTest = new AdminUserManager();
        MockitoAnnotations.initMocks(this);
       
        Mockito.when(adminDetailsService.isPasswordValid(USERNAME, OLD_PW)).thenReturn(true);
        Mockito.when(adminDetailsService.changePassword(USERNAME, NEW_PW)).thenReturn(true);
        
    }
    
    @Test
    public void test_getAdministrators_exist() {
        List<UserDetails> users = new ArrayList<UserDetails>();
        users.add(new User(USERNAME, OLD_PW, new ArrayList<>()));
        
        Mockito.when(adminDetailsService.getAllAdmins()).thenReturn(users);
        
        List<UserDetails> retrievedUsers = adminManagerToTest.getAdministrators();
        Assert.assertArrayEquals(users.toArray(), retrievedUsers.toArray());
    }
    
    @Test 
    public void test_getAdministrators_empty() {
        List<UserDetails> users = new ArrayList<UserDetails>();
        
        Mockito.when(adminDetailsService.getAllAdmins()).thenReturn(users);
        
        List<UserDetails> retrievedUsers = adminManagerToTest.getAdministrators();
        Assert.assertTrue(retrievedUsers.isEmpty());
    }
    
    @Test
    public void test_isPasswordValid_true() {
        Mockito.when(adminDetailsService.isPasswordValid(USERNAME, OLD_PW)).thenReturn(true);
        Assert.assertTrue(adminManagerToTest.isPasswordValid(USERNAME, OLD_PW));
    }
    
    @Test
    public void test_isPasswordValid_false() {
        Mockito.when(adminDetailsService.isPasswordValid(USERNAME, OLD_PW)).thenReturn(false);
        Assert.assertFalse(adminManagerToTest.isPasswordValid(USERNAME, OLD_PW));
    }
    
    @Test
    public void test_updatePassword_success() throws BadPasswordException, UnauthorizedException {
        boolean success = adminManagerToTest.updatePassword(USERNAME, OLD_PW, NEW_PW);
        Assert.assertTrue(success);
    }
    
    @Test(expected = UnauthorizedException.class)
    public void test_updatePassword_wrongPassword() throws BadPasswordException, UnauthorizedException {
        final String WRONG_PW = "wrong_pw";
        Mockito.when(adminDetailsService.isPasswordValid(USERNAME, WRONG_PW)).thenReturn(false);
        
        adminManagerToTest.updatePassword(USERNAME, WRONG_PW, NEW_PW);
    }
    
    @Test(expected = BadPasswordException.class)
    public void test_updatePassword_emptyPassword() throws BadPasswordException, UnauthorizedException {
        final String EMPTY_PW = "";
        adminManagerToTest.updatePassword(USERNAME, OLD_PW, EMPTY_PW);
    }
    
    @Test(expected = BadPasswordException.class)
    public void test_updatePassword_nullPassword() throws BadPasswordException, UnauthorizedException {
        final String NULL_PW = "";
        adminManagerToTest.updatePassword(USERNAME, OLD_PW, NULL_PW);
    }
    
}
