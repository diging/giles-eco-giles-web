package edu.asu.diging.gilesecosystem.web.config.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.web.config.IReloadService;

@Service
public class ReloadService implements IReloadService {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ApplicationContext applicationContext;
    
    private AtomicBoolean refreshComplete;

    @PostConstruct
    public void init() {
        refreshComplete = new AtomicBoolean(true);
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.config.impl.IConnectionFactoryService#loadConnectionFactories()
     */
    @Override
    public void reloadApplicationContext() {
        refreshComplete.set(false);
        ((ConfigurableApplicationContext)applicationContext).refresh();
    }
    
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        refreshComplete.set(true);
        logger.info("Context has been refreshed.");
    }
}
