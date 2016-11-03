package edu.asu.giles.aspects.access;

import java.lang.annotation.Annotation;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import edu.asu.giles.aspects.access.annotations.FileAccessCheck;
import edu.asu.giles.aspects.access.annotations.UploadIdAccessCheck;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IUpload;
import edu.asu.giles.core.impl.File;
import edu.asu.giles.core.impl.Upload;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.users.AccountStatus;
import edu.asu.giles.users.IUserManager;
import edu.asu.giles.users.User;

public class SecurityAspectTest {

    @Mock private IUserManager userManager;
    
    @Mock private IFilesManager filesManager;
    
    @Mock private ProceedingJoinPoint joinPoint;
    
    @Mock private MethodSignature sig;
    
    @Mock private HttpServletRequest request;
    
    @InjectMocks
    private SecurityAspect aspectToTest;
    
    @Before
    public void setUp() {
        aspectToTest = new SecurityAspect();
        MockitoAnnotations.initMocks(this);
        
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
        
        IUpload upload = new Upload();
        upload.setUsername("test");
        Mockito.when(filesManager.getUpload("UP123")).thenReturn(upload);
        
        IUpload upload2 = new Upload();
        upload2.setUsername("test2");
        upload2.setId("UP456");
        Mockito.when(filesManager.getUpload("UP456")).thenReturn(upload2);
        
        IFile file = new File();
        file.setUsername("test");
        file.setId("FI123");
        Mockito.when(filesManager.getFile("FI123")).thenReturn(file);
        
        IFile file2 = new File();
        file2.setUsername("test2");
        file2.setId("FI123");
        Mockito.when(filesManager.getFile("FI456")).thenReturn(file2);
        
        Mockito.when(userManager.findUser("test")).thenReturn(user);
        Mockito.when(userManager.findUser("test2")).thenReturn(addedAccount);
        Mockito.when(userManager.findUser("test3")).thenReturn(revokedAccount);
    }
    
    @Test
    public void test_checkUpoadIdAccess_success() throws Throwable {
        prepareMethodCalls("UP123", "uploadId", null);
        UploadIdAccessCheck check =  createUploadAccessCheckAnnotation("uploadId");
        Mockito.when(joinPoint.proceed()).thenReturn("proceed");
        
        Object retObj = aspectToTest.checkUpoadIdAccess(joinPoint, check);
        Assert.assertEquals("proceed", retObj);
    }

    @Test
    public void test_checkUpoadIdAccess_notFound() throws Throwable {
        prepareMethodCalls("UP124", "uploadId", null);
        UploadIdAccessCheck check =  createUploadAccessCheckAnnotation("uploadId");
        Object retObj = aspectToTest.checkUpoadIdAccess(joinPoint, check);
        Assert.assertEquals("notFound", retObj);
    }
    
    @Test
    public void test_checkUploadIdAccess_forbidden() throws Throwable {
        prepareMethodCalls("UP456", "uploadId", null);
        UploadIdAccessCheck check =  createUploadAccessCheckAnnotation("uploadId");
        Object retObj = aspectToTest.checkUpoadIdAccess(joinPoint, check);
        Assert.assertEquals("forbidden", retObj);        
    }
    
    @Test
    public void test_checkFileAccess_success() throws Throwable {
        prepareMethodCalls("FI123", "fileId", null);
        Mockito.when(joinPoint.proceed()).thenReturn("proceed");
        
        FileAccessCheck check = createFileAccessCheckAnnotation("fileId");
        Object retObj = aspectToTest.checkFileAccess(joinPoint, check);
        Assert.assertEquals("proceed", retObj);
    }
    
    @Test
    public void test_checkFileAccess_notFound() throws Throwable {
        prepareMethodCalls("FI234", "fileId", null);
        FileAccessCheck check = createFileAccessCheckAnnotation("fileId");
        Object retObj = aspectToTest.checkFileAccess(joinPoint, check);
        Assert.assertEquals(new ResponseEntity<String>(HttpStatus.NOT_FOUND), retObj);
    }
    
    @Test
    public void test_checkFileAccess_forbidden() throws Throwable {
        prepareMethodCalls("FI456", "fileId", null);
        FileAccessCheck check = createFileAccessCheckAnnotation("fileId");
        Object retObj = aspectToTest.checkFileAccess(joinPoint, check);
        Assert.assertEquals(new ResponseEntity<String>(HttpStatus.FORBIDDEN), retObj);
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
    
    private UploadIdAccessCheck createUploadAccessCheckAnnotation(String parameterName) {
        UploadIdAccessCheck check = new UploadIdAccessCheck() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return UploadIdAccessCheck.class;
            }
            
            @Override
            public String value() {
                return parameterName;
            }
        };
        return check;
    }
    
    private FileAccessCheck createFileAccessCheckAnnotation(String parameterName) {
        FileAccessCheck check = new FileAccessCheck() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return FileAccessCheck.class;
            }
            
            @Override
            public String value() {
                return parameterName;
            }
        };
        
        return check;
    }

}
