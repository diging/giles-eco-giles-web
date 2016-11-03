package edu.asu.giles.aspects.access;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.google.common.net.HttpHeaders;

import edu.asu.giles.aspects.access.annotations.AppTokenCheck;
import edu.asu.giles.aspects.access.annotations.TokenCheck;
import edu.asu.giles.aspects.access.openid.google.CheckerResult;
import edu.asu.giles.aspects.access.openid.google.ValidationResult;
import edu.asu.giles.aspects.access.tokens.IChecker;
import edu.asu.giles.aspects.access.tokens.impl.AppTokenChecker;
import edu.asu.giles.aspects.access.tokens.impl.GilesChecker;
import edu.asu.giles.aspects.access.tokens.impl.GitHubChecker;
import edu.asu.giles.aspects.access.tokens.impl.GoogleChecker;
import edu.asu.giles.exceptions.InvalidTokenException;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.service.IIdentityProviderRegistry;
import edu.asu.giles.tokens.IApiTokenContents;
import edu.asu.giles.tokens.IAppToken;
import edu.asu.giles.tokens.impl.ApiTokenContents;
import edu.asu.giles.tokens.impl.AppToken;
import edu.asu.giles.users.AccountStatus;
import edu.asu.giles.users.IUserManager;
import edu.asu.giles.users.User;

public class RestSecurityAspectTest {

    private final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private final String INVALID_ACCESS_TOKEN = "INVALID_ACCESS_TOKEN";
    private final String APP_TOKEN = "APP_TOKEN";
    private final String INVALID_APP_TOKEN = "INVALID_APP_TOKEN";

    @Mock
    private IUserManager userManager;

    @Mock
    private IFilesManager filesManager;
    
    @Mock
    private IIdentityProviderRegistry identityProvidersRegistry;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature sig;

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private GitHubChecker githubChecker;
    
    @Mock
    private GilesChecker gilesChecker;
    
    @Mock
    private AppTokenChecker appTokenChecker;
    
    @Spy
    private List<IChecker> checkers = new ArrayList<IChecker>();

    @InjectMocks
    private RestSecurityAspect aspectToTest = new RestSecurityAspect();
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        checkers.add(githubChecker);
        checkers.add(gilesChecker);
        checkers.add(appTokenChecker);
        
        Mockito.when(githubChecker.getId()).thenReturn(GitHubChecker.ID);
        Mockito.when(gilesChecker.getId()).thenReturn(GilesChecker.ID);
        Mockito.when(appTokenChecker.getId()).thenReturn(AppTokenChecker.ID);
        
        Mockito.when(identityProvidersRegistry.getCheckerId("github")).thenReturn(GitHubChecker.ID);
        
        aspectToTest.init();   
        
        User user = new User();
        user.setUsername("test");
        user.setAccountStatus(AccountStatus.APPROVED);
        Authentication auth = new UsernamePasswordAuthenticationToken(user,null);
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        User addedAccount = new User();
        addedAccount.setUsername("test2");
        addedAccount.setAccountStatus(AccountStatus.ADDED);
        
        User revokedAccount = new User();
        revokedAccount.setUsername("test3");
        revokedAccount.setAccountStatus(AccountStatus.REVOKED);
        
