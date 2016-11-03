package edu.asu.giles.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.asu.giles.apps.impl.RegisteredApp;

public class RegisteredAppValidator implements Validator {

    @Override
    public boolean supports(Class<?> arg0) {
        return arg0 == RegisteredApp.class;
    }

    @Override
    public void validate(Object arg0, Errors arg1) {
        ValidationUtils.rejectIfEmpty(arg1, "name", "app_name_required");
        ValidationUtils.rejectIfEmpty(arg1, "providerId", "app_providerId_required");
    }

}
