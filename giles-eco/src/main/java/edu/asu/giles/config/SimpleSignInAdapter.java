package edu.asu.giles.config;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.web.context.request.NativeWebRequest;

import edu.asu.giles.exceptions.UnstorableObjectException;
import edu.asu.giles.users.AccountStatus;
import edu.asu.giles.users.GilesGrantedAuthority;
import edu.asu.giles.users.IUserManager;
import edu.asu.giles.users.User;

public final class SimpleSignInAdapter implements SignInAdapter {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());            

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
                logger.error("Could not add user.", e);
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