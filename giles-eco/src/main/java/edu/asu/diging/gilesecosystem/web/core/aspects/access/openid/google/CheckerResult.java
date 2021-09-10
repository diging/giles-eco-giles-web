package edu.asu.diging.gilesecosystem.web.core.aspects.access.openid.google;

import edu.asu.diging.gilesecosystem.web.core.tokens.ITokenContents;

public class CheckerResult {
    private ValidationResult result;
    private ITokenContents payload;
    
    public ValidationResult getResult() {
        return result;
    }
    public void setResult(ValidationResult result) {
        this.result = result;
    }
    public ITokenContents getPayload() {
        return payload;
    }
    public void setPayload(ITokenContents payload) {
        this.payload = payload;
    }
    
    
}