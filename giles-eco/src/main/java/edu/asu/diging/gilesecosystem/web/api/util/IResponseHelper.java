package edu.asu.diging.gilesecosystem.web.api.util;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface IResponseHelper {
    /**
     * Generates a ResponseEntity with a JSON body based on the given map of messages and HTTP status.
     * @param msgs a map of messages where the keys represent message identifiers and the values represent message content
     * @param status the HTTP status code to be set in the response
     * @return a ResponseEntity object with JSON content and the specified HTTP status
    */
    public abstract ResponseEntity<String> generateResponse(Map<String, String> msgs, HttpStatus status);
}
