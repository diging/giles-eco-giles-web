package edu.asu.giles.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.asu.giles.users.IUserManager;
import edu.asu.giles.users.User;
import edu.asu.giles.web.profile.forms.UsernameForm;

@Component
public class UsernameValidator implements Validator {

    @Autowired
    private IUserManager userManager;

    @Override
    public boolean supports(Class<?> arg0) {
        return arg0 == UsernameForm.class;
    }

    @Override
    public void validate(Object arg0, Errors arg1) {
        ValidationUtils.rejectIfEmptyOrWhitespace(arg1, "username", "username_required");
        
        String username = ((UsernameForm)arg0).getUsername();
        boolean isValid = username.matches("[A-z0-9_-]{5,}");
        
        if (!isValid) {
            arg1.rejectValue("username", "username_invalid");
        }
        
        User user = userManager.findUser(username);
        if (user != null) {
            arg1.rejectValue("username", "username_exists");
        }
    }
    
    
}
