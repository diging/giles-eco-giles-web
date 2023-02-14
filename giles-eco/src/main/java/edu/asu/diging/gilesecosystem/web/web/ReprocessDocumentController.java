package edu.asu.diging.gilesecosystem.web.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.gilesecosystem.web.core.aspects.access.annotations.AccountCheck;
import edu.asu.diging.gilesecosystem.web.core.aspects.access.annotations.DocumentIdAccessCheck;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.reprocessing.IReprocessingService;

@Controller
public class ReprocessDocumentController {
    
    @Autowired
    private ITransactionalDocumentService documentService;
    
    @Autowired
    private IReprocessingService reprocessingService;
    
    @AccountCheck
    @DocumentIdAccessCheck("documentId")
    @RequestMapping(value = "/documents/{documentId}/reprocess", method = RequestMethod.POST)
    public String reprocessDocument(@PathVariable("documentId") String documentId) {
        IDocument document = documentService.getDocument(documentId);
        reprocessingService.reprocessDocument(document);

        return "redirect:/documents/" + documentId; 
    }
}
