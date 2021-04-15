package edu.asu.diging.gilesecosystem.web.config;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import edu.asu.diging.gilesecosystem.web.core.users.CitesphereUser;

public class CitesphereToken extends AbstractAuthenticationToken {
    
    private String clientToken;
    private String userToken;
    private CitesphereUser principal;
    
    public CitesphereToken(String clientToken, String userToken) {
        super(null);
        this.clientToken = clientToken;
        this.userToken = userToken;
    }

    public CitesphereToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Object getCredentials() {
        return clientToken;
    }
    
    public String getUserToken() {
        return this.userToken;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public void setPrincipal(CitesphereUser principal) {
        this.principal = principal;
    }

}
