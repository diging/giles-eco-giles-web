package edu.asu.diging.gilesecosystem.web.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.www.BasicAuthenticationConverter;

public class GilesAuthenticationConverter extends BasicAuthenticationConverter {

    @Override
    public UsernamePasswordAuthenticationToken convert(HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token = super.convert(request);
        
        return token;
    }

}
