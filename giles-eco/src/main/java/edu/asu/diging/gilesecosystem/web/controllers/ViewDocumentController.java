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
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.aspects.access.annotations.AccountCheck;
import edu.asu.diging.gilesecosystem.web.aspects.access.annotations.DocumentIdAccessCheck;
import edu.asu.diging.gilesecosystem.web.controllers.pages.AdditionalFilePageBean;
import edu.asu.diging.gilesecosystem.web.controllers.pages.Badge;
import edu.asu.diging.gilesecosystem.web.controllers.pages.DocumentPageBean;
import edu.asu.diging.gilesecosystem.web.controllers.pages.FilePageBean;
import edu.asu.diging.gilesecosystem.web.controllers.pages.PagePageBean;
import edu.asu.diging.gilesecosystem.web.controllers.util.StatusBadgeHelper;
import edu.asu.diging.gilesecosystem.web.domain.IDocument;
import edu.asu.diging.gilesecosystem.web.domain.IFile;
import edu.asu.diging.gilesecosystem.web.domain.IPage;
import edu.asu.diging.gilesecosystem.web.domain.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.domain.ITask;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesMappingException;
import edu.asu.diging.gilesecosystem.web.files.IProcessingRequestsDatabaseClient;
import edu.asu.diging.gilesecosystem.web.service.IGilesMappingService;
import edu.asu.diging.gilesecosystem.web.service.IMetadataUrlService;
import edu.asu.diging.gilesecosystem.web.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.service.impl.GilesMappingService;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;

@Controller
public class ViewDocumentController {

    @Autowired
    private ITransactionalDocumentService documentService;

    @Autowired
    private ITransactionalFileService fileService;

    @Autowired
    private IMetadataUrlService metadataService;

    @Autowired
    private StatusBadgeHelper statusHelper;

    @Autowired
    private IProcessingRequestsDatabaseClient procReqDbClient;

    @Autowired
    private IPropertiesManager propertiesManager;

    @AccountCheck
    @DocumentIdAccessCheck
    @RequestMapping(value = {"/documents/{docId}"}, method = RequestMethod.GET)
    public String showDocument(@PathVariable String docId, Model model, Locale locale)
            throws GilesMappingException {
        IDocument doc = documentService.getDocument(docId);

        IGilesMappingService<IFile, FilePageBean> fileMappingService = new GilesMappingService<>();
        IGilesMappingService<IDocument, DocumentPageBean> docMappingService = new GilesMappingService<>();
        IGilesMappingService<IPage, PagePageBean> pageMappingService = new GilesMappingService<>();

        DocumentPageBean docBean = docMappingService.convertToT2(doc,
                new DocumentPageBean());
        model.addAttribute("document", docBean);

        List<IProcessingRequest> procRequests = procReqDbClient
                .getRequestByDocumentId(doc.getId());
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

        statusHelper.createBadges(docBean, procRequests);
        statusHelper.createExternalBadges(docBean);

        Map<String, List<Badge>> badgesByFile = new HashMap<>();
        docBean.getExternalBadges().forEach(b -> {
            if (badgesByFile.get(b.getFileId()) == null) {
                badgesByFile.put(b.getFileId(), new ArrayList<>());
            }
            badgesByFile.get(b.getFileId()).add(b);
        });

        docBean.setFiles(new ArrayList<>());
        docBean.setTextFiles(new ArrayList<>());
        docBean.setMetadataUrl(metadataService.getDocumentLink(doc));
        docBean.setPages(new ArrayList<>());
        
        List<String> ids = new ArrayList<>();
        docBean.getTasks().forEach(t -> ids.add(t.getResultFileId()));
        Map<String, IFile> additionalFilesMap = fileService.getFilesForIds(ids);
        
        
        IFile origFile = fileService.getFileById(doc.getUploadedFileId());
        if (origFile != null) {
            FilePageBean bean = createFilePageBean(fileMappingService, requestsByFileId,
                    badgesByFile, origFile, docBean.getTasks(), additionalFilesMap);
            docBean.setUploadedFile(bean);
        }

        IFile textFile = fileService.getFileById(doc.getExtractedTextFileId());
        if (textFile != null) {
            FilePageBean bean = createFilePageBean(fileMappingService, requestsByFileId,
                    badgesByFile, textFile, docBean.getTasks(), additionalFilesMap);
            docBean.setExtractedTextFile(bean);
        }
        
        for (IPage page : doc.getPages()) {
            PagePageBean bean = pageMappingService.convertToT2(page, new PagePageBean());
            docBean.getPages().add(bean);
            
            Map<String, IFile> pageFiles = fileService.getFilesForPage(page);

            IFile imageFile = pageFiles.get(page.getImageFileId());
            if (imageFile != null) {
                FilePageBean pageBean = createFilePageBean(fileMappingService,
                        requestsByFileId, badgesByFile, imageFile, docBean.getTasks(), additionalFilesMap);
                bean.setImageFile(pageBean);

            }

            IFile pageTextFile = pageFiles.get(page.getTextFileId());
            if (pageTextFile != null) {
                FilePageBean textBean = createFilePageBean(fileMappingService,
                        requestsByFileId, badgesByFile, pageTextFile, docBean.getTasks(), additionalFilesMap);
                bean.setTextFile(textBean);
            }

            IFile ocrFile = pageFiles.get(page.getOcrFileId());
            if (ocrFile != null) {
                FilePageBean ocrBean = createFilePageBean(fileMappingService,
                        requestsByFileId, badgesByFile, ocrFile, docBean.getTasks(), additionalFilesMap);
                bean.setOcrFile(ocrBean);
            }
        }

        return "documents/document";
    }

