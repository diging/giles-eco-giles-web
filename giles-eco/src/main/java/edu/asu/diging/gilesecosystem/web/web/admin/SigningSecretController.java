package edu.asu.diging.gilesecosystem.web.web.admin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.exceptions.PropertiesStorageException;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.core.service.system.ISigningSecretGenerator;

@Controller
public class SigningSecretController {
    
    @Autowired
    private ISigningSecretGenerator secretGenerator;
    
    @Autowired
    private IPropertiesManager propertiesManager;

    @Autowired
    private ISystemMessageHandler messageHandler;

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
            messageHandler.handleMessage("Properties could not be stored.", e, MessageType.ERROR);
        }
        
        return "admin/system/auth/done";
    }
  
}
