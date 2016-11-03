package edu.asu.giles.exceptions;

public class AppMisconfigurationException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -8675204613571935513L;

    public AppMisconfigurationException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public AppMisconfigurationException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    public AppMisconfigurationException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public AppMisconfigurationException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public AppMisconfigurationException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
