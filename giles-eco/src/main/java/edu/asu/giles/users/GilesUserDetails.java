package edu.asu.giles.users;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.social.security.SocialUserDetails;

/**
 * UserDetails implementation for Conceptpower.
 * 
 * @author Julia Damerow
 *
 */
class GilesUserDetails implements SocialUserDetails {

    /**
	 * 
	 */
    private static final long serialVersionUID = -2936283695620688432L;

    private String username;
    private String name;
    private String password;
    private List<GilesGrantedAuthority> authorities;
    private String email;
    
    public GilesUserDetails(String username, String name, String password,
            List<GilesGrantedAuthority> authorities, String email) {
        super();
        this.username = username;
        this.name = name;
        this.password = password;
        this.authorities = authorities;
        this.email = email;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getUserId() {
        return username;
    }

}