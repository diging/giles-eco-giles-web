package edu.asu.diging.gilesecosystem.web.service.upload.impl;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.service.upload.impl.UploadService;

public class UploadServiceTest {
    
    @Mock private IPropertiesManager propManager;
    
    @InjectMocks private UploadService serviceToTest;
    
    @Before
    public void setUp() {
        serviceToTest = new UploadService();
        MockitoAnnotations.initMocks(this);
        
        Mockito.when(propManager.getProperty(Properties.EXPIRATION_TIME_UPLOADS_MS)).thenReturn("1");
        serviceToTest.init();
        
    }
    
//    @Test
//    public void test_startUpload_many() throws InterruptedException {
//       List<Thread> threads = new ArrayList<Thread>();
//       List<Runnable> failed = Collections.synchronizedList(new ArrayList<>());
//        for (int i = 0; i<3000; i++) {
//            Mockito.when(uploadThread.runUpload(DocumentAccess.PUBLIC, DocumentType.MULTI_PAGE, null, null, "user"+i)).thenReturn(new BasicFuture<>(null));
//            threads.add(new Thread(new UploadRunnable("user"+i, failed)));
//        }
//        
//        for (Thread t : threads) {
//            t.start();;
//        }
//        
//        for (Thread t : threads) {
//            t.join();
//        }
//        
//        System.out.println(serviceToTest.countNonExpiredUpload());
//        Assert.assertTrue(serviceToTest.countNonExpiredUpload() < 3000);
//        Assert.assertEquals(0, failed.size());
//    }
//
//    class UploadRunnable implements Runnable {
//        
//        private String username;
//        private List<Runnable> failed;
//        
//        public UploadRunnable(String username, List<Runnable> failed) {
//            this.username = username;
//            this.failed = failed;
//        }
//
//        @Override
//        public void run() {
//            try {
//                serviceToTest.startUpload(DocumentAccess.PUBLIC, DocumentType.MULTI_PAGE, null, null, username);
//            } catch (Exception e) {
//                e.printStackTrace();
//                failed.add(this);
//            }
//        }
//        
//    }
}
