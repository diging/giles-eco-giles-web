package edu.asu.diging.gilesecosystem.web.aspects.access;

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

import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.aspects.access.annotations.AccountCheck;
import edu.asu.diging.gilesecosystem.web.aspects.access.annotations.DocumentIdAccessCheck;
import edu.asu.diging.gilesecosystem.web.aspects.access.annotations.FileAccessCheck;
import edu.asu.diging.gilesecosystem.web.aspects.access.annotations.NoAccessCheck;
import edu.asu.diging.gilesecosystem.web.aspects.access.annotations.UploadIdAccessCheck;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IUpload;
import edu.asu.diging.gilesecosystem.web.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.users.AccountStatus;
import edu.asu.diging.gilesecosystem.web.users.IUserManager;
import edu.asu.diging.gilesecosystem.web.users.User;

@Aspect
@Component
public class SecurityAspect {

    private Logger logger = LoggerFactory.getLogger(SecurityAspect.class);

    @Autowired
    private IUserManager userManager;

    @Autowired
    private IFilesManager filesManager;
    
    @Around("within(edu.asu.diging.gilesecosystem.web.controllers..*) && @annotation(noCheck)")
    public Object doNotCheckUserAccess(ProceedingJoinPoint joinPoint,
            NoAccessCheck noCheck) throws Throwable {

        return joinPoint.proceed();
    }

    @Around("within(edu.asu.diging.gilesecosystem.web.controllers..*) && @annotation(uploadCheck)")
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
    
    @Around("within(edu.asu.diging.gilesecosystem.web.controllers..*) && @annotation(docCheck)")
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

    @Around("within(edu.asu.diging.gilesecosystem.web.controllers..*) && @annotation(fileCheck)")
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
    
    @Around("within(edu.asu.diging.gilesecosystem.web.controllers..*) && @annotation(check)")
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
