package edu.asu.giles.service.upload.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.asu.giles.core.DocumentAccess;
import edu.asu.giles.core.DocumentType;
import edu.asu.giles.files.impl.StorageStatus;
import edu.asu.giles.service.properties.IPropertiesManager;
import edu.asu.giles.service.upload.IUploadService;

@Service
public class UploadService implements IUploadService {
    
    private final static long DEFAULT_EXPIRATION = 24 * 60 * 60 * 1000L;

    private Map<String, Future<List<StorageStatus>>> currentUploads;
    private Map<Long, List<String>> expirationList;

    @Autowired
    private UploadThread uploadThread;

    @Autowired
    private IPropertiesManager propertiesManager;

    private long expirationMiliseconds;

    @PostConstruct
    public void init() {
        currentUploads = Collections.synchronizedMap(new HashMap<>());
        expirationList = Collections.synchronizedSortedMap(new TreeMap<>());

        String expirationProp = propertiesManager
                .getProperty(IPropertiesManager.EXPIRATION_TIME_UPLOADS_MS);
        expirationMiliseconds = expirationProp != null && !expirationProp.trim().isEmpty() ? new Long(expirationProp) : new Long(DEFAULT_EXPIRATION);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.service.upload.impl.IUploadService#startUpload(edu.asu.
     * giles.core.DocumentAccess, edu.asu.giles.core.DocumentType,
     * org.springframework.web.multipart.MultipartFile[], java.lang.String)
     */
    @Override
    public String startUpload(DocumentAccess access, DocumentType type,
            MultipartFile[] files, List<byte[]> fileBytes, String username) {
        // clean out old uploads before adding new
        cleanUp();

        String uploadProgressId = generateId();
        currentUploads.put(uploadProgressId,
                uploadThread.runUpload(access, type, files, fileBytes, username));

        Long time = new Date().getTime();
        // make sure only one thread creates array list and adds to it
        synchronized (expirationList) {
            if (expirationList.get(time) == null) {
                expirationList.put(time, new ArrayList<>());
            }
            expirationList.get(time).add(uploadProgressId);
        }

        return uploadProgressId;
    }

    public void cleanUp() {
        List<Long> remove = new ArrayList<Long>();
        Set<Long> keySet = expirationList.keySet();

        synchronized (expirationList) {
            for (Long expirationTime : keySet) {
                boolean expired = Math.abs(expirationTime - new Date().getTime()) > expirationMiliseconds;

                if (expired) {
                    remove.add(expirationTime);
                } else {
                    // since expirationList is sorted, we know we can stop here
                    break;
                }
            }

            for (Long expTime : remove) {
                expirationList.get(expTime).forEach(id -> currentUploads.remove(id));
                expirationList.remove(expTime);
            }
        }
    }

    @Override
    public Future<List<StorageStatus>> getUpload(String id) {
        return currentUploads.get(id);
    }

    @Override
    public long countNonExpiredUpload() {
        return currentUploads.size();
    }

    protected String generateId() {
        String id = null;
        while (true) {
            id = "PROG" + generateUniqueId();
            Object existingFile = currentUploads.get(id);
            if (existingFile == null) {
                break;
            }
        }
        return id;
    }

    /**
     * This methods generates a new 6 character long id. Note that this method
     * does not assure that the id isn't in use yet.
     * 
     * Adapted from
     * http://stackoverflow.com/questions/9543715/generating-human-readable
     * -usable-short-but-unique-ids
     * 
     * @return 6 character id
     */
    protected String generateUniqueId() {
        char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
                .toCharArray();

        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            builder.append(chars[random.nextInt(62)]);
        }

        return builder.toString();
    }
}
