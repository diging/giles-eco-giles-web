package edu.asu.diging.gilesecosystem.web.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.gilesecosystem.requests.exceptions.MessageCreationException;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.aspects.access.annotations.AccountCheck;
import edu.asu.diging.gilesecosystem.web.core.aspects.access.annotations.DocumentIdAccessCheck;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.delete.IDeleteDocumentService;

@Controller
public class DeleteDocumentController {
    
    @Autowired
    private ITransactionalDocumentService documentService;
    
    @Autowired
    private IDeleteDocumentService deleteDocumentService;
    
    @AccountCheck
    @DocumentIdAccessCheck("documentId")
    @RequestMapping(value = "/documents/{documentId}", method = RequestMethod.POST)
    public String deleteDocument(@PathVariable("documentId") String documentId) throws GilesProcessingException, MessageCreationException, UnstorableObjectException {
        IDocument document = documentService.getDocument(documentId);
        deleteDocumentService.initiateDeletion(document);
        
        return "files/upload";
    }
}
