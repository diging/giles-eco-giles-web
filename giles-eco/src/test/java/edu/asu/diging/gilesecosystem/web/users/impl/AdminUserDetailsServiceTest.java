package edu.asu.diging.gilesecosystem.web.users.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.PathResource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.PropertiesPersister;

import edu.asu.diging.gilesecosystem.web.users.GilesGrantedAuthority;

public class AdminUserDetailsServiceTest {

    @Mock
    private PropertiesPersister persister;
    @Mock
    private Properties users;
    @Mock
    private PathResource customPropsResource;

    @InjectMocks
    private AdminUserDetailsService adminDetailsServiceToTest;

    final private String USERNAME = "username";
    final private String PW = "$2a$05$KuDuEHjVEc7ZvCBxCAwmteHeWFCqOrXlSssMz5yhALaDuJtB7Feb2";
    final private String PW_PLAIN = "password";
    final private String USERDATA = PW + "," + GilesGrantedAuthority.ROLE_ADMIN
            + ",enabled";

    @Before
    public void setUp() {
        adminDetailsServiceToTest = new AdminUserDetailsService();
        MockitoAnnotations.initMocks(this);

        Mockito.when(users.getProperty(USERNAME)).thenReturn(USERDATA);
    }

    @Test
    public void test_loadUserByUsername_success() {

        UserDetails details = adminDetailsServiceToTest.loadUserByUsername(USERNAME);
        Assert.assertEquals(USERNAME, details.getUsername());
        Assert.assertEquals(PW, details.getPassword());

        Collection<? extends GrantedAuthority> roles = details.getAuthorities();
        Assert.assertEquals(GilesGrantedAuthority.ROLE_ADMIN,
                roles.toArray(new GilesGrantedAuthority[roles.size()])[0].getAuthority());
    }

    @Test(expected = UsernameNotFoundException.class)
    public void test_loadUserByUsername_notFound() {
        adminDetailsServiceToTest.loadUserByUsername("notExisting");
    }

    @Test(expected = UsernameNotFoundException.class)
    public void test_loadUserByUsername_incomplete() {
        final String userData = PW + "," + GilesGrantedAuthority.ROLE_ADMIN + "";
        Mockito.when(users.getProperty(USERNAME)).thenReturn(userData);

        adminDetailsServiceToTest.loadUserByUsername(USERNAME);
    }

    @Test
    public void test_getAllAdmins() {
        Entry<Object, Object> entry = new SimpleEntry<>(USERNAME, USERDATA);
        Set<Entry<Object, Object>> set = new HashSet<>();
        set.add(entry);
        Mockito.when(users.entrySet()).thenReturn(set);

        List<UserDetails> admins = adminDetailsServiceToTest.getAllAdmins();
        Assert.assertTrue(admins.size() == 1);
        Assert.assertEquals(USERNAME, admins.get(0).getUsername());
        Assert.assertEquals("", admins.get(0).getPassword());
        Assert.assertEquals(
                GilesGrantedAuthority.ROLE_ADMIN,
                ((GilesGrantedAuthority)admins.get(0).getAuthorities().toArray()[0]).getAuthority());
    }
    
    @Test
    public void test_changePassword_success() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        Mockito.when(customPropsResource.getOutputStream()).thenReturn(out);
        
        adminDetailsServiceToTest.changePassword(USERNAME, PW);
        Mockito.verify(persister).store(users, out, "");;
    }
    
    @Test
    public void test_changePassword_failure() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        Mockito.when(customPropsResource.getOutputStream()).thenReturn(out);
        
        Mockito.doThrow(new IOException()).when(persister).store(users, out, "");
        boolean success = adminDetailsServiceToTest.changePassword(USERNAME, PW);
        Assert.assertFalse(success);
    }
    
    @Test
    public void test_isPasswordValid_valid() {
        boolean valid = adminDetailsServiceToTest.isPasswordValid(USERNAME, PW_PLAIN);
        Assert.assertTrue(valid);
    }
    
    @Test
    public void test_isPasswordValid_invalid() {
        boolean valid = adminDetailsServiceToTest.isPasswordValid(USERNAME, "wrong");
        Assert.assertFalse(valid);
    }
}
