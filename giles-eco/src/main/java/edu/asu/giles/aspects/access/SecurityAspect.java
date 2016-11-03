package edu.asu.giles.aspects.access;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import edu.asu.giles.aspects.access.annotations.AccountCheck;
import edu.asu.giles.aspects.access.annotations.DocumentIdAccessCheck;
import edu.asu.giles.aspects.access.annotations.FileAccessCheck;
import edu.asu.giles.aspects.access.annotations.NoAccessCheck;
import edu.asu.giles.aspects.access.annotations.UploadIdAccessCheck;
import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IUpload;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.service.properties.IPropertiesManager;
import edu.asu.giles.users.AccountStatus;
import edu.asu.giles.users.IUserManager;
import edu.asu.giles.users.User;

@Aspect
@Component
public class SecurityAspect {

    private Logger logger = LoggerFactory.getLogger(SecurityAspect.class);

    @Autowired
    private IUserManager userManager;

    @Autowired
    private IFilesManager filesManager;
    
    @Autowired
    private IPropertiesManager propertiesManager;
    
//    @Autowired
//    private GitHubTemplateFactory templateFactory;
    

    @Around("within(edu.asu.giles.web..*) && @annotation(noCheck)")
    public Object doNotCheckUserAccess(ProceedingJoinPoint joinPoint,
            NoAccessCheck noCheck) throws Throwable {

        return joinPoint.proceed();
    }

    @Around("within(edu.asu.giles.web..*) && @annotation(uploadCheck)")
    public Object checkUpoadIdAccess(ProceedingJoinPoint joinPoint,
            UploadIdAccessCheck uploadCheck) throws Throwable {
        
        String uploadId = getRequestParameter(joinPoint, uploadCheck.value());

        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        User user = (User) auth.getPrincipal();

        IUpload upload = filesManager.getUpload(uploadId);
        if (upload == null) {
            return "notFound";
        }

        if (!upload.getUsername().equals(user.getUsername())) {
            return "forbidden";
        }

        return joinPoint.proceed();
    }
    
    @Around("within(edu.asu.giles.web..*) && @annotation(docCheck)")
    public Object checkDocumentIdAccess(ProceedingJoinPoint joinPoint, DocumentIdAccessCheck docCheck) throws Throwable {
        String docId = getRequestParameter(joinPoint, docCheck.value());
        
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        User user = (User) auth.getPrincipal();

        IDocument doc = filesManager.getDocument(docId);
        if (doc == null) {
            return "notFound";
        }

        if (doc.getUsername() != null) {
            if (!doc.getUsername().equals(user.getUsername())) {
                return "forbidden";
            }
        }

        return joinPoint.proceed();
    }

    @Around("within(edu.asu.giles.web..*) && @annotation(fileCheck)")
    public Object checkFileAccess(ProceedingJoinPoint joinPoint,
            FileAccessCheck fileCheck) throws Throwable {
        String fileId = getRequestParameter(joinPoint, fileCheck.value());
        
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        User user = (User) auth.getPrincipal();

        IFile file = filesManager.getFile(fileId);
        if (file == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        if (!file.getUsername().equals(user.getUsername())) {
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }

        return joinPoint.proceed();
    }
    
    private String getRequestParameter(ProceedingJoinPoint joinPoint,
            String parameterName) {
        Object[] args = joinPoint.getArgs();
        MethodSignature sig = (MethodSignature) joinPoint.getSignature();
        String[] argNames = sig.getParameterNames();

        String value = null;
        for (int i = 0; i < argNames.length; i++) {
            if (argNames[i].equals(parameterName)) {
                value = (String) args[i];
            }
        }
        return value;
    }
    
    @Around("within(edu.asu.giles.web..*) && @annotation(check)")
    public Object checkAccount(ProceedingJoinPoint joinPoint, AccountCheck check) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        User user = (User) auth.getPrincipal();
        if (user.getAccountStatus() != AccountStatus.APPROVED) {
            return "forbidden";
        }
        return joinPoint.proceed();
    }

    
}
