package edu.asu.giles.exceptions;

public class UnstorableObjectException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 4243432891295800157L;

    public UnstorableObjectException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public UnstorableObjectException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    public UnstorableObjectException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public UnstorableObjectException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public UnstorableObjectException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
