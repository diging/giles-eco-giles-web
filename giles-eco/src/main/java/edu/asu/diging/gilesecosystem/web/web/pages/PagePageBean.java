package edu.asu.diging.gilesecosystem.web.web.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.IPage;
import edu.asu.diging.gilesecosystem.web.core.model.PageStatus;

public class PagePageBean implements IPage, BeanWithAdditionalFiles {

    private int pageNr;
    private String imageFileId;
    private String textFileId;
    private String ocrFileId;
    
    private PageStatus imageFileStatus;
    private PageStatus textFileStatus;
    private PageStatus ocrFileStatus;
    
    private String imageFileErrorMsg;
    private String textFileErrorMsg;
    private String ocrFileErrorMsg;
    
    private IFile imageFile;
    private IFile textFile;
    private IFile ocrFile;
    
    private Map<String, List<AdditionalFilePageBean>> additionalFiles = new HashMap<>();  
    
    /* (non-Javadoc)
     * @see edu.asu.giles.core.impl.IPage#getPageNr()
     */
    @Override
    public int getPageNr() {
        return pageNr;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.core.impl.IPage#setPageNr(int)
     */
    @Override
    public void setPageNr(int pageNr) {
        this.pageNr = pageNr;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.core.impl.IPage#getImageFileId()
     */
    @Override
    public String getImageFileId() {
        return imageFileId;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.core.impl.IPage#setImageFileId(java.lang.String)
     */
    @Override
    public void setImageFileId(String imageFileId) {
        this.imageFileId = imageFileId;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.core.impl.IPage#getTextFileId()
     */
    @Override
    public String getTextFileId() {
        return textFileId;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.core.impl.IPage#setTextFileId(java.lang.String)
     */
    @Override
    public void setTextFileId(String textFileId) {
        this.textFileId = textFileId;
    }
    public IFile getImageFile() {
        return imageFile;
    }
    public void setImageFile(IFile imageFile) {
        this.imageFile = imageFile;
    }
    public IFile getTextFile() {
        return textFile;
    }
    public void setTextFile(IFile textFile) {
        this.textFile = textFile;
    }
    public void setOcrFileId(String ocrFileId) {
        this.ocrFileId = ocrFileId;
    }
    public String getOcrFileId() {
        return ocrFileId;
    }
    public IFile getOcrFile() {
        return ocrFile;
    }
    public void setOcrFile(IFile ocrFile) {
        this.ocrFile = ocrFile;
    }
    
    @Override
    public void setDocument(IDocument document) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public IDocument getDocument() {
        // TODO Auto-generated method stub
        return null;
    }
    public PageStatus getImageFileStatus() {
        return imageFileStatus;
    }
    public void setImageFileStatus(PageStatus imageFileStatus) {
        this.imageFileStatus = imageFileStatus;
    }
    public PageStatus getTextFileStatus() {
        return textFileStatus;
    }
    public void setTextFileStatus(PageStatus textFileStatus) {
        this.textFileStatus = textFileStatus;
    }
    public PageStatus getOcrFileStatus() {
        return ocrFileStatus;
    }
    public void setOcrFileStatus(PageStatus ocrFileStatus) {
        this.ocrFileStatus = ocrFileStatus;
    }
    public String getImageFileErrorMsg() {
        return imageFileErrorMsg;
    }
    public void setImageFileErrorMsg(String imageFileErrorMsg) {
        this.imageFileErrorMsg = imageFileErrorMsg;
    }
    public String getTextFileErrorMsg() {
        return textFileErrorMsg;
    }
    public void setTextFileErrorMsg(String textFileErrorMsg) {
        this.textFileErrorMsg = textFileErrorMsg;
    }
    public String getOcrFileErrorMsg() {
        return ocrFileErrorMsg;
    }
    public void setOcrFileErrorMsg(String ocrFileErrorMsg) {
        this.ocrFileErrorMsg = ocrFileErrorMsg;
    }
    @Override
    public void setAdditionalFileIds(List<String> additionalFileIds) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public List<String> getAdditionalFileIds() {
        // TODO Auto-generated method stub
        return null;
    }
    public Map<String, List<AdditionalFilePageBean>> getAdditionalFiles() {
        return additionalFiles;
    }
    public void setAdditionalFiles(Map<String, List<AdditionalFilePageBean>> additionalFiles) {
        this.additionalFiles = additionalFiles;
    } 
}
