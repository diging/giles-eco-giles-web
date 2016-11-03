package edu.asu.giles.aspects.access;

import java.io.IOException;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.giles.aspects.access.annotations.AppTokenCheck;
import edu.asu.giles.aspects.access.annotations.DocumentAccessCheck;
import edu.asu.giles.aspects.access.annotations.FileTokenAccessCheck;
import edu.asu.giles.aspects.access.annotations.TokenCheck;
import edu.asu.giles.aspects.access.openid.google.CheckerResult;
import edu.asu.giles.aspects.access.openid.google.ValidationResult;
import edu.asu.giles.aspects.access.tokens.IChecker;
import edu.asu.giles.aspects.access.tokens.impl.AppTokenChecker;
import edu.asu.giles.aspects.access.tokens.impl.GilesChecker;
import edu.asu.giles.core.DocumentAccess;
import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.exceptions.AspectMisconfigurationException;
import edu.asu.giles.exceptions.InvalidTokenException;
import edu.asu.giles.exceptions.ServerMisconfigurationException;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.service.IIdentityProviderRegistry;
import edu.asu.giles.tokens.IApiTokenContents;
import edu.asu.giles.tokens.IAppToken;
import edu.asu.giles.tokens.ITokenContents;
import edu.asu.giles.users.AccountStatus;
import edu.asu.giles.users.IUserManager;
import edu.asu.giles.users.User;

@Aspect
@Component
public class RestSecurityAspect {

    private Logger logger = LoggerFactory.getLogger(SecurityAspect.class);

    @Autowired
    private IUserManager userManager;

    @Autowired
    private IFilesManager filesManager;
    
    @Autowired
    private IIdentityProviderRegistry identityProviderRegistry;
    
    @Autowired
    private List<IChecker> checkers;
    
    private Map<String, IChecker> tokenCheckers;
    
    @PostConstruct
    public void init() {
        tokenCheckers = new HashMap<>();
        checkers.forEach(checker -> tokenCheckers.put(checker.getId(), checker));
    }
    
    
    @Around("within(edu.asu.giles.rest..*) && @annotation(tokenCheck)")
    public Object checkAppTokenAccess(ProceedingJoinPoint joinPoint,
            AppTokenCheck tokenCheck) throws Throwable {
        logger.debug("Checking App access token for REST endpoint.");
        
        UserTokenObject userTokenObj = extractUserTokenInfo(joinPoint, tokenCheck.value(), tokenCheck.providerToken());
        
        User user = userTokenObj.user;
        String token = userTokenObj.token;
        String providerToken = userTokenObj.parameter;

        if (user == null) {
            throw new AspectMisconfigurationException(
                    "User object is missing in method.");
        }
        
        TokenHolder holder = new TokenHolder();
        ResponseEntity<String> authResult = checkAuthorization(user, token, AppTokenChecker.ID, holder, null);
        if (authResult != null) {
            return authResult;
        }
        
        IAppToken appToken = ((IAppToken)holder.tokenContents);
        String checkerId = identityProviderRegistry.getCheckerId(appToken.getProviderId());
        if (checkerId == null) {
            logger.warn("Token references non existing identity provider.");
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("errorMsg", "The token you sent references an identity provider that is not registered with Giles.");
            
            return generateResponse(msgs, HttpStatus.UNAUTHORIZED);
        }
        
        TokenHolder apiTokenHolder = new TokenHolder();
        ResponseEntity<String> apiTokenAuthResult = checkAuthorization(user, providerToken, checkerId, apiTokenHolder, appToken.getAppId());
        if (apiTokenAuthResult != null) {
            return apiTokenAuthResult;
        }
        
        IApiTokenContents tokenContents = (IApiTokenContents) apiTokenHolder.tokenContents;
        User userInToken = userManager.findUserByProviderUserId(tokenContents.getUsername(), appToken.getProviderId());
        
        if (userInToken == null) {
            logger.info("The user doesn't seem to have a Giles account.");
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("errorMsg", "The user doesn't seem to have a Giles account.");
            
            return generateResponse(msgs, HttpStatus.FORBIDDEN);
        }
        if (userInToken.getAccountStatus() != AccountStatus.APPROVED) {
            logger.info("The user account you are using has not been approved. Please contact a Giles administrator.");
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("errorMsg", "The user account you are using has not been approved. Please contact a Giles administrator.");
            
            return generateResponse(msgs, HttpStatus.FORBIDDEN);
        }
        
        fillUser(userInToken, user);
        return joinPoint.proceed();
    }
    
