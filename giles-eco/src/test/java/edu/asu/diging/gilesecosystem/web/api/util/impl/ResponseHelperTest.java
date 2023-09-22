package edu.asu.diging.gilesecosystem.web.api.util.impl;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.web.api.util.IResponseHelper;
import junit.framework.Assert;

public class ResponseHelperTest {
    @Mock
    private ISystemMessageHandler messageHandler;
    
    @InjectMocks 
    private IResponseHelper responseHelper;
    
    @Before
    public void setUp() {
        responseHelper = new ResponseHelper();
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void test_generateResponse_success() throws JSONException {
        Map<String, String> msgs = new HashMap<String, String>();
        msgs.put("id", "DOC12345");
        ResponseEntity<String> response = responseHelper.generateResponse(msgs, HttpStatus.OK);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        String expectedJson = "{\"id\" : \"DOC12345\"}";
        JSONAssert.assertEquals(expectedJson, response.getBody(), JSONCompareMode.LENIENT);
    }
    
    @Test
    public void test_generateResponse_whenPassingEmptyMessage() throws JSONException {
        Map<String, String> msgs = new HashMap<String, String>();
        ResponseEntity<String> response = responseHelper.generateResponse(msgs, HttpStatus.OK);
        String expectedJson = "{ }";
        JSONAssert.assertEquals(expectedJson, response.getBody(), JSONCompareMode.LENIENT);
    }
}
