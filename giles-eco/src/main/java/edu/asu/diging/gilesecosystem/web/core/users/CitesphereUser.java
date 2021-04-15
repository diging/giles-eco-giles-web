package edu.asu.diging.gilesecosystem.web.core.users;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

public class CitesphereUser {

    private String username;
    private String authorizingClient;
    private List<GrantedAuthority> roles;
    
    public CitesphereUser() {}
    
    public CitesphereUser(String username, String authorizingClient,
            List<GrantedAuthority> roles) {
        super();
        this.username = username;
        this.authorizingClient = authorizingClient;
        this.roles = roles;
    }
    
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getAuthorizingClient() {
        return authorizingClient;
    }
    public void setAuthorizingClient(String authorizingClient) {
        this.authorizingClient = authorizingClient;
    }
    public List<GrantedAuthority> getRoles() {
        return roles;
    }
    public void setRoles(List<GrantedAuthority> roles) {
        this.roles = roles;
    }
    
    
}
