package edu.asu.diging.gilesecosystem.web.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.gilesecosystem.requests.impl.ImageExtractionRequest;
import edu.asu.diging.gilesecosystem.requests.impl.OCRRequest;
import edu.asu.diging.gilesecosystem.requests.impl.StorageRequest;
import edu.asu.diging.gilesecosystem.requests.impl.TextExtractionRequest;
import edu.asu.diging.gilesecosystem.web.aspects.access.annotations.AccountCheck;
import edu.asu.diging.gilesecosystem.web.aspects.access.annotations.DocumentIdAccessCheck;
import edu.asu.diging.gilesecosystem.web.controllers.pages.DocumentPageBean;
import edu.asu.diging.gilesecosystem.web.controllers.pages.FilePageBean;
import edu.asu.diging.gilesecosystem.web.controllers.pages.PagePageBean;
import edu.asu.diging.gilesecosystem.web.controllers.util.StatusHelper;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IPage;
import edu.asu.diging.gilesecosystem.web.core.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesMappingException;
import edu.asu.diging.gilesecosystem.web.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.files.IProcessingRequestsDatabaseClient;
import edu.asu.diging.gilesecosystem.web.service.IGilesMappingService;
import edu.asu.diging.gilesecosystem.web.service.IMetadataUrlService;
import edu.asu.diging.gilesecosystem.web.service.impl.GilesMappingService;

@Controller
public class ViewDocumentController {
    
    @Autowired
    private IFilesManager fileManager;
    
    @Autowired
    private IMetadataUrlService metadataService;  
    
    @Autowired
    private StatusHelper statusHelper;
    
    @Autowired
    private IProcessingRequestsDatabaseClient procReqDbClient;

    @AccountCheck
    @DocumentIdAccessCheck
    @RequestMapping(value = "/documents/{docId}", method = RequestMethod.GET)
    public String showDocument(@PathVariable String docId, Model model, Locale locale) throws GilesMappingException {
        IDocument doc = fileManager.getDocument(docId);
        
        IGilesMappingService<IFile, FilePageBean> fileMappingService = new GilesMappingService<>();
        IGilesMappingService<IDocument, DocumentPageBean> docMappingService = new GilesMappingService<>();
        IGilesMappingService<IPage, PagePageBean> pageMappingService = new GilesMappingService<>();
        
        DocumentPageBean docBean = docMappingService.convertToT2(doc, new DocumentPageBean());
        model.addAttribute("document", docBean);
        
        List<IProcessingRequest> procRequests = procReqDbClient.getRequestByDocumentId(doc.getId());
        Map<String, List<IProcessingRequest>> requestsByFileId = new HashMap<String, List<IProcessingRequest>>();
        procRequests.forEach(new Consumer<IProcessingRequest>() {
            @Override
            public void accept(IProcessingRequest t) {
                if (requestsByFileId.get(t.getFileId()) == null) {
                    requestsByFileId.put(t.getFileId(), new ArrayList<>());
                }
                requestsByFileId.get(t.getFileId()).add(t);
            }
        });
        
        statusHelper.setProcessingLabel(docBean, procRequests, locale);
        
        docBean.setFiles(new ArrayList<>());
        docBean.setTextFiles(new ArrayList<>());
        docBean.setMetadataUrl(metadataService.getDocumentLink(doc));
        docBean.setPages(new ArrayList<>());
        docBean.setRequest(doc.getRequest());
        
        IFile origFile = fileManager.getFile(doc.getUploadedFileId());  
        if (origFile != null) {
            
            FilePageBean bean = fileMappingService.convertToT2(origFile, new FilePageBean());
            bean.setMetadataLink(metadataService.getFileLink(origFile));
            docBean.setUploadedFile(bean);
            
            setRequestStatus(bean, requestsByFileId);
        }
        
        IFile textFile = fileManager.getFile(doc.getExtractedTextFileId());
        if (textFile != null) {
            FilePageBean bean = fileMappingService.convertToT2(textFile, new FilePageBean());
            bean.setMetadataLink(metadataService.getFileLink(textFile));
            docBean.setExtractedTextFile(bean);
            setRequestStatus(bean, requestsByFileId);
        }
        
        for (IPage page : doc.getPages()) {
            PagePageBean bean = pageMappingService.convertToT2(page, new PagePageBean());
            docBean.getPages().add(bean);
            
            IFile imageFile = fileManager.getFile(page.getImageFileId());
            if (imageFile != null) {
                FilePageBean imageBean = fileMappingService.convertToT2(imageFile, new FilePageBean());
                imageBean.setMetadataLink(metadataService.getFileLink(imageFile));
                bean.setImageFile(imageBean);
                setRequestStatus(imageBean, requestsByFileId);
            }
            
            IFile pageTextFile = fileManager.getFile(page.getTextFileId());
            if (pageTextFile != null) {
                FilePageBean textBean = fileMappingService.convertToT2(pageTextFile, new FilePageBean());
                textBean.setMetadataLink(metadataService.getFileLink(pageTextFile));
                bean.setTextFile(textBean);
                setRequestStatus(textBean, requestsByFileId);
            }
            
            IFile ocrFile = fileManager.getFile(page.getOcrFileId());
            if (ocrFile != null) {
                FilePageBean ocrBean = fileMappingService.convertToT2(ocrFile, new FilePageBean());
                ocrBean.setMetadataLink(metadataService.getFileLink(ocrFile));
                bean.setOcrFile(ocrBean);
                setRequestStatus(ocrBean, requestsByFileId);
            }
        }
        
        return "documents/document";
    }
    
    private void setRequestStatus(FilePageBean bean, Map<String, List<IProcessingRequest>> requestsByFileId) {
        List<IProcessingRequest> fileReqs = requestsByFileId.get(bean.getId());
        for (IProcessingRequest req : fileReqs) {
            if (req.getSentRequest() instanceof StorageRequest) {
                bean.setStoredStatus(req.getRequestStatus());
            } else if (req.getSentRequest() instanceof TextExtractionRequest) {
                bean.setTextExtractionStatus(req.getRequestStatus());
            } else if (req.getSentRequest() instanceof ImageExtractionRequest) {
                bean.setImageExtractionStatus(req.getRequestStatus());
            } else if (req.getSentRequest() instanceof OCRRequest) {
                bean.setOcrStatus(req.getRequestStatus());
            }
        }
    }
}
