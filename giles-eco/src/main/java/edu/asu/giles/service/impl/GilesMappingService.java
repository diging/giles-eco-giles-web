package edu.asu.giles.service.impl;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.asu.giles.exceptions.GilesMappingException;
import edu.asu.giles.service.IGilesMappingService;

/**
 * Generic class for mapping objects from one type to another.
 * 
 * @author jdamerow
 *
 * @param <T1> The first type. To be mapped onto the second type.
 * @param <T2> The second type. To be mapped onto the first type.
 */
public class GilesMappingService<T1, T2> implements IGilesMappingService<T1, T2> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    /* (non-Javadoc)
     * @see edu.asu.giles.service.impl.IGilesMappingService#convert(T1, T2)
     */
    @Override
    public T2 convertToT2(T1 t1, T2 t2) throws GilesMappingException {
        Map<String, Field> t1FieldMap = new HashMap<String, Field>();
        Map<String, Field> t2FieldMap = new HashMap<String, Field>();
        
        List<Field> t1FieldsList = Arrays.asList(t1.getClass().getDeclaredFields());
        List<Field> t2FieldsList = Arrays.asList(t2.getClass().getDeclaredFields());
        
        t1FieldsList.forEach(field -> t1FieldMap.put(field.getName(), field));
        t2FieldsList.forEach(field -> t2FieldMap.put(field.getName(), field));
        
        mapFields(t1FieldMap, t2FieldMap, t1, t2);
        
        return t2;
    }
    
    @Override
    public T1 convertToT1(T1 t1, T2 t2) throws GilesMappingException {
        Map<String, Field> t1FieldMap = new HashMap<String, Field>();
        Map<String, Field> t2FieldMap = new HashMap<String, Field>();
        
        List<Field> t1FieldsList = Arrays.asList(t1.getClass().getDeclaredFields());
        List<Field> t2FieldsList = Arrays.asList(t2.getClass().getDeclaredFields());
        
        t1FieldsList.forEach(field -> t1FieldMap.put(field.getName(), field));
        t2FieldsList.forEach(field -> t2FieldMap.put(field.getName(), field));
        
        mapFields(t2FieldMap, t1FieldMap, t2, t1);
        
        return t1;
    }

    private void mapFields(Map<String, Field> t1FieldMap, Map<String, Field> t2FieldMap, Object t1, Object t2) throws GilesMappingException {
         
        for (String fieldName : t1FieldMap.keySet()) {
            if (t2FieldMap.containsKey(fieldName)) {
                try {
                    t1FieldMap.get(fieldName).setAccessible(true);
                    t2FieldMap.get(fieldName).setAccessible(true);
                    Object t1FieldValue = t1FieldMap.get(fieldName).get(t1);
                    t2FieldMap.get(fieldName).set(t2, t1FieldValue);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    logger.error("Could not process field: " + fieldName);
                    throw new GilesMappingException(e);
                }
            }
        }
    }
}
