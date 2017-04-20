package edu.asu.diging.gilesecosystem.web.controllers.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.pdfbox.rendering.ImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.diging.gilesecosystem.util.exceptions.PropertiesStorageException;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.config.GilesTokenConfig;
import edu.asu.diging.gilesecosystem.web.controllers.admin.pages.SystemConfigPage;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.validators.SystemConfigValidator;

@Controller
public class EditPropertiesController {
    
    @Autowired
    private IPropertiesManager propertyManager;

    @Autowired
    private GilesTokenConfig tokenConfig;
    
    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder, WebDataBinder validateBinder) {
        validateBinder.addValidators(new SystemConfigValidator());
    }

    @RequestMapping(value = "/admin/system/config", method = RequestMethod.GET)
    public String getConfigPage(Model model) {
        SystemConfigPage page = new SystemConfigPage();
        
        page.setDigilibScalerUrl(propertyManager.getProperty(Properties.DIGILIB_SCALER_URL));
        page.setGilesUrl(propertyManager.getProperty(Properties.GILES_URL));
        page.setFreddieUrl(propertyManager.getProperty(Properties.FREDDIE_HOST));
        page.setJarsFileUrl(propertyManager.getProperty(Properties.JARS_FILE_URL));
        page.setJarsUrl(propertyManager.getProperty(Properties.JARS_URL));
        page.setMetadataServiceDocUrl(propertyManager.getProperty(Properties.METADATA_SERVICE_DOC_ENDPOINT));
        
        page.setIframingAllowedHosts(propertyManager.getProperty(Properties.ALLOW_IFRAMING_FROM));
        
        page.setShowGithubLogin(propertyManager.getProperty(Properties.GITHUB_SHOW_LOGIN).equalsIgnoreCase("true"));
        page.setShowGoogleLogin(propertyManager.getProperty(Properties.GOOGLE_SHOW_LOGIN).equalsIgnoreCase("true"));
        page.setShowMitreidLogin(propertyManager.getProperty(Properties.MITREID_SHOW_LOGIN).equalsIgnoreCase("true"));
        page.setGilesFilesTmpDir(propertyManager.getProperty(Properties.GILES_TMP_FOLDER));
        
        List<String> imageTypes = new ArrayList<String>();
        imageTypes.add(ImageType.ARGB.toString());
        imageTypes.add(ImageType.BINARY.toString());
        imageTypes.add(ImageType.GRAY.toString());
        imageTypes.add(ImageType.RGB.toString());
        model.addAttribute("imageTypeOptions", imageTypes);
        model.addAttribute("systemConfigPage", page);
        return "admin/system/config";
    }
    
    @RequestMapping(value = "/admin/system/config", method = RequestMethod.POST)
    public String storeSystemConfig(@Validated @ModelAttribute SystemConfigPage systemConfigPage, BindingResult results, Model model, RedirectAttributes redirectAttrs) {
        model.addAttribute("systemConfigPage", systemConfigPage);
        
        if (results.hasErrors()) {
            model.addAttribute("show_alert", true);
            model.addAttribute("alert_type", "danger");
            model.addAttribute("alert_msg", "System Configuration could not be saved. Please check the error messages below.");
            return "admin/system/config";
        }
        
        Map<String, String> propertiesMap = new HashMap<String, String>();
        propertiesMap.put(Properties.DIGILIB_SCALER_URL, systemConfigPage.getDigilibScalerUrl());
        propertiesMap.put(Properties.GILES_URL, systemConfigPage.getGilesUrl());
        propertiesMap.put(Properties.FREDDIE_HOST, systemConfigPage.getFreddieUrl());
        propertiesMap.put(Properties.JARS_URL, systemConfigPage.getJarsUrl());
        propertiesMap.put(Properties.JARS_FILE_URL, systemConfigPage.getJarsFileUrl());
        propertiesMap.put(Properties.METADATA_SERVICE_DOC_ENDPOINT, systemConfigPage.getMetadataServiceDocUrl());
        propertiesMap.put(Properties.ALLOW_IFRAMING_FROM, systemConfigPage.getIframingAllowedHosts());
        propertiesMap.put(Properties.GITHUB_SHOW_LOGIN, new Boolean(systemConfigPage.isShowGithubLogin()).toString());
        propertiesMap.put(Properties.GOOGLE_SHOW_LOGIN, new Boolean(systemConfigPage.isShowGoogleLogin()).toString());
        propertiesMap.put(Properties.MITREID_SHOW_LOGIN, new Boolean(systemConfigPage.isShowMitreidLogin()).toString());
        propertiesMap.put(Properties.GILES_TMP_FOLDER, systemConfigPage.getGilesFilesTmpDir());
        
        try {
            propertyManager.updateProperties(propertiesMap);
        } catch (PropertiesStorageException e) {
            tokenConfig.getMessageHandler().handleError("System Configuration could not be saved.", e);
            model.addAttribute("show_alert", true);
            model.addAttribute("alert_type", "danger");
            model.addAttribute("alert_msg", "An unexpected error occurred. System Configuration could not be saved.");
            return "admin/system/config";
        }
        
        redirectAttrs.addFlashAttribute("show_alert", true);
        redirectAttrs.addFlashAttribute("alert_type", "success");
        redirectAttrs.addFlashAttribute("alert_msg", "System Configuration was successfully saved.");
        
        return "redirect:/admin/system/config";
    }
}
