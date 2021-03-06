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
import edu.asu.diging.gilesecosystem.web.core.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.core.files.impl.StorageStatus;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentType;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalUploadService;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.core.service.upload.IUploadService;
import edu.asu.diging.gilesecosystem.web.core.service.upload.impl.UploadService;
import edu.asu.diging.gilesecosystem.web.core.users.User;
import edu.asu.diging.gilesecosystem.web.core.util.FileUploadHelper;

public class UploadServiceTest {
    
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Mock
    private IPropertiesManager propManager;

    @Mock
    private FileUploadHelper uploadHelper;
    
    @Mock
    private IFilesManager filesManager;
    
    @Mock
    private ITransactionalUploadService uploadService;

    @InjectMocks
    private IUploadService serviceToTest;

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

}
