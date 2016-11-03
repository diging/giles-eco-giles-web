package edu.asu.giles.web.util;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import edu.asu.giles.service.requests.RequestStatus;

@Service
public class StatusHelper {
    
    @Autowired
    private MessageSource messageSource;

    public String getLabelText(RequestStatus status, Locale locale) {
        String label = "upload_status_text_" + status.name().toLowerCase();
        return messageSource.getMessage(label, new String[]{}, locale);
    }
}
