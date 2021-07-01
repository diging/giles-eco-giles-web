package edu.asu.diging.gilesecosystem.web.web.admin;

import java.util.Collections;
import java.util.List;

import org.bouncycastle.asn1.cms.TimeStampedDataParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.gilesecosystem.web.core.service.IProcessingRequestService;
import edu.asu.diging.gilesecosystem.web.core.service.impl.TimestampedRequestData;

@Controller
public class CurrentRequestsController {

    @Autowired
    private IProcessingRequestService requestService;
    
    @RequestMapping("admin/requests/current")
    public String showCurrentRequests() {
        return "admin/requests/current";
    }
    
    @RequestMapping("admin/requests/current/processed")
    public ResponseEntity<List<TimestampedRequestData>> getCurrentProcessedRequests() {
        List<TimestampedRequestData> requests = requestService.getCurrentReceivedRequests();
        Collections.reverse(requests);
        return new ResponseEntity<List<TimestampedRequestData>>(requests, HttpStatus.OK);
    }
    
    @RequestMapping("admin/requests/current/sent")
    public ResponseEntity<List<TimestampedRequestData>> getCurrentSentRequests() {
        List<TimestampedRequestData> requests = requestService.getCurrentSentRequests();
        Collections.reverse(requests);
        return new ResponseEntity<List<TimestampedRequestData>>(requests, HttpStatus.OK);
    }
}
