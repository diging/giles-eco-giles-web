package edu.asu.diging.gilesecosystem.web.config;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import edu.asu.diging.gilesecosystem.web.core.users.CitesphereUser;

public class CitesphereToken extends AbstractAuthenticationToken {
    
    private String token;
    private CitesphereUser principal;
    
    public CitesphereToken(String token) {
        super(null);
        this.token = token;
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
        return token;
    }
    
    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public void setPrincipal(CitesphereUser principal) {
        this.principal = principal;
    }

}
