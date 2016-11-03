package edu.asu.giles.web.users;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.giles.users.GilesRole;
import edu.asu.giles.users.IUserManager;

@Controller
public class AccountController {
    
    @Autowired
    private IUserManager userManager;

    @RequestMapping(value = "/users/user/{username}/approve", method = RequestMethod.POST)
    public String approveUserAccount(@PathVariable String username, Model model, Principal principal) {
        userManager.approveUserAccount(username);
        return "redirect:/users";
    }
    
    @RequestMapping(value = "/users/user/{username}/revoke", method = RequestMethod.POST)
    public String revokeUserAccount(@PathVariable String username, Model model, Principal principal) {
        userManager.revokeUserAccount(username);
        return "redirect:/users";
    }
    
    @RequestMapping(value = "/users/user/{username}/role/add", method = RequestMethod.POST) 
    public String addRoleToUser(@PathVariable String username, @RequestParam("role") String role, Principal principal) {
        GilesRole gilesRole = GilesRole.valueOf(role);
        if (gilesRole == null) {
            return "error/noSuchRole";
        }
        
        userManager.addRoleToUser(username, gilesRole);
        return "redirect:/users";
    }
    
    @RequestMapping(value = "/users/user/{username}/role/remove", method = RequestMethod.POST)
    public String removeRoleFromUser(@PathVariable String username, @RequestParam("role") String role, Principal principal) {
        GilesRole gilesRole = GilesRole.valueOf(role);
        if (gilesRole == null) {
            return "error/noSuchRole";
        }
        
        userManager.removeRoleFromUser(username, gilesRole);
        return "redirect:/users";
    }
    
    @RequestMapping(value = "/users/user/{username}/remove", method = RequestMethod.POST)
    public String deleteUser(@PathVariable String username, RedirectAttributes redirectAttrs) {
        userManager.deleteUser(username);
        
        redirectAttrs.addFlashAttribute("show_alert", true);
        redirectAttrs.addFlashAttribute("alert_type", "info");
        redirectAttrs.addFlashAttribute("alert_msg", "User successfully deleted.");
        return "redirect:/users";
    }
}
