package edu.asu.diging.gilesecosystem.web.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.web.context.request.NativeWebRequest;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.users.GilesGrantedAuthority;
import edu.asu.diging.gilesecosystem.web.core.users.IUserManager;
import edu.asu.diging.gilesecosystem.web.core.users.User;

public final class SimpleSignInAdapter implements SignInAdapter {
    
    @Autowired
    private ISystemMessageHandler messageHandler;

    private IUserManager userManager;
    private IUserHelper userHelper;

    public SimpleSignInAdapter(IUserManager userManager, IUserHelper userHelper) {
        this.userManager = userManager;
        this.userHelper = userHelper;
    }

    public String signIn(String userId, Connection<?> connection,
            NativeWebRequest request) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        User user = userManager.findUserByProviderUserId(connection.getKey().getProviderUserId(), connection.getKey().getProviderId());
        
        if (user == null) {
            authorities.add(new GilesGrantedAuthority(
                    GilesGrantedAuthority.ROLE_USER));
            user = userHelper.createUser(connection);

            try {
                userManager.addUser(user);
            } catch (UnstorableObjectException e) {
                messageHandler.handleMessage("Could not add user.", e, MessageType.ERROR);
                user = null;
            }
        } else {
            List<String> roles = user.getRoles();
            if (roles != null) {
                for (String role : roles) {
                    authorities.add(new GilesGrantedAuthority(role));
                }
            }
        }
        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(user, null,
                                authorities));
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(
                request.getNativeRequest(HttpServletRequest.class),
                request.getNativeResponse(HttpServletResponse.class));

        if (savedRequest != null) {
            return savedRequest.getRedirectUrl();
        }
        return null;
    }

}