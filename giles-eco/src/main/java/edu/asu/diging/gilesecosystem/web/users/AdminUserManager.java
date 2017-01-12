package edu.asu.diging.gilesecosystem.web.users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.web.exceptions.BadPasswordException;

@Service
public class AdminUserManager {

    @Autowired
    @Qualifier("adminDetailsService")
    private UserDetailsService adminDetailsService;
    
    public boolean updatePassword(String username, String password) throws BadPasswordException {
        if (password == null || password.trim().isEmpty()) {
            throw new BadPasswordException("Password cannot be empty.");
        }
        return ((AdminUserDetailsService)adminDetailsService).changePassword(username, password);
    }
    
    public List<UserDetails> getAdministrators() {
        return ((AdminUserDetailsService)adminDetailsService).getAllAdmins();
    }
    
    public boolean isPasswordValid(String username, String password) {
        UserDetails details;
        try { 
            details = adminDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException ex) {
            return false;
        }
        return BCrypt.checkpw(password, details.getPassword());
    }
}
