package edu.asu.giles.tokens.impl;

import edu.asu.giles.tokens.IApiTokenContents;

/**
 * Class that holds the information that was encoded in a token.
 * 
 * @author Julia Damerow
 *
 */
public class ApiTokenContents implements IApiTokenContents {

    private String username;
    private boolean expired;
    
    /* (non-Javadoc)
     * @see edu.asu.giles.tokens.impl.ITokenContents#getUsername()
     */
    @Override
    public String getUsername() {
        return username;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.tokens.impl.ITokenContents#setUsername(java.lang.String)
     */
    @Override
    public void setUsername(String username) {
        this.username = username;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.tokens.impl.ITokenContents#isExpired()
     */
    @Override
    public boolean isExpired() {
        return expired;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.tokens.impl.ITokenContents#setExpired(boolean)
     */
    @Override
    public void setExpired(boolean expired) {
        this.expired = expired;
    }
    
    
}
