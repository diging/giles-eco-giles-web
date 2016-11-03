package edu.asu.giles.service.requests;

import edu.asu.giles.service.requests.impl.Request;

/**
 * Factory to create requests to be submitted to Kafka.
 * 
 * @author jdamerow
 *
 * @param <T> The type of {@link Request} this class handles.
 */
public interface IRequestFactory<T, V extends T> {

    public abstract T createRequest(String uploadId) throws InstantiationException,
            IllegalAccessException;

    public abstract void config(Class<V> classToInstantiate);

}