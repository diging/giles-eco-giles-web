package edu.asu.diging.gilesecosystem.web.core.service.impl;

import java.time.ZonedDateTime;

public class ResendingResult {

    private int requestCount;
    private ZonedDateTime completionTime;
    
    public ResendingResult(int requestCount, ZonedDateTime completionTime) {
        super();
        this.requestCount = requestCount;
        this.completionTime = completionTime;
    }
    
    public int getRequestCount() {
        return requestCount;
    }
    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }
    public ZonedDateTime getCompletionTime() {
        return completionTime;
    }
    public void setCompletionTime(ZonedDateTime completionTime) {
        this.completionTime = completionTime;
    }
    
    
}
