package edu.asu.diging.gilesecosystem.web.controllers.admin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.gilesecosystem.util.exceptions.PropertiesStorageException;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.service.system.ISigningSecretGenerator;

@Controller
public class SigningSecretController {
    
    @Autowired
    private ISigningSecretGenerator secretGenerator;
    
    @Autowired
    private IPropertiesManager propertiesManager;

    @RequestMapping(value="/admin/system/auth")
    public String showPage(Model model) {
        
        return "admin/system/auth";
    }
    
    @RequestMapping(value="/admin/system/auth", method=RequestMethod.POST)
    public String generateSecrets(Model model) {
        
        String secretTokens = secretGenerator.generateSigningSecret();
        String secretApps = secretGenerator.generateSigningSecret();
        
        Map<String, String> props = new HashMap<String, String>();
        props.put(Properties.SIGNING_KEY, secretTokens);
        props.put(Properties.SIGNING_KEY_APPS, secretApps);
        
        try {
            propertiesManager.updateProperties(props);
        } catch (PropertiesStorageException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return "admin/system/auth/done";
    }
  
}
