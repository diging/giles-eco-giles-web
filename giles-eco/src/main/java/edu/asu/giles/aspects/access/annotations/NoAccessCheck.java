package edu.asu.giles.aspects.access.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to indicate a specific controller method should not be
 * checked regarding user access.
 * 
 * @author jdamerow
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NoAccessCheck {

}
