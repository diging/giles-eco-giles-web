package edu.asu.giles.web;

import java.util.ArrayList;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.giles.aspects.access.annotations.AccountCheck;
import edu.asu.giles.aspects.access.annotations.DocumentIdAccessCheck;
import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IPage;
import edu.asu.giles.exceptions.GilesMappingException;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.service.IGilesMappingService;
import edu.asu.giles.service.IMetadataUrlService;
import edu.asu.giles.service.impl.GilesMappingService;
import edu.asu.giles.web.pages.DocumentPageBean;
import edu.asu.giles.web.pages.FilePageBean;
import edu.asu.giles.web.pages.PagePageBean;
import edu.asu.giles.web.util.StatusHelper;

@Controller
public class ViewDocumentController {
    
    @Autowired
    private IFilesManager fileManager;
    
    @Autowired
    private IMetadataUrlService metadataService;  
    
    @Autowired
    private StatusHelper statusHelper;

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
        
        docBean.setFiles(new ArrayList<>());
        docBean.setTextFiles(new ArrayList<>());
        docBean.setMetadataUrl(metadataService.getDocumentLink(doc));
        docBean.setPages(new ArrayList<>());
        docBean.setRequest(doc.getRequest());
        docBean.setStatusLabel(statusHelper.getLabelText(doc.getRequest().getStatus(), locale));
        
        
        IFile origFile = fileManager.getFile(doc.getUploadedFileId());  
        if (origFile != null) {
            FilePageBean bean = fileMappingService.convertToT2(origFile, new FilePageBean());
            bean.setMetadataLink(metadataService.getFileLink(origFile));
            docBean.setUploadedFile(bean);
        }
        
        IFile textFile = fileManager.getFile(doc.getExtractedTextFileId());
        if (textFile != null) {
            FilePageBean bean = fileMappingService.convertToT2(textFile, new FilePageBean());
            bean.setMetadataLink(metadataService.getFileLink(textFile));
            docBean.setExtractedTextFile(bean);
        }
        
        for (IPage page : doc.getPages()) {
            PagePageBean bean = pageMappingService.convertToT2(page, new PagePageBean());
            docBean.getPages().add(bean);
            
            IFile imageFile = fileManager.getFile(page.getImageFileId());
            FilePageBean imageBean = fileMappingService.convertToT2(imageFile, new FilePageBean());
            imageBean.setMetadataLink(metadataService.getFileLink(imageFile));
            bean.setImageFile(imageBean);
            
            IFile pageTextFile = fileManager.getFile(page.getTextFileId());
            FilePageBean textBean = fileMappingService.convertToT2(pageTextFile, new FilePageBean());
            textBean.setMetadataLink(metadataService.getFileLink(pageTextFile));
            bean.setTextFile(textBean);
        }
        
        return "documents/document";
    }
}
