package edu.asu.diging.gilesecosystem.web.core.service.impl;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.service.IProcessingRequestService;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;

/**
 * Not thread-safe implementation of {@link IProcessingRequestService} using a {@link Queue}.
 * 
 * The number of maximum requests in the queue can be defined using the 
 * property: current_requests_max_number.
 * 
 * @author jdamerow
 *
 */
@Service
public class ProcessingRequestService implements IProcessingRequestService {
    
    @Autowired
    private IPropertiesManager propertyManager; 

    private Queue<TimestampedRequestData> currentReceivedRequests;
    
    private Queue<TimestampedRequestData> currentSentRequests;
    
    @PostConstruct
    public void init() {
        currentReceivedRequests = new ConcurrentLinkedQueue<>();
        currentSentRequests = new ConcurrentLinkedQueue<>();
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.impl.IProcessingRequestService#addReceivedRequest(edu.asu.diging.gilesecosystem.requests.IRequest)
     */
    @Override
    public void addReceivedRequest(IRequest request) {
        if (currentReceivedRequests.size() > new Integer(propertyManager.getProperty(Properties.CURRENT_REQUESTS_MAX))) {
            currentReceivedRequests.poll();
        }
        currentReceivedRequests.add(new TimestampedRequestData(request, ZonedDateTime.now()));
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.impl.IProcessingRequestService#getCurrentReceivedRequests()
     */
    @Override
    public List<TimestampedRequestData> getCurrentReceivedRequests() {
        return new ArrayList<>(currentReceivedRequests);
    }
    
    @Override
    public void addSentRequest(IRequest request) {
        if (currentSentRequests.size() > new Integer(propertyManager.getProperty(Properties.CURRENT_REQUESTS_MAX))) {
            currentSentRequests.poll();
        }
        currentSentRequests.add(new TimestampedRequestData(request, ZonedDateTime.now()));
    }
    
    @Override
    public List<TimestampedRequestData> getCurrentSentRequests() {
        return new ArrayList<>(currentSentRequests);
    }
}
