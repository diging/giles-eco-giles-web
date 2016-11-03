package edu.asu.giles.users;

import org.springframework.security.core.GrantedAuthority;

public class GilesGrantedAuthority implements GrantedAuthority {

    public final static String ROLE_USER = "ROLE_USER";
    public final static String ROLE_ADMIN = "ROLE_ADMIN";

    private String roleName;

    public GilesGrantedAuthority(String name) {
        this.roleName = name;
    }

    public GilesGrantedAuthority() {
    }

    /**
		 * 
		 */
    private static final long serialVersionUID = 711167440813692597L;

    public String getAuthority() {
        return roleName;
    }

    public void setAuthority(String rolename) {
        this.roleName = rolename;
    }
}
