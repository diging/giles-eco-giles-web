package edu.asu.diging.gilesecosystem.web.core.exceptions;

import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class ExceptionHandlingController {

    private static Logger logger = LoggerFactory
            .getLogger(ExceptionHandlingController.class);

    @ExceptionHandler({ SizeLimitExceededException.class,
            MaxUploadSizeExceededException.class })
    public ResponseEntity<String> fileTooBig(Exception ex) {
        logger.error("File too big.", ex);
        return new ResponseEntity<String>(
                "{\"error\": \"File upload limit exceeded.\"}",
                HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<String> handleHttpClient(RestClientResponseException e) {
        logger.error("Caught REST exception.", e);
        return new ResponseEntity<String>(HttpStatus.valueOf(e
                .getRawStatusCode()));
    }
    
    @ExceptionHandler(SearchException.class)
    public ResponseEntity<String> handleSearchException(SearchException e) {
        logger.error("Could not run query.", e);
        return new ResponseEntity<String>("{ \"error\" : \"" + e.getMessage() + "\" }", HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(NoFreddieInstanceConfigured.class)
    public ResponseEntity<String> handleFreddieNotConfigured(NoFreddieInstanceConfigured ex) {
        logger.error("Freddie is not configured.", ex);
        return new ResponseEntity<String>("{ \"error\" : \"No Freddie instance has been configured for this Giles instance.\" }", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Internal server error.")
    @ExceptionHandler(Exception.class)
    public void exception(Exception e) {
        logger.error("Exception handler caught:", e);
    }

}
