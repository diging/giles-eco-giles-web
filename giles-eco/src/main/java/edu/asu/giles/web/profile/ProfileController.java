package edu.asu.giles.web.profile;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.giles.users.IUserManager;
import edu.asu.giles.users.User;

@Controller
public class ProfileController {

    @Autowired
    private IUserManager userManager;
    
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String getUserProfile(Principal principal, Model model) {
        
        User user = null;
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
            if (token.getPrincipal() instanceof User) {
                user = (User) token.getPrincipal();
            } else if (token.getPrincipal() instanceof UserDetails) {
                String username = ((UserDetails) token.getPrincipal()).getUsername();
                user = userManager.findUser(username);
            }
        }
        
        model.addAttribute("user", user);
        
        return "profile";
    }
}
