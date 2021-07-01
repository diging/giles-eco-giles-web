package edu.asu.diging.gilesecosystem.web.core.exceptions;

import org.springframework.ldap.AuthenticationException;

public class OAuthException extends AuthenticationException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public OAuthException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public OAuthException(javax.naming.AuthenticationException cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
