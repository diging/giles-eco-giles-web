package edu.asu.diging.gilesecosystem.web.api.v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.reprocessing.IReprocessingService;

@Controller
public class V2ReprocessDocumentController {
    @Autowired
    private IReprocessingService reprocessingService;
    
    @Autowired
    private ITransactionalDocumentService documentService;
    
    @RequestMapping(value = "/api/v2/resources/documents/{documentId}/reprocess", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> reprocessDocument(@PathVariable("documentId") String documentId) {
        IDocument document = documentService.getDocument(documentId);
        if (document == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        reprocessingService.reprocessDocument(document);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