    @Around("within(edu.asu.giles.rest..*) && @annotation(tokenCheck)")
    public Object checkGilesTokenAccess(ProceedingJoinPoint joinPoint,
            TokenCheck tokenCheck) throws Throwable {
        
        UserTokenObject userTokenObj = extractUserTokenInfo(joinPoint, tokenCheck.value(), null);
        
        User user = userTokenObj.user;
        String token = userTokenObj.token;

        if (user == null) {
            throw new AspectMisconfigurationException(
                    "User object is missing in method.");
        }
        
        TokenHolder holder = new TokenHolder();
        ResponseEntity<String> authResult = checkAuthorization(user, token, GilesChecker.ID, holder, null);
        if (authResult != null) {
            return authResult;
        }
        
        // because we asked for the giles checker we know what type
        // the token contents is
        extractUser(user, (IApiTokenContents)holder.tokenContents);
        
        return joinPoint.proceed();
    }
    
    @Around("within(edu.asu.giles.rest..*) && @annotation(check)")
    public Object checkDocument(ProceedingJoinPoint joinPoint, DocumentAccessCheck check) throws Throwable {
                
        UserTokenObject userTokenObj = extractUserTokenInfo(joinPoint, check.github(), check.value());
        
        User user = userTokenObj.user;
        String token = userTokenObj.token;
        String docId = userTokenObj.parameter;
        
        if (user == null) {
            throw new AspectMisconfigurationException(
                    "User object is missing in method.");
        }
        
        IDocument doc = filesManager.getDocument(docId);
        if (doc == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        
        if (doc.getAccess() == DocumentAccess.PUBLIC) {
            return joinPoint.proceed();
        }
        
        TokenHolder holder = new TokenHolder();
        ResponseEntity<String> authResult = checkAuthorization(user, token, GilesChecker.ID, holder, null);
        if (authResult != null) {
            return authResult;
        }
        
        // because we asked for the giles checker we know what type
        // the token contents is
        extractUser(user, (IApiTokenContents)holder.tokenContents);

        if (!doc.getUsername().equals(user.getUsername())) {
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }
        
        return joinPoint.proceed();
    }
    
    @Around("within(edu.asu.giles.rest..*) && @annotation(check)")
    public Object checkFileGitHubAccess(ProceedingJoinPoint joinPoint, FileTokenAccessCheck check) throws Throwable {
        
        UserTokenObject userTokenObj = extractUserTokenInfo(joinPoint, check.github(), check.value());
        
        User user = userTokenObj.user;
        String token = userTokenObj.token;
        String fileId = userTokenObj.parameter;
        
        
        if (user == null) {
            throw new AspectMisconfigurationException(
                    "User object is missing in method.");
        }
        
        IFile file = filesManager.getFile(fileId);
        if (file == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        
        if (file.getAccess() == DocumentAccess.PUBLIC) {
            return joinPoint.proceed();
        }
        
        TokenHolder holder = new TokenHolder();
        ResponseEntity<String> authResult = checkAuthorization(user, token, GilesChecker.ID, holder, null);
        if (authResult != null) {
            return authResult;
        }
        
        // because we asked for the giles checker we know what type
        // the token contents is
        extractUser(user, (IApiTokenContents)holder.tokenContents);

        if (!file.getUsername().equals(user.getUsername())) {
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }
        
        return joinPoint.proceed();
    }
    
    private UserTokenObject extractUserTokenInfo(ProceedingJoinPoint joinPoint, String tokenParameter, String parameterName) {
        Object[] args = joinPoint.getArgs();
        MethodSignature sig = (MethodSignature) joinPoint.getSignature();
        String[] argNames = sig.getParameterNames();
        Class<?>[] argTypes = sig.getParameterTypes();

        User user = null;
        String token = null;
        String elementId = null;
        for (int i = 0; i < argNames.length; i++) {
            // check if GitHub token is passed as parameters
            if (argNames[i].equals(tokenParameter)) {
                token = (String) args[i];
            }
            // check if there is a request header with github token
            if (HttpServletRequest.class.isAssignableFrom(argTypes[i])) {
                String tokenHeader = ((HttpServletRequest)args[i]).getHeader(HttpHeaders.AUTHORIZATION);
                if (tokenHeader != null) {
                    token = tokenHeader.substring(6);
                }
            }
            if (argTypes[i].equals(User.class)) {
                user = (User) args[i];
            }
            
            if (parameterName != null) {
                if (argNames[i].equals(parameterName)) {
                    elementId = (String) args[i];
                }
            }
        }
        
        return new UserTokenObject(user, token, elementId);
    }
    
    private ResponseEntity<String> checkAuthorization(User user, String token, String provider, TokenHolder tokenHolder, String appId) {
        if (token == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        CheckerResult validationResult = null;
        
        try {
            validationResult = tokenCheckers.get(provider).validateToken(token, appId);
            tokenHolder.checkResult = validationResult;
            tokenHolder.tokenContents = validationResult.getPayload();
        } catch (GeneralSecurityException e) {
            logger.error("Security issue with token.", e);
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("errorMsg", e.getLocalizedMessage());
            msgs.put("provider", provider);
            
            return generateResponse(msgs, HttpStatus.UNAUTHORIZED);
        } catch (IOException e) {
            logger.error("Network issue.", e);
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("errorMsg", e.getLocalizedMessage());
            msgs.put("provider", provider);
            
            return generateResponse(msgs, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InvalidTokenException e) {
            logger.error("Token is invalid.", e);
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("errorMsg", e.getLocalizedMessage());
            msgs.put("provider", provider);
            
            return generateResponse(msgs, HttpStatus.UNAUTHORIZED);
        } catch (ServerMisconfigurationException e) {
            logger.error("Server or apps are misconfigured.", e);
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("errorMsg", e.getLocalizedMessage());
            msgs.put("provider", provider);
            
            return generateResponse(msgs, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        if (validationResult == null || validationResult.getPayload() == null) {
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("errorMsg", "Missing or invalid token.");
            msgs.put("errorCode", "401");
            msgs.put("provider", provider);
            return generateResponse(msgs, HttpStatus.UNAUTHORIZED);
        }
        
        if (validationResult.getResult() == ValidationResult.EXPIRED) {
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("errorMsg", "The sent token is expired.");
            msgs.put("errorCode", "600");
            msgs.put("provider", provider);
            return generateResponse(msgs, HttpStatus.UNAUTHORIZED);
        }
        
        if (validationResult.getResult() != ValidationResult.VALID) {
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("errorMsg", validationResult.getResult().name());
            msgs.put("errorCode", "401");
            msgs.put("provider", provider);
            return generateResponse(msgs, HttpStatus.UNAUTHORIZED);
        }
        
        return null;
    }
    
    private ResponseEntity<String> extractUser(User user, IApiTokenContents tokenContents) {
        User foundUser = userManager.findUser(tokenContents.getUsername());
        logger.debug("Authorizing: " + tokenContents.getUsername());

        if (foundUser == null) {
            return new ResponseEntity<>(
                    "{ \"error\": \"The user doesn't seem to have a Giles account.\" } ",
                    HttpStatus.FORBIDDEN);
        }
        if (foundUser.getAccountStatus() != AccountStatus.APPROVED) {
            return new ResponseEntity<>(
                    "{ \"error\": \"The user account you are using has not been approved. Please contact a Giles administrator.\" } ",
                    HttpStatus.FORBIDDEN);
        }

        fillUser(foundUser, user);
        return null;
    }
    
    private void fillUser(User filled, User toBeFilled) {
        toBeFilled.setAdmin(filled.getIsAdmin());
        toBeFilled.setEmail(filled.getEmail());
        toBeFilled.setFirstname(filled.getFirstname());
        toBeFilled.setLastname(filled.getLastname());
        toBeFilled.setProvider(filled.getProvider());
        toBeFilled.setRoles(filled.getRoles());
        toBeFilled.setUserIdOfProvider(filled.getUserIdOfProvider());
        toBeFilled.setUsername(filled.getUsername());
    }
    
    private ResponseEntity<String> generateResponse(Map<String, String> msgs, HttpStatus status) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ObjectNode root = mapper.createObjectNode();
        for (String key : msgs.keySet()) {
            root.put(key, msgs.get(key));
        }
        
        StringWriter sw = new StringWriter();
        try {
            mapper.writeValue(sw, root);
        } catch (IOException e) {
            logger.error("Could not write json.", e);
            return new ResponseEntity<String>(
                    "{\"errorMsg\": \"Could not write json result.\", \"errorCode\": \"errorCode\": \"500\" }",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return new ResponseEntity<String>(sw.toString(), status);
    }
    
    
    /*
     * Helper classes just for this aspect.
     */   
    class UserTokenObject {
        
        public User user;
        public String token;
        public String parameter;
        
        public UserTokenObject(User user, String token, String elementId) {
            super();
            this.user = user;
            this.token = token;
            this.parameter = elementId;
        }
    }
    
    class TokenHolder {
        public CheckerResult checkResult;
        public ITokenContents tokenContents;
    }
}
