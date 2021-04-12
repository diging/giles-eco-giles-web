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
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.StringUtils;

public class CitesphereTokenFilter extends AbstractAuthenticationProcessingFilter {

    public static final String AUTHENTICATION_SCHEME_BASIC = "Bearer";

    public CitesphereTokenFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        chain.doFilter(request, response);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null) {
            throw new AuthenticationCredentialsNotFoundException(
                    "No Bearer token found.");
        }
        header = header.trim();
        if (!StringUtils.startsWithIgnoreCase(header, AUTHENTICATION_SCHEME_BASIC)) {
            throw new AuthenticationCredentialsNotFoundException(
                    "No Bearer token found.");
        }

        String token = header.substring(AUTHENTICATION_SCHEME_BASIC.length() + 1);

        CitesphereToken citesphereToken = new CitesphereToken(token);

        return this.getAuthenticationManager().authenticate(citesphereToken);
    }

}
