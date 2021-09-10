package edu.asu.diging.gilesecosystem.web.web.admin;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesMappingException;
import edu.asu.diging.gilesecosystem.web.core.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.core.files.IUploadDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.IUpload;
import edu.asu.diging.gilesecosystem.web.core.service.IGilesMappingService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalUploadService;
import edu.asu.diging.gilesecosystem.web.core.service.impl.GilesMappingService;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.core.util.IStatusHelper;
import edu.asu.diging.gilesecosystem.web.web.pages.Badge;
import edu.asu.diging.gilesecosystem.web.web.pages.UploadPageBean;

@Controller
public class RecentUploadsController {
    
    @Autowired
    private ITransactionalUploadService uploadService;
    
    @Autowired
    private IFilesManager filesManager;
    
    @Autowired
    private IStatusHelper statusHelper;

    @Autowired
    private IPropertiesManager propertiesManager;


    @RequestMapping(value = "/admin/uploads", method = RequestMethod.GET)
    public String showRecentUploads(Principal principal,
            Model model,
            @RequestParam(defaultValue = "1") String page,
            @RequestParam(defaultValue = IUploadDatabaseClient.DESCENDING + "") String sortDir) throws GilesMappingException {
        
        int pageInt = new Integer(page);
        int pageCount = 1;
        int sortDirInt = new Integer(sortDir);

        pageCount = uploadService.getUploadsPageCount();

        List<IUpload> uploads = uploadService.getUploads(pageInt, -1,
                "createdDate", sortDirInt);
        List<UploadPageBean> mappedUploads = new ArrayList<UploadPageBean>();

        if (uploads != null) {
            IGilesMappingService<IUpload, UploadPageBean> uploadMappingService = new GilesMappingService<>();
            for (IUpload up : uploads) {
                UploadPageBean upload = uploadMappingService.convertToT2(up,
                        new UploadPageBean());
                upload.setUploadedFiles(new ArrayList<>());

                List<IDocument> docs = filesManager
                        .getDocumentsByUploadId(up.getId());
                upload.setNrOfDocuments(docs.size());

                boolean inProgress = false;
                for (IDocument doc : docs) {
                    if (doc.getFiles() != null && doc.getFiles().size() > 0) {
                        IFile firstFile = doc.getFiles().get(0);
                        upload.getUploadedFiles().add(firstFile);
                    }

                    if (!statusHelper.isProcessingDone(doc)) {
                        inProgress = true;
                    }
                }

                String processingStatus = inProgress ? propertiesManager
                        .getProperty(Properties.BADGE_PROCESSING_UPLOAD_IN_PROGRESS)
                        : propertiesManager
                                .getProperty(Properties.BADGE_PROCESSING_UPLOAD_COMPLETE);
                upload.setStatus(new Badge(
                        propertiesManager
                                .getProperty(Properties.BADGE_PROCESSING_UPLOAD_LABEL),
                        processingStatus,
                        propertiesManager
                                .getProperty(Properties.BADGE_PROCESSING_UPLOAD_COLOR),
                        0));
                mappedUploads.add(upload);
            }
            
            model.addAttribute("count", pageCount);
            model.addAttribute("uploads", mappedUploads);
            model.addAttribute("totalUploads",
                    uploadService.getUploadsCount());
        }
        if (pageInt < 1) {
            pageInt = 1;
        }
        if (pageInt > pageCount) {
            pageInt = pageCount;
        }
        model.addAttribute("page", pageInt);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute(
                "oppSortDir",
                sortDirInt == IUploadDatabaseClient.ASCENDING ? IUploadDatabaseClient.DESCENDING
                        : IUploadDatabaseClient.ASCENDING);
        
        return "admin/uploads";
    }
}
