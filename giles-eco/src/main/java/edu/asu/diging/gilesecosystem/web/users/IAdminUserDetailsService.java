package edu.asu.diging.gilesecosystem.web.users;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface IAdminUserDetailsService {

    public abstract UserDetails loadUserByUsername(String arg0)
            throws UsernameNotFoundException;

    /**
     * Return all stored admin users.
     * @return
     */
    public abstract List<UserDetails> getAllAdmins();

    /**
     * Change the password of a user. This method will generate a BCrypt hash
     * for a given plaintext password and will store the hashed password.
     * @param username Username of the user whose password should be changed.
     * @param password New password in plain text.
     * @return
     */
    public abstract boolean changePassword(String username, String password);

    public abstract boolean isPasswordValid(String username, String password);

}