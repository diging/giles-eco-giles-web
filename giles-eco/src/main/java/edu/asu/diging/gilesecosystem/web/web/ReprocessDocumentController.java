package edu.asu.diging.gilesecosystem.web.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.gilesecosystem.web.core.aspects.access.annotations.AccountCheck;
import edu.asu.diging.gilesecosystem.web.core.aspects.access.annotations.DocumentIdAccessCheck;
import edu.asu.diging.gilesecosystem.web.core.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;

public class ReprocessDocumentController {
    
    @Autowired
    private ITransactionalDocumentService documentService;
    
    @Autowired
    private IFilesManager filesManager;
    
    @AccountCheck
    @DocumentIdAccessCheck("documentId")
    @RequestMapping(value = "/documents/{documentId}/reprocess", method = RequestMethod.POST)
    public String reprocessDocument(@PathVariable("documentId") String documentId) {
        IDocument document = documentService.getDocument(documentId);
        List<IFile> files = filesManager.getFilesOfDocument(document);
        for(IFile file : files) {;
            filesManager.changeFileProcessingStatus(file, ProcessingStatus.UNPROCESSED);
        }
        
        return "redirect:documents/" + documentId; 
    }
}
