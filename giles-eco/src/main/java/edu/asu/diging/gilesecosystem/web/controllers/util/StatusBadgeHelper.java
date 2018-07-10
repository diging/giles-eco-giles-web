package edu.asu.diging.gilesecosystem.web.controllers.util;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.IImageExtractionRequest;
import edu.asu.diging.gilesecosystem.requests.IOCRRequest;
import edu.asu.diging.gilesecosystem.requests.ITextExtractionRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.controllers.pages.Badge;
import edu.asu.diging.gilesecosystem.web.controllers.pages.DocumentPageBean;
import edu.asu.diging.gilesecosystem.web.domain.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.domain.ITask;
import edu.asu.diging.gilesecosystem.web.domain.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.util.IStatusHelper;

@Service
public class StatusBadgeHelper {

    @Autowired
    private IPropertiesManager propertiesManager;

    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private IStatusHelper statusHelper;

    public String getLabelText(RequestStatus status, Locale locale) {
        String label = "upload_status_text_"
                + (status != null ? status.name().toLowerCase() : "default");
        return messageSource.getMessage(label, new String[] {}, locale);
    }

    public String getProcessText(ProcessingStatus status, Locale locale) {
        String label = "processing_status_text_" + status.name().toLowerCase();
        return messageSource.getMessage(label, new String[] {}, locale);
    }

    public void createBadges(DocumentPageBean docBean,
            List<IProcessingRequest> procRequests) {
        // create text extraction badges
        if (procRequests.stream()
                .filter(preq -> preq.getSentRequest() instanceof ITextExtractionRequest)
                .count() > 0) {
            RequestStatus status = statusHelper.getProcessingPhaseResult(ITextExtractionRequest.class, procRequests);
            docBean.getBadges()
                .add(new Badge(
                    propertiesManager
                            .getProperty(Properties.BADGE_TEXT_EXTRACTION_SUBJECT),
                    propertiesManager
                            .getProperty(Properties.BADGE_STATUS_PREFIX + status.name().toLowerCase()),
                    propertiesManager
                            .getProperty(Properties.BADGE_TEXT_EXTRACTION_COLOR),
                    1));
        }
        // create image extraction badges
        if (procRequests.stream()
                .filter(preq -> preq.getSentRequest() instanceof IImageExtractionRequest)
                .count() > 0) {
            RequestStatus status = statusHelper.getProcessingPhaseResult(IImageExtractionRequest.class, procRequests);
            docBean.getBadges()
                .add(new Badge(
                    propertiesManager
                            .getProperty(Properties.BADGE_IMAGE_EXTRACTION_SUBJECT),
                    propertiesManager
                            .getProperty(Properties.BADGE_STATUS_PREFIX + status.name().toLowerCase()),
                    propertiesManager
                            .getProperty(Properties.BADGE_IMAGE_EXTRACTION_COLOR),
                    0));
        }
        
        // create ocr badges
        if (procRequests.stream()
                .filter(preq -> preq.getSentRequest() instanceof IOCRRequest)
                .count() > 0) {
            RequestStatus status = statusHelper.getProcessingPhaseResult(IOCRRequest.class, procRequests);
            docBean.getBadges()
                .add(new Badge(
                    propertiesManager
                            .getProperty(Properties.BADGE_OCR_SUBJECT),
                    propertiesManager
                            .getProperty(Properties.BADGE_STATUS_PREFIX + status.name().toLowerCase()),
                    propertiesManager
                            .getProperty(Properties.BADGE_OCR_COLOR),
                    2));
        }
        
        docBean.getBadges().sort((b1, b2) -> b1.getOrder() - b2.getOrder());
    }
    
    public void createExternalBadges(DocumentPageBean docBean) {
        for(ITask task : docBean.getTasks()) {
            docBean.getExternalBadges().add(new Badge(
                    propertiesManager.getProperty(propertiesManager.getProperty(Properties.EXTERNAL_BADGE_PREFIX) + task.getTaskHandlerId()),
                    propertiesManager.getProperty(Properties.BADGE_STATUS_PREFIX + task.getStatus().name().toLowerCase()),
                    propertiesManager.getProperty(propertiesManager.getProperty(Properties.EXTERNAL_BADGE_COLOR_PREFIX) + task.getTaskHandlerId()),
                    1,
                    task.getFileId()
                    ));
        }
    }

}