    private FilePageBean createFilePageBean(
            IGilesMappingService<IFile, FilePageBean> fileMappingService,
            Map<String, List<IProcessingRequest>> requestsByFileId,
            Map<String, List<Badge>> badgesByFile, IFile file, List<ITask> tasks, Map<String, IFile> additionalFiles)
            throws GilesMappingException {
        FilePageBean pageBean = fileMappingService.convertToT2(file, new FilePageBean());
        pageBean.setMetadataLink(metadataService.getFileLink(file));
        setRequestStatus(pageBean, requestsByFileId);
        pageBean.setBadges(badgesByFile.get(pageBean.getId()));
        addAdditionalFiles(pageBean, tasks, requestsByFileId, additionalFiles);
        return pageBean;
    }

    private void addAdditionalFiles(FilePageBean bean, List<ITask> tasks, 
            Map<String, List<IProcessingRequest>> requestsByFileId, Map<String, IFile> additionalFiles) {
        tasks.forEach(t -> {
            IFile additionalFile = additionalFiles.get(t.getResultFileId());
            if (additionalFile != null) {   
                if (bean.getId().equals(additionalFile.getDerivedFrom())) {
                    AdditionalFilePageBean additionalFileBean = new AdditionalFilePageBean(t.getResultFileId(),
                            additionalFile.getFilename(),
                            propertiesManager.getProperty(propertiesManager
                                    .getProperty(Properties.EXTERNAL_BADGE_PREFIX)
                                    + t.getTaskHandlerId()));
                            
                    
                    List<IProcessingRequest> reqs = requestsByFileId.get(additionalFileBean.getFileId());
                    // for now we are going to assume additional files are only being stored
                    for (IProcessingRequest req : reqs) {
                        if (req.getSentRequest() instanceof StorageRequest) {
                            additionalFileBean.setStatus(req.getRequestStatus());
                        }
                    }
                    
                    
                    bean.getAdditionalFiles().put(t.getTaskHandlerId(), additionalFileBean);
                }
            }
        });
    }

    private void setRequestStatus(FilePageBean bean,
            Map<String, List<IProcessingRequest>> requestsByFileId) {
        List<IProcessingRequest> fileReqs = requestsByFileId.get(bean.getId());
        if (fileReqs == null) {
            return;
        }
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
