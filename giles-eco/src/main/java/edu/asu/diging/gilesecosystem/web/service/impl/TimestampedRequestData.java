package edu.asu.diging.gilesecosystem.web.service.impl;

import java.time.ZonedDateTime;

import edu.asu.diging.gilesecosystem.requests.IRequest;

public class TimestampedRequestData {
    public IRequest request;
    public ZonedDateTime time;

    public TimestampedRequestData(IRequest request, ZonedDateTime time) {
        this.request = request;
        this.time = time;
    }
    
    public IRequest getRequest() {
        return request;
    }
    public void setRequest(IRequest request) {
        this.request = request;
    }
    public ZonedDateTime getTime() {
        return time;
    }
    public void setTime(ZonedDateTime time) {
        this.time = time;
    }
}