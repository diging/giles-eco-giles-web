package edu.asu.diging.gilesecosystem.web.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.asu.diging.gilesecosystem.web.controllers.admin.pages.AdminUser;
import edu.asu.diging.gilesecosystem.web.users.AdminUserManager;

@Component
public class AdminPasswordValidator implements Validator {
    
    @Autowired
    private AdminUserManager adminManager;

    @Override
    public boolean supports(Class<?> arg0) {
        return AdminUser.class.isAssignableFrom(arg0);
    }

    @Override
    public void validate(Object arg0, Errors arg1) {
        AdminUser user = (AdminUser) arg0;
        boolean passwordValid = adminManager.isPasswordValid(user.getUsername(), user.getOldPassword());
        
        if (!passwordValid) {
            arg1.rejectValue("oldPassword", "admin_user_old_password_incorrect");
        }
        ValidationUtils.rejectIfEmpty(arg1, "username", "admin_user_username_missing");
        ValidationUtils.rejectIfEmpty(arg1, "oldPassword", "admin_user_old_password_missing");
        ValidationUtils.rejectIfEmpty(arg1, "newPassword", "admin_user_new_password_missing");
        ValidationUtils.rejectIfEmpty(arg1, "retypedPassword", "admin_user_retyped_password_missing");
        
        if (!user.getNewPassword().equals(user.getRetypedPassword())) {
            arg1.rejectValue("retypedPassword", "admin_user_new_password_mismatch");
        }
    }

}
