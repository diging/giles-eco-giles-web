package edu.asu.giles.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.giles.aspects.access.annotations.AccountCheck;
import edu.asu.giles.aspects.access.annotations.DocumentIdAccessCheck;
import edu.asu.giles.core.DocumentAccess;
import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.exceptions.UnstorableObjectException;
import edu.asu.giles.files.IFilesManager;

@Controller
public class ChangeAccessController {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IFilesManager filesManager;

    @AccountCheck
    @DocumentIdAccessCheck("documentId")
    @RequestMapping(value = "/documents/{documentId}/access/change", method = RequestMethod.POST)
    public String changeAccess(@PathVariable("documentId") String documentId,
            @RequestParam("access") String access,
            @RequestParam("uploadId") String uploadId,
            RedirectAttributes redirectAttrs) {

        if (documentId == null || documentId.isEmpty() || access == null
                || access.isEmpty()) {
            // soemthing is wrong here
            // let's just silently ignore it...
            return "redirect:/files/upload";
        }

        IDocument document = filesManager.getDocument(documentId);
        if (document == null) {
            // and again, something weird going on
            // let's ignore it
            return "redirect:/files/upload";
        }

        DocumentAccess docAccess = DocumentAccess.valueOf(access);
        if (docAccess == null) {
            // and again, something weird going on
            // let's ignore it
            return "redirect:/files/upload";
        }

        document.setAccess(docAccess);
        try {
            filesManager.saveDocument(document);
        } catch (UnstorableObjectException e) {
            // this should not happen, since it's an existing object
            logger.error("Could not store document.", e);
            redirectAttrs.addAttribute("show_alert", true);
            redirectAttrs.addAttribute("alert_type", "danger");
            redirectAttrs.addAttribute("alert_msg",
                    "An interal server error occurred. Document access could not be changed.");
            return "redirect:/uploads/" + uploadId;
        }

        boolean errorWhenSavingFiles = false;
        List<IFile> files = filesManager.getFilesOfDocument(document);
        for (IFile file : files) {
            file.setAccess(docAccess);
            try {
                filesManager.saveFile(file);
            } catch (UnstorableObjectException e) {
                logger.error("Could not store file.", e);
                errorWhenSavingFiles = true;
            }
        }
        files = filesManager.getTextFilesOfDocument(document);
        for (IFile file : files) {
            file.setAccess(docAccess);
            try {
                filesManager.saveFile(file);
            } catch (UnstorableObjectException e) {
                logger.error("Could not store file.", e);
                errorWhenSavingFiles = true;
            }
        }

        if (errorWhenSavingFiles) {
            redirectAttrs.addAttribute("show_alert", true);
            redirectAttrs.addAttribute("alert_type", "warning");
            redirectAttrs.addAttribute("alert_msg",
                    "Access type successfully updated for document but one or more files could not be updated.");
        } else {
            redirectAttrs.addAttribute("show_alert", true);
            redirectAttrs.addAttribute("alert_type", "success");
            redirectAttrs.addAttribute("alert_msg",
                    "Access type successfully updated.");
        }

        return "redirect:/uploads/" + uploadId;
    }
}
