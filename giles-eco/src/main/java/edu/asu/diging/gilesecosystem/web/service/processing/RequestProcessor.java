package edu.asu.diging.gilesecosystem.web.service.processing;

import edu.asu.diging.gilesecosystem.requests.IRequest;


public interface RequestProcessor<T extends IRequest> {

    public String getProcessedTopic();
    
    public default void handleRequest(IRequest request) {
        processRequest((T)request); 
    }
    
    public void processRequest(T request);
    
    public Class<? extends T> getRequestClass();
    
}
