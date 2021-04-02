package edu.asu.diging.gilesecosystem.web.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.StringUtils;

public class CitesphereTokenFilter extends AbstractAuthenticationProcessingFilter {
    
    public static final String AUTHENTICATION_SCHEME_BASIC = "Bearer";
    
    private AuthenticationSuccessHandler successHandler;

    public CitesphereTokenFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
        
        successHandler = new AuthenticationSuccessHandler() {
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                    Authentication authentication) throws IOException, ServletException {
                // no-op - just allow filter chain to continue to token endpoint
            }
        };
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        this.successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null) {
            throw new AuthenticationCredentialsNotFoundException("No Bearer token found.");
        }
        header = header.trim();
        if (!StringUtils.startsWithIgnoreCase(header, AUTHENTICATION_SCHEME_BASIC)) {
            throw new AuthenticationCredentialsNotFoundException("No Bearer token found.");
        }
        
        String token = header.substring(AUTHENTICATION_SCHEME_BASIC.length() + 1);
        
        CitesphereToken citesphereToken = new CitesphereToken(token);
        
        return this.getAuthenticationManager().authenticate(citesphereToken);
    }

}
