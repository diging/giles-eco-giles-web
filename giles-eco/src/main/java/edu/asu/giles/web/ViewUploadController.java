package edu.asu.giles.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.giles.aspects.access.annotations.AccountCheck;
import edu.asu.giles.aspects.access.annotations.UploadIdAccessCheck;
import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IUpload;
import edu.asu.giles.exceptions.GilesMappingException;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.service.IGilesMappingService;
import edu.asu.giles.service.IMetadataUrlService;
import edu.asu.giles.service.impl.GilesMappingService;
import edu.asu.giles.web.pages.DocumentPageBean;
import edu.asu.giles.web.pages.FilePageBean;
import edu.asu.giles.web.util.StatusHelper;

@Controller
public class ViewUploadController {

    @Autowired
    private IFilesManager filesManager;
    
    @Autowired
    private IMetadataUrlService metadataService;
    
    @Autowired
    private StatusHelper statusHelper;
    
    @AccountCheck
    @UploadIdAccessCheck
    @RequestMapping(value = "/uploads/{uploadId}")
    public String showUploadPage(@PathVariable("uploadId") String uploadId,
            Model model, Locale locale) throws GilesMappingException {
        IUpload upload = filesManager.getUpload(uploadId);
        List<IDocument> docs = filesManager.getDocumentsByUploadId(uploadId);
        
        IGilesMappingService<IFile, FilePageBean> fileMappingService = new GilesMappingService<>();
        IGilesMappingService<IDocument, DocumentPageBean> docMappingService = new GilesMappingService<>();
        
        List<DocumentPageBean> mappedDocs = new ArrayList<DocumentPageBean>();
        for (IDocument doc : docs) {
            DocumentPageBean docBean = docMappingService.convertToT2(doc, new DocumentPageBean());
            mappedDocs.add(docBean);
            docBean.setFiles(new ArrayList<>());
            docBean.setTextFiles(new ArrayList<>());
            docBean.setMetadataUrl(metadataService.getDocumentLink(doc));
            docBean.setRequest(doc.getRequest());
            docBean.setStatusLabel(statusHelper.getLabelText(doc.getRequest().getStatus(), locale));
            
            IFile origFile = filesManager.getFile(doc.getUploadedFileId());  
            if (origFile != null) {
                FilePageBean bean = fileMappingService.convertToT2(origFile, new FilePageBean());
                bean.setMetadataLink(metadataService.getFileLink(origFile));
                docBean.setUploadedFile(bean);
            }
            
            IFile textFile = filesManager.getFile(doc.getExtractedTextFileId());
            if (textFile != null) {
                FilePageBean bean = fileMappingService.convertToT2(textFile, new FilePageBean());
                bean.setMetadataLink(metadataService.getFileLink(textFile));
                docBean.setExtractedTextFile(bean);
            }
            
            for (IFile file : doc.getFiles()) {
                FilePageBean bean = fileMappingService.convertToT2(file, new FilePageBean());
                bean.setMetadataLink(metadataService.getFileLink(file));
                docBean.getFiles().add(bean);
            }
            if (doc.getTextFileIds() != null) {
                for (String fileId : doc.getTextFileIds()) {
                    IFile file = filesManager.getFile(fileId);
                    FilePageBean bean = fileMappingService.convertToT2(file, new FilePageBean());
                    bean.setMetadataLink(metadataService.getFileLink(file));
                    docBean.getTextFiles().add(bean);
                }
            }
        }
        
        model.addAttribute("upload", upload);
        model.addAttribute("docs", mappedDocs);

        return "uploads/upload";
    }
}
