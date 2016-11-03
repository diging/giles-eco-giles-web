package edu.asu.giles.service.requests.impl;

import org.springframework.stereotype.Service;

import edu.asu.giles.exceptions.MisconfigurationException;
import edu.asu.giles.service.requests.IRequest;
import edu.asu.giles.service.requests.IRequestFactory;
import edu.asu.giles.service.requests.RequestStatus;

@Service
public class RequestFactory<T extends IRequest, V extends T> implements IRequestFactory<T, V> {
    
    private Class<V> classToInstantiate;
    
    @Override
    public void config(Class<V> classToInstantiate) {
        this.classToInstantiate = classToInstantiate;
    }
    
    /* (non-Javadoc)
     * @see edu.asu.giles.service.kafka.requests.IRequestFactory#createRequest(java.lang.String)
     */
    @Override
    public T createRequest(String uploadId) throws InstantiationException, IllegalAccessException {
        if (classToInstantiate == null) {
            throw new MisconfigurationException("RequestFactory is not properly configured and doesn't know what objects to create.");
        }
        T request = classToInstantiate.newInstance();
        request.setUploadId(uploadId);
        request.setStatus(RequestStatus.NEW);
        return request;
    }
}
