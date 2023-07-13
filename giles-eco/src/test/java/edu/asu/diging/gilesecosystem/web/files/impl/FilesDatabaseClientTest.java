package edu.asu.diging.gilesecosystem.web.files.impl;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import edu.asu.diging.gilesecosystem.web.core.files.impl.FilesDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.repository.FileRepository;

public class FilesDatabaseClientTest {
    @Mock
    private FileRepository fileRepository;
    
    @InjectMocks
    private FilesDatabaseClient filesDatabaseClient;
    
    @Before
    public void setUp() {
        filesDatabaseClient = new FilesDatabaseClient(fileRepository);
        
    }
}
