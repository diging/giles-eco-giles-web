package edu.asu.diging.gilesecosystem.web.service.impl;

import java.lang.reflect.Field;

import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import edu.asu.diging.gilesecosystem.web.service.IPropertiesCopier;

@Service
public class PropertiesCopier implements IPropertiesCopier {

    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.impl.IPropertiesCopier#copyObject(java.lang.Object, java.lang.Object)
     */
    @Override
    public void copyObject(Object objectToCopy, Object copyInto) {
        ReflectionUtils.doWithFields(objectToCopy.getClass(), new FieldCallback() {
            
            @Override
            public void doWith(Field field) throws IllegalArgumentException,
                    IllegalAccessException {
                field.setAccessible(true);
                ReflectionUtils.setField(field, copyInto, ReflectionUtils.getField(field, objectToCopy));
            }
        });
    }
}
