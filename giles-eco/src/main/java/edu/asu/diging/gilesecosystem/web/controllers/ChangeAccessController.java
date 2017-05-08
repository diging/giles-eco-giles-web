package edu.asu.diging.gilesecosystem.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.aspects.access.annotations.AccountCheck;
import edu.asu.diging.gilesecosystem.web.aspects.access.annotations.DocumentIdAccessCheck;
import edu.asu.diging.gilesecosystem.web.domain.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.domain.IDocument;
import edu.asu.diging.gilesecosystem.web.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.service.core.ITransactionalDocumentService;

@Controller
public class ChangeAccessController {
    
    @Autowired
    private IFilesManager filesManager;
    
    @Autowired
    private ITransactionalDocumentService documentService;

    @Autowired
    private ISystemMessageHandler messageHandler;

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

        IDocument document = documentService.getDocument(documentId);
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
        
        try {
            boolean isChangeSuccess = filesManager.changeDocumentAccess(document, docAccess);

            if (!isChangeSuccess) {
                redirectAttrs.addAttribute("show_alert", true);
                redirectAttrs.addAttribute("alert_type", "warning");
                redirectAttrs.addAttribute("alert_msg",
                        "Access type successfully updated for document but one or more files could not be updated.");
            } else {
                redirectAttrs.addAttribute("show_alert", true);
                redirectAttrs.addAttribute("alert_type", "success");
                redirectAttrs.addAttribute("alert_msg", "Access type successfully updated.");
            }

        } catch (UnstorableObjectException e) {
            // this should not happen, since it's an existing object
            messageHandler.handleMessage("Could not store document.", e, MessageType.ERROR);
            redirectAttrs.addAttribute("show_alert", true);
            redirectAttrs.addAttribute("alert_type", "danger");
            redirectAttrs.addAttribute("alert_msg",
                    "An interal server error occurred. Document access could not be changed.");
            return "redirect:/uploads/" + uploadId;
        }

        return "redirect:/uploads/" + uploadId;

    }
}
