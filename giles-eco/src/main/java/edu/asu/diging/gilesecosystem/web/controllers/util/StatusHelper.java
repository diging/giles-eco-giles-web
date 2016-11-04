package edu.asu.diging.gilesecosystem.web.controllers.util;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;

@Service
public class StatusHelper {
    
    @Autowired
    private MessageSource messageSource;

    public String getLabelText(RequestStatus status, Locale locale) {
        String label = "upload_status_text_" + status.name().toLowerCase();
        return messageSource.getMessage(label, new String[]{}, locale);
    }
}
