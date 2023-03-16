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
import edu.asu.diging.gilesecosystem.web.core.service.delete.IDeleteDocumentService;

@Controller
public class V2DeleteDocumentController {

    @Autowired
    private IDeleteDocumentService deleteDocumentService;
    
    @Autowired
    private ITransactionalDocumentService documentService;
    
    @RequestMapping(value = "/api/v2/resources/documents/{documentId}", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> deleteDocument(@PathVariable("documentId") String documentId) {
        IDocument document = documentService.getDocument(documentId);
        if (document == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        deleteDocumentService.deleteDocument(document);
        return new ResponseEntity<String>(HttpStatus.ACCEPTED);
    }
}
