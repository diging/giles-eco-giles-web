package edu.asu.diging.gilesecosystem.web.api.util;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.gilesecosystem.web.core.model.IDocument;

public interface IJSONHelper {

    public abstract void createDocumentJson(IDocument doc, ObjectMapper mapper,
            ObjectNode docNode);

    public abstract ResponseEntity<String> generateSimpleResponse(Map<String, String> msgs, HttpStatus status);

}