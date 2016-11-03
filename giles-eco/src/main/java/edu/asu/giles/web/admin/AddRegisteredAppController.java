package edu.asu.giles.web.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import edu.asu.giles.apps.IRegisteredApp;
import edu.asu.giles.apps.impl.RegisteredApp;
import edu.asu.giles.exceptions.TokenGenerationErrorException;
import edu.asu.giles.service.IIdentityProviderRegistry;
import edu.asu.giles.service.apps.IRegisteredAppManager;
import edu.asu.giles.tokens.IAppToken;
import edu.asu.giles.validators.RegisteredAppValidator;

@Controller
public class AddRegisteredAppController {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IRegisteredAppManager appManager;
    
    @Autowired
    private IIdentityProviderRegistry providerRegistry;
    
    @InitBinder("app")
    public void init(WebDataBinder binder) {
        binder.addValidators(new RegisteredAppValidator());
    }

    @RequestMapping(value = "/admin/apps/register", method = RequestMethod.GET)
    public String showRegisterAppPage(Model model) {
        model.addAttribute("app", new RegisteredApp());
        
        model.addAttribute("providers", providerRegistry.getProviders());
        
        return "admin/apps/register";
    }
    
    @RequestMapping(value = "/admin/apps/register", method = RequestMethod.POST)
    public String registerApp(@Validated @ModelAttribute("app") RegisteredApp app, BindingResult results, Model model, RedirectAttributes redirectAttrs) {
        
        if (results.hasErrors()) {
            model.addAttribute("providers", providerRegistry.getProviders());
            return "admin/apps/register";
        }
        
        IRegisteredApp newApp = appManager.storeApp(app);
        
        IAppToken token = null;
        try {
            token = appManager.createToken(newApp);
        } catch (TokenGenerationErrorException e) {
            logger.error("Token generation failed.", e);
            redirectAttrs.addFlashAttribute("show_alert", true);
            redirectAttrs.addFlashAttribute("alert_type", "danger");
            redirectAttrs.addFlashAttribute("alert_msg", "You app has been registered, but token generation failed.");   
        }
        redirectAttrs.addFlashAttribute("token", token);
        
        return "redirect:/admin/apps/" + app.getId();
    }
}
