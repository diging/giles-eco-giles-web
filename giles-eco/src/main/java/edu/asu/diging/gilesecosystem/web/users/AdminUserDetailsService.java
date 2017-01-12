package edu.asu.diging.gilesecosystem.web.users;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.PathResource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

@Service("adminUserService")
public class AdminUserDetailsService implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
  
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
}
