package edu.asu.diging.gilesecosystem.web.controllers.pages;

import edu.asu.diging.gilesecosystem.web.domain.IDocument;
import edu.asu.diging.gilesecosystem.web.domain.IFile;
import edu.asu.diging.gilesecosystem.web.domain.IPage;
import edu.asu.diging.gilesecosystem.web.domain.PageStatus;

public class PagePageBean implements IPage {

    private int pageNr;
    private String imageFileId;
    private String textFileId;
    private String ocrFileId;
    
    private PageStatus imageFileStatus;
    private PageStatus textFileStatus;
    private PageStatus ocrFileStatus;
    
    private String imageFileErrorMsg;
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
    private String textFileErrorMsg;
    private String ocrFileErrorMsg;
    
    private IFile imageFile;
    private IFile textFile;
    private IFile ocrFile;
    
    
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

}
