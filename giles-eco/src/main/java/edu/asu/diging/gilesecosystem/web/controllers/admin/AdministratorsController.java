package edu.asu.diging.gilesecosystem.web.controllers.admin;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.gilesecosystem.web.users.AdminUserManager;

@Controller
public class AdministratorsController {

    @Autowired
    private AdminUserManager adminManager;
    
    @RequestMapping("/admin/system/admins")
    public String show(Model model, Principal principal) {
        model.addAttribute("currentUser", principal.getName());
        model.addAttribute("admins", adminManager.getAdministrators());
        return "admin/system/admins";
    }
    
}
