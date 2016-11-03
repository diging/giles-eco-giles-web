package edu.asu.giles.rest;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.giles.aspects.access.annotations.AppTokenCheck;
import edu.asu.giles.rest.util.IJSONHelper;
import edu.asu.giles.tokens.ITokenService;
import edu.asu.giles.users.User;

@Controller
public class TokenController {
    
    @Autowired
    private ITokenService tokenService;
    
    @Autowired
    private IJSONHelper jsonHelper;

    @AppTokenCheck
    @RequestMapping(value = "/rest/token", method = RequestMethod.POST)
    public ResponseEntity<String> getUserToken(@RequestParam(defaultValue = "") String accessToken, @RequestParam(defaultValue = "") String providerToken,
            HttpServletRequest request, User user) {
        
        String token = tokenService.generateApiToken(user);
        Map<String, String> resp = new HashMap<String, String>();
        resp.put("authentication", "success");
        resp.put("token", token);
        
        return jsonHelper.generateSimpleResponse(resp, HttpStatus.OK);
    }
}
