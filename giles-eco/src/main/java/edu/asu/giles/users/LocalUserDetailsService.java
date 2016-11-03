package edu.asu.giles.users;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userService")
public class LocalUserDetailsService implements UserDetailsService {

    @Autowired
    private IUserManager userManager;

    @Override
    public UserDetails loadUserByUsername(String arg0)
            throws UsernameNotFoundException {
        User user = userManager.findUser(arg0);

        if (user == null)
            throw new UsernameNotFoundException("Couldn't find username.");

        List<GilesGrantedAuthority> roles = new ArrayList<GilesGrantedAuthority>();
        for (String role : user.getRoles()) {
            roles.add(new GilesGrantedAuthority(role));
        }

        UserDetails details = new GilesUserDetails(user.getUsername(),
                user.getFullname(), user.getPassword(), roles, user.getEmail());
        return details;
    }
}