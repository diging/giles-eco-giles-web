package edu.asu.giles.db4o.impl;

import java.util.Random;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

import edu.asu.giles.db4o.IDatabaseClient;
import edu.asu.giles.db4o.IStorableObject;
import edu.asu.giles.exceptions.UnstorableObjectException;

public abstract class DatabaseClient<T extends IStorableObject> implements IDatabaseClient<T> {

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.files.impl.IDatabaseClient#generateFileId()
     */
    @Override
    public String generateId() {
        String id = null;
        while (true) {
            id = getIdPrefix() + generateUniqueId();
            Object existingFile = getById(id);
            if (existingFile == null) {
                break;
            }
        }
        return id;
    }
    
    protected T queryByExampleGetFirst(T example) {
        ObjectSet<T> docs = getClient().queryByExample(example);
        if (docs != null && docs.size() > 0) {
            return docs.get(0);
        }
        return null;
    }
    
    @Override
    public T store(T element) throws UnstorableObjectException {
        if (element.getId() == null) {
            throw new UnstorableObjectException("The object does not have an id.");
        }
        
        ObjectContainer client = getClient();
        client.store(element);
        client.commit();
        return element;
    }
    
    @Override
    public void delete(T element) {
        ObjectContainer client = getClient();
        client.delete(element);
        client.commit();
    }

    protected abstract String getIdPrefix();

    protected abstract Object getById(String id);
    
    protected abstract ObjectContainer getClient();

    /**
     * This methods generates a new 6 character long id. Note that this method
     * does not assure that the id isn't in use yet.
     * 
     * Adapted from
     * http://stackoverflow.com/questions/9543715/generating-human-readable
     * -usable-short-but-unique-ids
     * 
     * @return 12 character id
     */
    protected String generateUniqueId() {
        char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
                .toCharArray();

        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            builder.append(chars[random.nextInt(62)]);
        }

        return builder.toString();
    }
}
