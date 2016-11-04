package edu.asu.diging.gilesecosystem.web.db4o;

import com.db4o.ObjectContainer;

import edu.asu.diging.gilesecosystem.web.exceptions.UnstorableObjectException;


public interface IDatabaseClient<T extends IStorableObject> {

    public abstract String generateId();

    public abstract T store(T element) throws UnstorableObjectException;

    public abstract void delete(T element);

}