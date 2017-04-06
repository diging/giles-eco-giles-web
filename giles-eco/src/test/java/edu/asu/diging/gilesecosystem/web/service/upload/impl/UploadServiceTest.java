package edu.asu.diging.gilesecosystem.web.service.upload.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.DocumentType;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.impl.Document;
import edu.asu.diging.gilesecosystem.web.files.impl.StorageStatus;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.users.User;
import edu.asu.diging.gilesecosystem.web.util.FileUploadHelper;

public class UploadServiceTest {
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    

    @Mock
    private IPropertiesManager propManager;

    @Mock
    private FileUploadHelper uploadHelper;

    @InjectMocks
    private UploadService serviceToTest;

    @Before
    public void setUp() {
        serviceToTest = new UploadService();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_startUpload_many() throws InterruptedException {
        // set expiration
        Mockito.when(propManager.getProperty(Properties.EXPIRATION_TIME_UPLOADS_MS))
            .thenReturn("1");
        serviceToTest.init();

        // prepare
        List<Thread> threads = new ArrayList<Thread>();
        List<Runnable> failed = Collections.synchronizedList(new ArrayList<>());

        List<StorageStatus> statuses = new ArrayList<>();
        statuses.add(new StorageStatus(null, null, null, RequestStatus.SUBMITTED));

        for (int i = 0; i < 3000; i++) {
            User user = new User();
            user.setUsername("user" + i);
            Mockito.when(
                    uploadHelper.processUpload(DocumentAccess.PUBLIC,
                            DocumentType.MULTI_PAGE, null, null, user, "id")).thenReturn(
                    statuses);
            threads.add(new Thread(new UploadRunnable("user" + i, failed)));
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        logger.info("Non expired uploads: " + serviceToTest.countNonExpiredUpload());
        Assert.assertTrue(serviceToTest.countNonExpiredUpload() < 3000);
        Assert.assertEquals(0, failed.size());
    }

    class UploadRunnable implements Runnable {

        private String username;
        private List<Runnable> failed;

        public UploadRunnable(String username, List<Runnable> failed) {
            this.username = username;
            this.failed = failed;
        }

        @Override
        public void run() {
            User user = new User();
            user.setUsername(username);
            try {
                // let's put some randomness into the request timing
                TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(1, 10));

                serviceToTest.startUpload(DocumentAccess.PUBLIC, DocumentType.MULTI_PAGE,
                        null, null, user);
            } catch (Exception e) {
                e.printStackTrace();
                failed.add(this);
            }
        }

    }

    @Test
    public void test_updateStatus_many() throws InterruptedException {
        // set expiration
        Mockito.when(propManager.getProperty(Properties.EXPIRATION_TIME_UPLOADS_MS))
            .thenReturn("3600");
        serviceToTest.init();
        
        List<Thread> threads = new ArrayList<Thread>();
        List<Runnable> failed = Collections.synchronizedList(new ArrayList<>());

        List<String> docIds = new ArrayList<>();
        List<String> progressIds = new ArrayList<>();

        // prepare
        for (int i = 0; i < 100; i++) {
            List<StorageStatus> statuses = new ArrayList<>();

            for (int j = 0; j < ThreadLocalRandom.current().nextInt(1, 5); j++) {
                IDocument doc = new Document();
                doc.setId("DOC" + ThreadLocalRandom.current().nextInt(1, 1000));
                docIds.add(doc.getId());

                statuses.add(new StorageStatus(doc, null, null, RequestStatus.SUBMITTED));
            }

            User user = new User();
            user.setUsername("user");

            Mockito.when(
                    uploadHelper.processUpload(DocumentAccess.PRIVATE,
                            DocumentType.SINGLE_PAGE, null, null, user, "id")).thenReturn(
                    statuses);
            String progId = serviceToTest.startUpload(DocumentAccess.PRIVATE, DocumentType.SINGLE_PAGE,
                    null, null, user);
            progressIds.add(progId);
        }

        // now test updating the added uploads
        for (int i = 0; i < 300; i++) {

            String id = docIds.get(ThreadLocalRandom.current().nextInt(0,
                    docIds.size() - 1));
            threads.add(new Thread(new UpdateRunnable(id, failed)));
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        boolean atLeastOneSuccess = progressIds
                .stream()
                .anyMatch(
                        id -> serviceToTest
                                .getUpload(id)
                                .stream()
                                .allMatch(
                                        status -> status.getStatus() == RequestStatus.COMPLETE));
        Assert.assertTrue(atLeastOneSuccess);
        long statusLeft = progressIds.stream().filter(id -> serviceToTest
                                .getUpload(id)
                                .stream()
                                .allMatch(
                                        status -> status.getStatus() != RequestStatus.COMPLETE)).count();
        logger.info("Uploads left to be processed: " + statusLeft);
        Assert.assertEquals(0, failed.size());
    }

    class UpdateRunnable implements Runnable {

        private String documentId;
        private List<Runnable> failed;

        public UpdateRunnable(String documentId, List<Runnable> failed) {
            this.documentId = documentId;
            this.failed = failed;
        }

        @Override
        public void run() {
            try {
                // let's put some randomness into the request timing
                TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(1, 10));

                serviceToTest.updateStatus(documentId, RequestStatus.COMPLETE);
            } catch (Exception e) {
                e.printStackTrace();
                failed.add(this);
            }
        }

    }
}
