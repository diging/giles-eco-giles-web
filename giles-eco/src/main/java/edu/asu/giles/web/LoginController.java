package edu.asu.giles.web;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.giles.core.IUpload;
import edu.asu.giles.exceptions.GilesMappingException;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.files.IUploadDatabaseClient;
import edu.asu.giles.service.IGilesMappingService;
import edu.asu.giles.service.impl.GilesMappingService;
import edu.asu.giles.service.properties.IPropertiesManager;
import edu.asu.giles.users.IUserManager;
import edu.asu.giles.users.User;
import edu.asu.giles.web.pages.UploadPageBean;

@Controller
public class LoginController {

    @Autowired
    private IFilesManager filesManager;
    
    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private IPropertiesManager propertiesManager;
    
    @RequestMapping(value = "/")
    public String login(Principal principal, Model model) throws GilesMappingException {

        String username = null;
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
            if (token.getPrincipal() instanceof User) {
                username = ((User) token.getPrincipal()).getUsername();
            } else if (token.getPrincipal() instanceof UserDetails) {
                username = ((UserDetails) token.getPrincipal()).getUsername();
            }
        }

        if (username != null) {
            List<IUpload> uploads = filesManager.getUploadsOfUser(username, 1, -1, "createdDate", IUploadDatabaseClient.DESCENDING);

            List<IUpload> latestUploads = uploads.subList(0, uploads.size() > 5 ? 5 : uploads.size());
            
            IGilesMappingService<IUpload, UploadPageBean> uploadMappingService = new GilesMappingService<>();
            List<UploadPageBean> mappedUploads = new ArrayList<UploadPageBean>();
           
            for (IUpload up : latestUploads) {
                UploadPageBean upload = uploadMappingService.convertToT2(up, new UploadPageBean());
                upload.setNrOfDocuments(filesManager.getDocumentsByUploadId(upload.getId()).size());
                mappedUploads.add(upload);
            }
            
            model.addAttribute("uploads", mappedUploads);
            model.addAttribute("user", userManager.findUser(username));
        }
        
        model.addAttribute("githubShowLogin", propertiesManager.getProperty(IPropertiesManager.GITHUB_SHOW_LOGIN).equals("true"));
        model.addAttribute("googleShowLogin", propertiesManager.getProperty(IPropertiesManager.GOOGLE_SHOW_LOGIN).equals("true"));
        model.addAttribute("mitreidShowLogin", propertiesManager.getProperty(IPropertiesManager.MITREID_SHOW_LOGIN).equals("true"));
        
        return "login";
    }
    
    @RequestMapping("/403")
    public String accessForbidden() {
        return "403";
    }

}
