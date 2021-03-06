package edu.asu.diging.gilesecosystem.web.web.users;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.gilesecosystem.web.core.users.IUserManager;
import edu.asu.diging.gilesecosystem.web.core.users.User;

@Controller
public class ListUsersController {
    
    @Autowired
    private IUserManager userManager;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String users(Model model, Principal principal) {
        User[] users = userManager.getAllUsers();
        model.addAttribute("users", users);
        
        return "users";
    }
    
}
