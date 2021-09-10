package edu.asu.diging.gilesecosystem.web.core.users.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.web.core.users.GilesGrantedAuthority;
import edu.asu.diging.gilesecosystem.web.core.users.IAdminUserDetailsService;

/**
 * User service for admin users. Currently this class reads/stores admin users from/in
 * a properties file (user.properties).
 * @author jdamerow
 *
 */
@Service("adminDetailsService")
public class AdminUserDetailsService implements IAdminUserDetailsService {

    @Autowired
    private ISystemMessageHandler messageHandler;
  
    private PropertiesPersister persister;
    private Properties users;
    private PathResource customPropsResource;
    
    @PostConstruct
    public void init() throws IOException, URISyntaxException {
        persister = new DefaultPropertiesPersister();
        users = new Properties();
        
        URL resURL = getClass().getResource("/user.properties");
        customPropsResource = new PathResource(resURL.toURI());
        
        persister.load(users, customPropsResource.getInputStream());      
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.users.IAdminUserDetailsService#loadUserByUsername(java.lang.String)
     */
    @Override
    public UserDetails loadUserByUsername(String arg0) throws UsernameNotFoundException {
        String userData = users.getProperty(arg0);
        if (userData == null) {
            throw new UsernameNotFoundException("User could not be found in user file.");
        }
        
        String[] data = userData.split(",");
        if (data.length < 3) {
            throw new UsernameNotFoundException("The userdata is incomplete.");
        }
        
        List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
        roles.add(new GilesGrantedAuthority(data[1]));
        
        UserDetails details = new User(arg0, data[0], data[2].equals("enabled"), true, true, true, roles);
        return details;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.users.IAdminUserDetailsService#getAllAdmins()
     */
    @Override
    public List<UserDetails> getAllAdmins() {
        List<UserDetails> userList = new ArrayList<>();
        Set<Entry<Object, Object>> entries = users.entrySet();
        for (Entry<Object, Object> entry : entries) {
            String userData = users.getProperty(entry.getKey().toString());
            String[] data = userData.split(",");
            if (data.length >= 3) {
                List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
                roles.add(new GilesGrantedAuthority(data[1]));
                
                UserDetails details = new User(entry.getKey().toString(), "", roles);
                userList.add(details);
            }
        }
        return userList;
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.users.IAdminUserDetailsService#changePassword(java.lang.String, java.lang.String)
     */
    @Override
    public boolean changePassword(String username, String password) {
        String hashedPW = BCrypt.hashpw(password, BCrypt.gensalt());
        users.put(username, hashedPW + "," + GilesGrantedAuthority.ROLE_ADMIN + ",enabled");
        try {
            persister.store(users, customPropsResource.getOutputStream(), "");
        } catch (IOException e) {
            messageHandler.handleMessage("Could not store properties.", e, MessageType.ERROR);
            return false;
        }

        return true;
    }
    
    /**
     * Checks if the provided password is valid. This method expects a plaintext 
     * password and will hash it using the BCrypt algorithm to check it against the
     * stored data.
     * @param username Username of user whose password should be checked.
     * @param password Plaintext password.
     * @return
     */
    @Override
    public boolean isPasswordValid(String username, String password) {
        UserDetails details = loadUserByUsername(username);
        return BCrypt.checkpw(password, details.getPassword());
    }
}
