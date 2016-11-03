package edu.asu.giles.aspects.access.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AppTokenCheck {
    String value() default "accessToken";
    
    String providerToken() default "providerToken";
}
