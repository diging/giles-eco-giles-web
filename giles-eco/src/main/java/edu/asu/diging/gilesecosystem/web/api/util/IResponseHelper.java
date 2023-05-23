package edu.asu.diging.gilesecosystem.web.api.util;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface IResponseHelper {
    public abstract ResponseEntity<String> generateResponse(Map<String, String> msgs, HttpStatus status);
}
