package edu.asu.diging.gilesecosystem.web.domain;



/**
 * Status enum to indicate if a page was successfully processed. 
 * 
 * Note: if {@link edu.asu.diging.gilesecosystem.requests.PageStatus} changes, this enum has
 * to be adjusted to map statuses correctly using the valueof() method.
 * 
 * @author jdamerow
 *
 */
public enum PageStatus {
    COMPLETE,
    FAILED;
}
