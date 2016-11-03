package edu.asu.giles.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import edu.asu.giles.service.IFileHandlerRegistry;
import edu.asu.giles.service.IFileTypeHandler;

@Service
public class FileHandlerRegistry implements IFileHandlerRegistry {

    @Autowired
    private ApplicationContext ctx;
    
    private Map<String, IFileTypeHandler> handlers;
    
    @PostConstruct
    public void init() {
        handlers = new HashMap<String, IFileTypeHandler>();
        
        Map<String, IFileTypeHandler> ctxMap = ctx.getBeansOfType(IFileTypeHandler.class);
        Iterator<Entry<String, IFileTypeHandler>> iter = ctxMap.entrySet().iterator();
        
        while(iter.hasNext()){
            Entry<String, IFileTypeHandler> handlerEntry = iter.next();
            IFileTypeHandler handler = (IFileTypeHandler) handlerEntry.getValue();
            for (String type : handler.getHandledFileTypes()) {
                handlers.put(type, handler);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see edu.asu.giles.service.impl.IFileHandlerRegistry#getHandler(java.lang.String)
     */
    @Override
    public IFileTypeHandler getHandler(String contentType) {
        IFileTypeHandler handler = handlers.get(contentType);
        if (handler == null) {
            handler = handlers.get(IFileTypeHandler.DEFAULT_HANDLER);
        }
        return handler;
    }
    
}
