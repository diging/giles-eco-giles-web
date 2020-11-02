package edu.asu.diging.gilesecosystem.web.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import edu.asu.diging.gilesecosystem.web.core.apps.IRegisteredApp;
import edu.asu.diging.gilesecosystem.web.core.service.apps.IRegisteredAppManager;
import edu.asu.diging.gilesecosystem.web.core.tokens.IAppToken;
import edu.asu.diging.gilesecosystem.web.core.tokens.ITokenService;
import edu.asu.diging.gilesecosystem.web.core.users.GilesGrantedAuthority;
import edu.asu.diging.gilesecosystem.web.core.users.GilesRole;
import io.jsonwebtoken.MalformedJwtException;

public class ApiAuthenticationProvider implements AuthenticationProvider {
    
    @Autowired
    private IRegisteredAppManager appManager;
    
    @Autowired
    private ITokenService tokenService;
    
    @Override
    public Authentication authenticate(Authentication auth)
            throws AuthenticationException {
        
        IRegisteredApp app = appManager.getApp(auth.getName());
        
        if (app == null) {
            throw new BadCredentialsException("Invalid app id.");
        }
        
        IAppToken token;
        try {
            token = tokenService.getAppTokenContents(auth.getCredentials().toString());
        } catch (MalformedJwtException e) {
            throw new BadCredentialsException("Token is malformed.");
        }
        
        if (token != null && app.getTokenIds().contains(token.getId())) {
            return new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), Arrays.asList(new GrantedAuthority[] { new GilesGrantedAuthority(GilesRole.ROlE_APP.name()) }));
        }
        
        throw new BadCredentialsException("Token is invalid for app.");
    }

    @Override
    public boolean supports(Class<?> arg0) {
        return arg0.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }

}
