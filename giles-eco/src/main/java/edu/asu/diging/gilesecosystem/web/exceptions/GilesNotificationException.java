package edu.asu.diging.gilesecosystem.web.exceptions;

public class GilesNotificationException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 5203233497728362534L;

    public GilesNotificationException() {
        super();
    }

    public GilesNotificationException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public GilesNotificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public GilesNotificationException(String message) {
        super(message);
    }

    public GilesNotificationException(Throwable cause) {
        super(cause);
    }
}
