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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.giles.aspects.access.annotations.AccountCheck;
import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IUpload;
import edu.asu.giles.exceptions.GilesMappingException;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.files.IUploadDatabaseClient;
import edu.asu.giles.service.IGilesMappingService;
import edu.asu.giles.service.impl.GilesMappingService;
import edu.asu.giles.users.User;
import edu.asu.giles.web.pages.UploadPageBean;

@Controller
public class ListUploadsController {

    @Autowired
    private IFilesManager filesManager;
    
    @AccountCheck
    @RequestMapping(value = "/uploads", method = RequestMethod.GET)
    public String showUploads(Principal principal, Model model, @RequestParam(defaultValue = "1") String page, @RequestParam(defaultValue = IUploadDatabaseClient.DESCENDING + "") String sortDir) throws GilesMappingException {
        String username = null;
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
            if (token.getPrincipal() instanceof User) {
                username = ((User) token.getPrincipal()).getUsername();
            } else if (token.getPrincipal() instanceof UserDetails) {
                username = ((UserDetails) token.getPrincipal()).getUsername();
            }
        }
        
        int pageInt = new Integer(page);
        int pageCount = 1;
        int sortDirInt = new Integer(sortDir);
        
        if (username != null) {
            pageCount = filesManager.getUploadsOfUserPageCount(username);
            
            List<IUpload> uploads = filesManager.getUploadsOfUser(username, pageInt, -1, "createdDate", sortDirInt);
            List<UploadPageBean> mappedUploads = new ArrayList<UploadPageBean>();
            
            if (uploads != null) {
                IGilesMappingService<IUpload, UploadPageBean> uploadMappingService = new GilesMappingService<>();
                for (IUpload up : uploads) {
                    UploadPageBean upload = uploadMappingService.convertToT2(up, new UploadPageBean());
                    upload.setUploadedFiles(new ArrayList<>());
                    
                    List<IDocument> docs = filesManager.getDocumentsByUploadId(up.getId());
                    upload.setNrOfDocuments(docs.size());
                    
                    for (IDocument doc : docs) {
                        if (doc.getFiles() != null && doc.getFiles().size() > 0) {
                            IFile firstFile = doc.getFiles().get(0);
                            upload.getUploadedFiles().add(firstFile);
                        }
                    }
                    
                    mappedUploads.add(upload);
                }
            }
            model.addAttribute("count", pageCount);
            model.addAttribute("uploads", mappedUploads);
            model.addAttribute("totalUploads", filesManager.getUploadsOfUserCount(username));
        }
        if (pageInt < 1) {
            pageInt = 1;
        }
        if (pageInt > pageCount) {
            pageInt = pageCount;
        }
        model.addAttribute("page", pageInt);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("oppSortDir", sortDirInt == IUploadDatabaseClient.ASCENDING ? IUploadDatabaseClient.DESCENDING : IUploadDatabaseClient.ASCENDING);
        return "uploads";
    }
}
