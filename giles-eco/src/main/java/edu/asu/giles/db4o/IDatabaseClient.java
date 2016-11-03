package edu.asu.giles.db4o;

import com.db4o.ObjectContainer;

import edu.asu.giles.exceptions.UnstorableObjectException;


public interface IDatabaseClient<T extends IStorableObject> {

    public abstract String generateId();

    public abstract T store(T element) throws UnstorableObjectException;

    public abstract void delete(T element);

}