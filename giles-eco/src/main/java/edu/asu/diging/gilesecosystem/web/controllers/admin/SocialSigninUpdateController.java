package edu.asu.diging.gilesecosystem.web.controllers.admin;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.gilesecosystem.util.exceptions.PropertiesStorageException;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.config.IReloadService;
import edu.asu.diging.gilesecosystem.web.controllers.admin.pages.SignInProviderConfig;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;

@Controller
public class SocialSigninUpdateController {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IReloadService connFactoryService;
    
    @Autowired
    private IPropertiesManager propertiesManager;

    @RequestMapping(value="/admin/system/social")
    public String showUpdatePage(Model model) {
        SignInProviderConfig githubConfig = new SignInProviderConfig();
        githubConfig.setClientId(propertiesManager.getProperty(Properties.GITHUB_CLIENT_ID));
        String githubSecret = propertiesManager.getProperty(Properties.GITHUB_SECRET);
        if (githubSecret != null && githubSecret.length() > 2) {
            githubSecret = githubSecret.substring(0,2) + githubSecret.substring(2).replaceAll(".", "*");
            githubConfig.setSecret(githubSecret);
        }
        model.addAttribute("githubConfig", githubConfig);
        
        return "admin/system/social";
    }
    
    @RequestMapping(value="/admin/system/social/github", method=RequestMethod.POST)
    public String updateGithubConfig(@ModelAttribute SignInProviderConfig config, Model model) {
        
        Map<String, String> githubConfig = new HashMap<String, String>();
        githubConfig.put(Properties.GITHUB_CLIENT_ID, config.getClientId());
        githubConfig.put(Properties.GITHUB_SECRET, config.getSecret());
        try {
            propertiesManager.updateProperties(githubConfig);
        } catch (PropertiesStorageException e) {
            logger.error("Could not store properties.", e);
        }
        
        connFactoryService.reloadApplicationContext();
        
        return "redirect:/admin/system/social";
    }
}
