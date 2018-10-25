package edu.asu.diging.gilesecosystem.web.zookeeper.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.ZKPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.exceptions.NoNepomukFoundException;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.zookeeper.INepomukServiceDiscoverer;

@Service
public class NepomukServiceDiscoverer implements INepomukServiceDiscoverer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String znode;

    private CuratorFramework curatorFramework;

    @Autowired
    private IPropertiesManager propertiesManager;

    @Autowired
    private ISystemMessageHandler msgHandler;

    @PostConstruct
    public void init() {
        curatorFramework = CuratorFrameworkFactory.newClient(propertiesManager.getProperty(Properties.ZOOKEEPER_HOST)
                + ":" + propertiesManager.getProperty(Properties.ZOOKEEPER_PORT), new RetryNTimes(5, 1000));
        curatorFramework.start();

        znode = "/services/" + propertiesManager.getProperty(Properties.ZOOKEEPER_NEPOMUK_SERVICE_NAME);
        checkServices();
    }

    private void checkServices() {
        List<String> uris;
        try {
            uris = curatorFramework.getChildren().forPath(znode);
        } catch (Exception e) {
            // this is actually throwing Exception
            msgHandler.handleMessage("Could not get Nepomuk entries in Zookeeper.", e, MessageType.WARNING);
            return;
        }
        for (String uri : uris) {
            logger.debug("Found: " + uri);
            byte[] url;
            try {
                url = curatorFramework.getData().forPath(ZKPaths.makePath(znode, uri));
            } catch (Exception e) {
                // method is actually throwing exception
                msgHandler.handleMessage("Could not get Nepomuk URL from Zookeeper.", e, MessageType.ERROR);
                return;
            }
            if (url != null) {
                logger.debug("with URL: " + new String(url));
                msgHandler.handleMessage("Nepomuk instance found.",
                        "Giles found a Nepomuk instance at ZNode " + uri + " with URL " + new String(url),
                        MessageType.INFO);
            } else {
                msgHandler.handleMessage("No Nepomuk URL configured.",
                        "There was no Nepomuk instance found at " + new String(uri), MessageType.WARNING);
                logger.debug("There was no URL registered with the ZNode.");
            }
        }
    }

    @Override
    public String getRandomNepomukInstance() throws NoNepomukFoundException {
        // throws Exception
        List<String> uris;
        int randomInstance = 0;
        try {
            uris = curatorFramework.getChildren().forPath(znode);
            if ((uris != null) && (uris.size() > 0)) {
                randomInstance = ThreadLocalRandom.current().nextInt(0, uris.size());
                // error after randomInstance is generated
            }
        } catch (Exception e) {
            throw new NoNepomukFoundException(e);
        }

        for (int i = 0; i < uris.size(); i++) {
            // get random Nepomuk instance
            byte[] urlBytes;
            try {
                urlBytes = curatorFramework.getData().forPath(ZKPaths.makePath(znode, uris.get(randomInstance)));
            } catch (Exception e) {
                throw new NoNepomukFoundException(e);
            }
            if (urlBytes != null) {
                String url = new String(urlBytes);
                try {
                    if (validateUrl(url)) {
                        // if Nepomuk instance url specified and nepomuk responds with ok
                        // return url
                        return url;
                    }
                } catch (IOException e) {
                    msgHandler.handleMessage("Nepomuk URL could not be validated.", e, MessageType.WARNING);
                }
            }

            // if there is no URL or URL is not valid, let's take the next instance
            randomInstance++;
            // if we're at the end of the list, start from its beginning
            if (randomInstance >= uris.size()) {
                randomInstance -= uris.size();
            }
        }

        return null;
    }

    private boolean validateUrl(String url) throws IOException {
        URL myURL = new URL(url);
        HttpURLConnection myConnection = (HttpURLConnection) myURL.openConnection();

        try {
            if (myConnection.getResponseCode() == HttpStatus.OK.value()) {
                return true;
            } else {
                return false;
            }
        } finally {
            myConnection.disconnect();
        }
    }
}
