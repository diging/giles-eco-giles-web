package edu.asu.diging.gilesecosystem.web.web.util;

import java.util.ArrayList;
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
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.model.ITask;
import edu.asu.diging.gilesecosystem.web.core.model.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.core.util.IStatusHelper;
import edu.asu.diging.gilesecosystem.web.web.pages.Badge;
import edu.asu.diging.gilesecosystem.web.web.pages.DocumentPageBean;

@Service
public class StatusBadgeHelper {

    @Autowired
    private IPropertiesManager propertiesManager;

    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private IStatusHelper statusHelper;
    
    public static final String DELETION_PREFIX = "DELREQ";

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
            List<IProcessingRequest> procRequests, IDocument document) {
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
        if (document.getRequestId() != null && document.getRequestId().startsWith(DELETION_PREFIX)) {
            System.out.println("Diya Test");
            docBean.getBadges()
            .add(new Badge(
                propertiesManager
                        .getProperty(Properties.BADGE_DELETION_SUBJECT),
                propertiesManager
                        .getProperty(Properties.BADGE_DELETION_STATUS),
                propertiesManager
                        .getProperty(Properties.BADGE_DELETION_COLOR),
                3));
        }
        docBean.getBadges().sort((b1, b2) -> b1.getOrder() - b2.getOrder());
    }
    
    public void createExternalBadges(DocumentPageBean docBean) {
        List<String> addedHandlers = new ArrayList<>();
        for(ITask task : docBean.getTasks()) {
            if (!addedHandlers.contains(task.getTaskHandlerId())) {
                docBean.getExternalBadges().add(new Badge(
                    propertiesManager.getProperty(propertiesManager.getProperty(Properties.EXTERNAL_BADGE_PREFIX) + task.getTaskHandlerId()),
                    propertiesManager.getProperty(Properties.BADGE_STATUS_PREFIX + task.getStatus().name().toLowerCase()),
                    propertiesManager.getProperty(propertiesManager.getProperty(Properties.EXTERNAL_BADGE_COLOR_PREFIX) + task.getTaskHandlerId()),
                    1,
                    task.getFileId()
                    ));
                addedHandlers.add(task.getTaskHandlerId());
            }
        }
    }

}
