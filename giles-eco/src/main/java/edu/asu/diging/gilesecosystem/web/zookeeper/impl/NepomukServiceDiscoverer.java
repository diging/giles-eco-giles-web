package edu.asu.diging.gilesecosystem.web.zookeeper.impl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.ZKPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;

@Service
public class NepomukServiceDiscoverer {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String znode;

    private CuratorFramework curatorFramework;
    
    @Autowired
    private IPropertiesManager propertiesManager;
    
    @Autowired
    private ISystemMessageHandler msgHandler;

    @PostConstruct
    public void init() throws Exception {
        curatorFramework = CuratorFrameworkFactory
                .newClient(propertiesManager.getProperty(Properties.ZOOKEEPER_HOST) 
                        + ":" 
                        + propertiesManager.getProperty(Properties.ZOOKEEPER_PORT) , new RetryNTimes(5, 1000));
        curatorFramework.start();
        
        znode = "/services/" + propertiesManager.getProperty(Properties.ZOOKEEPER_NEPOMUK_SERVICE_NAME);
        checkServices();
    }
    
    private void checkServices() throws Exception {
        List<String> uris = curatorFramework.getChildren().forPath(znode);
        for (String uri : uris) {
            logger.debug("Found: " + uri);
            byte[] url = curatorFramework.getData().forPath(ZKPaths.makePath(znode, uri));
            if (url != null) {
                logger.debug("with URL: " + new String(url));
                msgHandler.handleMessage("Nepomuk instance found.", "Giles found a Nepomuk instance at ZNode " + uri + " with URL " + new String(url), MessageType.INFO);
            } else {
                msgHandler.handleMessage("No Nepomuk URL configured.", "There was no Nepomuk instance found at " + new String(uri), MessageType.WARNING);
                logger.debug("There was no URL registered with the ZNode.");
            }          
        }      
    }
}
