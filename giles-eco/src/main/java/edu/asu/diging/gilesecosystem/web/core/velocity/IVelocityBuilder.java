package edu.asu.diging.gilesecosystem.web.core.velocity;

import java.util.Map;

import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * The purpose of this interface is to update template with provided data.
 * 
 * @author snilapwa
 */
public interface IVelocityBuilder {

    String getRenderedTemplate(String templateName, Map<String, Object> contextProperties)
            throws ResourceNotFoundException, ParseErrorException, Exception;
}
