package edu.asu.diging.gilesecosystem.web.controllers.util;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.IImageExtractionRequest;
import edu.asu.diging.gilesecosystem.requests.IOCRRequest;
import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.requests.ITextExtractionRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.requests.impl.ImageExtractionRequest;
import edu.asu.diging.gilesecosystem.requests.impl.OCRRequest;
import edu.asu.diging.gilesecosystem.requests.impl.TextExtractionRequest;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.controllers.pages.Badge;
import edu.asu.diging.gilesecosystem.web.controllers.pages.DocumentPageBean;
import edu.asu.diging.gilesecosystem.web.core.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;

@Service
public class StatusHelper {

    @Autowired
    private IPropertiesManager propertiesManager;

    @Autowired
    private MessageSource messageSource;

    public String getLabelText(RequestStatus status, Locale locale) {
        String label = "upload_status_text_"
                + (status != null ? status.name().toLowerCase() : "default");
        return messageSource.getMessage(label, new String[] {}, locale);
    }

    public String getProcessText(ProcessingStatus status, Locale locale) {
        String label = "processing_status_text_" + status.name().toLowerCase();
        return messageSource.getMessage(label, new String[] {}, locale);
    }

    public void setProcessingLabel(DocumentPageBean docBean,
            List<IProcessingRequest> procRequests, Locale locale) {
        if (procRequests.stream().allMatch(
                preq -> preq.getRequestStatus() == RequestStatus.COMPLETE)) {
            docBean.setStatusLabel(getLabelText(RequestStatus.COMPLETE, locale));
        } else if (procRequests.stream().anyMatch(
                preq -> preq.getRequestStatus() == RequestStatus.SUBMITTED
                        || preq.getRequestStatus() == RequestStatus.NEW)) {
            docBean.setStatusLabel(getLabelText(RequestStatus.SUBMITTED, locale));
        }

        if (procRequests.stream()
                .filter(preq -> preq.getSentRequest() instanceof TextExtractionRequest)
                .count() > 0
                && procRequests
                        .stream()
                        .filter(preq -> preq.getSentRequest() instanceof TextExtractionRequest)
                        .allMatch(
                                preq -> preq.getRequestStatus() == RequestStatus.COMPLETE)) {
            docBean.setProcessingLabel(getProcessText(
                    ProcessingStatus.TEXT_EXTRACTION_COMPLETE, locale));
        }
        if (procRequests.stream()
                .filter(preq -> preq.getSentRequest() instanceof ImageExtractionRequest)
                .count() > 0
                && procRequests
                        .stream()
                        .filter(preq -> preq.getSentRequest() instanceof ImageExtractionRequest)
                        .allMatch(
                                preq -> preq.getRequestStatus() == RequestStatus.COMPLETE)) {
            docBean.setProcessingLabel(getProcessText(
                    ProcessingStatus.IMAGE_EXTRACTION_COMPLETE, locale));
        }
        if (procRequests.stream()
                .filter(preq -> preq.getSentRequest() instanceof OCRRequest).count() > 0
                && procRequests
                        .stream()
                        .filter(preq -> preq.getSentRequest() instanceof OCRRequest)
                        .allMatch(
                                preq -> preq.getRequestStatus() == RequestStatus.COMPLETE)) {
            docBean.setProcessingLabel(getProcessText(ProcessingStatus.OCR_COMPLETE,
                    locale));
        }
    }

    public void createBadges(DocumentPageBean docBean,
            List<IProcessingRequest> procRequests) {
        // create text extraction badges
        if (procRequests.stream()
                .filter(preq -> preq.getSentRequest() instanceof ITextExtractionRequest)
                .count() > 0) {
            RequestStatus status = calculateStatus(procRequests, ITextExtractionRequest.class);
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
            RequestStatus status = calculateStatus(procRequests, IImageExtractionRequest.class);
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
            RequestStatus status = calculateStatus(procRequests, IOCRRequest.class);
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

    private RequestStatus calculateStatus(List<IProcessingRequest> procRequests, Class<? extends IRequest> requestClass) {
        RequestStatus status = RequestStatus.SUBMITTED;
        if (procRequests
                .stream()
                .filter(preq -> requestClass.isAssignableFrom(preq.getSentRequest().getClass()))
                .allMatch(
                        preq -> preq.getRequestStatus() == RequestStatus.COMPLETE)) {
            status = RequestStatus.COMPLETE;
        } else if (procRequests
                .stream()
                .filter(preq -> requestClass.isAssignableFrom(preq.getSentRequest().getClass()))
                .anyMatch(
                        preq -> preq.getRequestStatus() == RequestStatus.FAILED)) {
            status = RequestStatus.FAILED;
        }
        return status;
    }
}
