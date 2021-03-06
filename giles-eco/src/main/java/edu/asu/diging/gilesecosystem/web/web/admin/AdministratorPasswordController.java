package edu.asu.diging.gilesecosystem.web.web.admin;

import java.security.Principal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.web.core.exceptions.BadPasswordException;
import edu.asu.diging.gilesecosystem.web.core.exceptions.UnauthorizedException;
import edu.asu.diging.gilesecosystem.web.core.users.IAdminUserManager;
import edu.asu.diging.gilesecosystem.web.web.admin.pages.AdminUser;
import edu.asu.diging.gilesecosystem.web.web.validators.AdminPasswordValidator;

@Controller
public class AdministratorPasswordController {
    
    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private AdminPasswordValidator validator;
    
    @Autowired
    private IAdminUserManager adminManager;

    @Autowired
    private ISystemMessageHandler messageHandler;

    @InitBinder
    public void init(WebDataBinder binder) {
        binder.addValidators(validator);
    }

    @RequestMapping(value = "/admin/system/admins/password/change")
    public String showPage(Model model, Principal principal) {
        AdminUser admin = new AdminUser();
        admin.setUsername(principal.getName());
        model.addAttribute("adminUser", admin);
        return "admin/system/admins/password/change";
    }

    @RequestMapping(value = "/admin/system/admins/password/change", method = RequestMethod.POST)
    public String changePassword(@Validated @ModelAttribute("adminUser") AdminUser adminUser,
            BindingResult results, RedirectAttributes redirectAttrs, Locale locale, Principal principal) {

        if (!principal.getName().equals(adminUser.getUsername())) {
            redirectAttrs.addFlashAttribute("show_alert", true);
            redirectAttrs.addFlashAttribute("alert_type", "danger");
            redirectAttrs.addFlashAttribute("alert_msg", messageSource.getMessage("admin_user_change_password_not_allowed", new String[]{adminUser.getUsername()}, locale));

            return "redirect:/admin/system/admins";
        }
        
        if (results.hasErrors()) {
            adminUser.setOldPassword("");
            adminUser.setNewPassword("");
            adminUser.setRetypedPassword("");
            return "admin/system/admins/password/change";
        }
        
        boolean success = false;
        try {
            success = adminManager.updatePassword(adminUser.getUsername(), adminUser.getOldPassword(), adminUser.getNewPassword());
        } catch (BadPasswordException | UnauthorizedException e) {
            // this should never happen because it should be caught by the validator
            messageHandler.handleMessage("Could not update password.", e, MessageType.ERROR);
        }

        if (success) {
            redirectAttrs.addFlashAttribute("show_alert", true);
            redirectAttrs.addFlashAttribute("alert_type", "success");
            redirectAttrs.addFlashAttribute("alert_msg", messageSource.getMessage("admin_user_change_password_success", new String[]{adminUser.getUsername()}, locale));
        } else {
            redirectAttrs.addFlashAttribute("show_alert", true);
            redirectAttrs.addFlashAttribute("alert_type", "danger");
            redirectAttrs.addFlashAttribute("alert_msg", messageSource.getMessage("admin_user_change_password_failure", new String[]{adminUser.getUsername()}, locale));
        }
        
        return "redirect:/admin/system/admins";
    }
}
