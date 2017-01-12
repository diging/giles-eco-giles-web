package edu.asu.diging.gilesecosystem.web.controllers.admin;

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

import edu.asu.diging.gilesecosystem.web.controllers.admin.pages.AdminUser;
import edu.asu.diging.gilesecosystem.web.validators.AdminPasswordValidator;

@Controller
public class AdministratorPasswordController {

    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private AdminPasswordValidator validator;

    @InitBinder
    public void init(WebDataBinder binder) {
        binder.addValidators(validator);
    }

    @RequestMapping(value = "/admin/system/admins/password/change")
    public String showPage(Model model) {
        model.addAttribute("adminUser", new AdminUser());
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

        redirectAttrs.addFlashAttribute("show_alert", true);
        redirectAttrs.addFlashAttribute("alert_type", "success");
        redirectAttrs.addFlashAttribute("alert_msg", messageSource.getMessage("admin_user_change_password_success", new String[]{adminUser.getUsername()}, locale));

        return "redirect:/admin/system/admins";
    }
}
