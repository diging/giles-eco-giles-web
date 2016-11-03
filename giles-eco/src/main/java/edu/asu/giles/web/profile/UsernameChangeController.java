package edu.asu.giles.web.profile;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.giles.users.IUserManager;
import edu.asu.giles.users.User;
import edu.asu.giles.validators.UsernameValidator;
import edu.asu.giles.web.profile.forms.UsernameForm;

@Controller
public class UsernameChangeController {
    
    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private UsernameValidator usernameValidator;
    
    @InitBinder("usernameForm")
    public void initBinder(WebDataBinder validateBinder) {
        validateBinder.addValidators(usernameValidator);
    }
    
    @RequestMapping(value = "/profile/username/change")
    public String showPage(Model model, Principal principal) {
        
        User user = getUserObject(principal);
        
        UsernameForm form = new UsernameForm();
        form.setUsername(user.getUsername());
        model.addAttribute("usernameForm", form);
        model.addAttribute("user", user);
        
        return "profile/username/change";
    }

    @RequestMapping(value = "/profile/username/change", method = RequestMethod.POST)
    public String changeUsername(@Validated @ModelAttribute UsernameForm form, BindingResult result, Model model, Principal principal, RedirectAttributes redirectAttrs) {
        
        if (result.hasErrors()) {
            model.addAttribute("usernameForm", form);
            return "profile/username/change";
        }
        
        User user = getUserObject(principal);
        user.setUsername(form.getUsername());
        userManager.storeModifiedUser(user);
        
        redirectAttrs.addFlashAttribute("show_alert", true);
        redirectAttrs.addFlashAttribute("alert_type", "success");
        redirectAttrs.addFlashAttribute("alert_msg", "Your username has been changed. Please logout and login again.");
        
        return "redirect:/profile";
    }
    
    private User getUserObject(Principal principal) {
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
        return user;
    }
    
}