        Mockito.when(userManager.findUser("test")).thenReturn(user);
        Mockito.when(userManager.findUserByProviderUserId("test", "github")).thenReturn(user);
        Mockito.when(userManager.findUser("test2")).thenReturn(addedAccount);
        Mockito.when(userManager.findUser("test3")).thenReturn(revokedAccount);
    }

    @Test
    public void test_checkAppTokenAccess_success() throws Throwable {
        setUpTokenMocking("test");

        User user = new User();
        prepareAppTokenMethodCalls(APP_TOKEN, "token", ACCESS_TOKEN, user);
        AppTokenCheck check = createAppTokenAccessCheckAnnotation("token");

        Mockito.when(joinPoint.proceed()).thenReturn("proceed");

        Object returnObj = aspectToTest.checkAppTokenAccess(joinPoint, check);
        Assert.assertEquals("proceed", returnObj);
        Assert.assertEquals("test", user.getUsername());
    }

    @Test
    public void test_checkAppTokenAccess_tokenInHeader_success() throws Throwable {
        Mockito.when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(
                "token " + APP_TOKEN);
        setUpTokenMocking("test");

        User user = new User();
        prepareAppTokenMethodCalls("", "token", ACCESS_TOKEN, user);
        AppTokenCheck check = createAppTokenAccessCheckAnnotation("token");

        Mockito.when(joinPoint.proceed()).thenReturn("proceed");

        Object returnObj = aspectToTest.checkAppTokenAccess(joinPoint, check);
        Assert.assertEquals("proceed", returnObj);
        Assert.assertEquals("test", user.getUsername());
    }

    @Test
    public void test_checkAppTokenAccess_invalidToken() throws Throwable {
        setUpTokenMocking("test");

        User user = new User();
        prepareAppTokenMethodCalls(INVALID_APP_TOKEN, "token", INVALID_ACCESS_TOKEN, user);
        AppTokenCheck check = createAppTokenAccessCheckAnnotation("token");

        Mockito.when(joinPoint.proceed()).thenReturn("proceed");

        Object returnObj = aspectToTest.checkAppTokenAccess(joinPoint, check);
        
        Assert.assertEquals(ResponseEntity.class, returnObj.getClass());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED,
                ((ResponseEntity) returnObj).getStatusCode());
    }

    @Test()
    public void test_checkAppTokenAccess_tokenInHeader_invalidToken() throws Throwable {
        Mockito.when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(
                "token " + INVALID_APP_TOKEN);
        setUpTokenMocking("test");

        User user = new User();
        prepareAppTokenMethodCalls("", "token", ACCESS_TOKEN, user);
        AppTokenCheck check = createAppTokenAccessCheckAnnotation("token");

        Mockito.when(joinPoint.proceed()).thenReturn("proceed");

        Object returnObj = aspectToTest.checkAppTokenAccess(joinPoint, check);
        
        Assert.assertEquals(ResponseEntity.class, returnObj.getClass());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED,
                ((ResponseEntity) returnObj).getStatusCode());
    }

    @Test
    public void test_checkAppTokenAccess_added() throws Throwable {
        setUpTokenMocking("test2");

        User user = new User();
        prepareAppTokenMethodCalls(APP_TOKEN, "token", ACCESS_TOKEN, user);
        AppTokenCheck check = createAppTokenAccessCheckAnnotation("token");
        Object returnObj = aspectToTest.checkAppTokenAccess(joinPoint, check);

        Assert.assertEquals(ResponseEntity.class, returnObj.getClass());
        Assert.assertEquals(HttpStatus.FORBIDDEN,
                ((ResponseEntity) returnObj).getStatusCode());
    }

    @Test
    public void test_checkAppTokenAccess_revoked() throws Throwable {
        setUpTokenMocking("test3");

        User user = new User();
        prepareAppTokenMethodCalls(APP_TOKEN, "token", ACCESS_TOKEN, user);
        AppTokenCheck check = createAppTokenAccessCheckAnnotation("token");
        Object returnObj = aspectToTest.checkAppTokenAccess(joinPoint, check);

        Assert.assertEquals(ResponseEntity.class, returnObj.getClass());
        Assert.assertEquals(HttpStatus.FORBIDDEN,
                ((ResponseEntity) returnObj).getStatusCode());
    }

    @Test
    public void test_checkAppTokenAccess_noAccount() throws Throwable {
        setUpTokenMocking("test4");

        User user = new User();
        prepareAppTokenMethodCalls(APP_TOKEN, "token", ACCESS_TOKEN, user);
        AppTokenCheck check = createAppTokenAccessCheckAnnotation("token");

        Object returnObj = aspectToTest.checkAppTokenAccess(joinPoint, check);

        Assert.assertEquals(ResponseEntity.class, returnObj.getClass());
        Assert.assertEquals(HttpStatus.FORBIDDEN,
                ((ResponseEntity) returnObj).getStatusCode());
    }

    @Test
    public void test_checkAppTokenAccess_noToken() throws Throwable {
        setUpTokenMocking("test");

        User user = new User();
        prepareMethodCalls(null, "token", user);
        AppTokenCheck check = createAppTokenAccessCheckAnnotation("token");

        Object returnObj = aspectToTest.checkAppTokenAccess(joinPoint, check);

        Assert.assertEquals(ResponseEntity.class, returnObj.getClass());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED,
                ((ResponseEntity) returnObj).getStatusCode());
    }

    private void prepareMethodCalls(String paraValue, String paraName, User user) {

        Object[] args = new Object[3];
        args[0] = paraValue;
        args[1] = user;
        args[2] = request;

        Mockito.when(joinPoint.getArgs()).thenReturn(args);
        Mockito.when(joinPoint.getSignature()).thenReturn(sig);

        String[] argNames = new String[3];
        argNames[0] = paraName;
        argNames[1] = "user";
        argNames[2] = "request";
        Mockito.when(sig.getParameterNames()).thenReturn(argNames);

        Class<?>[] paraTypes = new Class<?>[3];
        paraTypes[0] = String.class;
        paraTypes[1] = User.class;
        paraTypes[2] = StandardMultipartHttpServletRequest.class;
        Mockito.when(sig.getParameterTypes()).thenReturn(paraTypes);
    }
    
    private void prepareAppTokenMethodCalls(String paraValue, String paraName, String providerToken, User user) {

        Object[] args = new Object[4];
        args[0] = paraValue;
        args[1] = user;
        args[2] = request;
        args[3] = providerToken;

        Mockito.when(joinPoint.getArgs()).thenReturn(args);
        Mockito.when(joinPoint.getSignature()).thenReturn(sig);

        String[] argNames = new String[4];
        argNames[0] = paraName;
        argNames[1] = "user";
        argNames[2] = "request";
        argNames[3] = "providerToken";
        Mockito.when(sig.getParameterNames()).thenReturn(argNames);

        Class<?>[] paraTypes = new Class<?>[4];
        paraTypes[0] = String.class;
        paraTypes[1] = User.class;
        paraTypes[2] = StandardMultipartHttpServletRequest.class;
        paraTypes[3] = String.class;
        Mockito.when(sig.getParameterTypes()).thenReturn(paraTypes);
    }

    private void setUpTokenMocking(String username) throws GeneralSecurityException, IOException, InvalidTokenException {
        CheckerResult validResult = new CheckerResult();
        validResult.setResult(ValidationResult.VALID);
        
        IApiTokenContents tokenContents = new ApiTokenContents();
        tokenContents.setUsername(username);
        tokenContents.setExpired(false);
        validResult.setPayload(tokenContents);
        Mockito.when(githubChecker.validateToken(ACCESS_TOKEN, "appId1")).thenReturn(validResult);
        
        CheckerResult invalidResult = new CheckerResult();
        invalidResult.setResult(ValidationResult.INVALID);
        
        invalidResult.setPayload(null);
        Mockito.when(githubChecker.validateToken(INVALID_ACCESS_TOKEN, null)).thenReturn(invalidResult);
        
        IAppToken appToken = new AppToken();
        appToken.setAppId("appId1");
        appToken.setProviderId("github");
        
        CheckerResult validAppToken = new CheckerResult();
        validAppToken.setPayload(appToken);
        validAppToken.setResult(ValidationResult.VALID);
        Mockito.when(appTokenChecker.validateToken(APP_TOKEN, null)).thenReturn(validAppToken);
        
        CheckerResult invalidAppToken = new CheckerResult();
        invalidAppToken.setPayload(null);
        invalidAppToken.setResult(ValidationResult.INVALID);
        Mockito.when(appTokenChecker.validateToken(INVALID_APP_TOKEN, null)).thenReturn(invalidAppToken);
    }

    private TokenCheck createTokenAccessCheckAnnotation(String parameterName) {
        TokenCheck check = new TokenCheck() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return TokenCheck.class;
            }

            @Override
            public String value() {
                return parameterName;
            }
        };

        return check;
    }
    
    private AppTokenCheck createAppTokenAccessCheckAnnotation(String parameterName) {
        AppTokenCheck check = new AppTokenCheck() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return TokenCheck.class;
            }

            @Override
            public String value() {
                return parameterName;
            }

            @Override
            public String providerToken() {
                return "providerToken";
            }
        };

        return check;
    }
}
