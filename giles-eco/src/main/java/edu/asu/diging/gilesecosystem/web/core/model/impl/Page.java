package edu.asu.diging.gilesecosystem.web.core.model.impl;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IPage;
import edu.asu.diging.gilesecosystem.web.core.model.PageStatus;

@Entity
public class Page implements IPage {
    
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE) private Integer id;
    private int pageNr;
    private String imageFileId;
    private String textFileId;
    private String ocrFileId;
    
    @Enumerated(EnumType.STRING)
    private PageStatus imageFileStatus;
    private String imageFileErrorMsg;
    
    @Enumerated(EnumType.STRING)
    private PageStatus textFileStatus;
    private String textFileErrorMsg;
    
    @Enumerated(EnumType.STRING)
    private PageStatus ocrFileStatus;
    private String ocrFileErrorMsg;
    
    @ManyToOne(fetch = FetchType.LAZY, targetEntity=Document.class)
    @JoinColumn(name = "document_id", nullable = false)
    private IDocument document;
    
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
    @Override
    public String getOcrFileId() {
        return ocrFileId;
    }
    @Override
    public void setOcrFileId(String ocrFileId) {
        this.ocrFileId = ocrFileId;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    @Override
    public IDocument getDocument() {
        return document;
    }
    @Override
    public void setDocument(IDocument document) {
        this.document = document;
    }
    @Override
    public PageStatus getImageFileStatus() {
        return imageFileStatus;
    }
    @Override
    public void setImageFileStatus(PageStatus imageFileStatus) {
        this.imageFileStatus = imageFileStatus;
    }
    @Override
    public PageStatus getTextFileStatus() {
        return textFileStatus;
    }
    @Override
    public void setTextFileStatus(PageStatus textFileStatus) {
        this.textFileStatus = textFileStatus;
    }
    @Override
    public PageStatus getOcrFileStatus() {
        return ocrFileStatus;
    }
    @Override
    public void setOcrFileStatus(PageStatus ocrFileStatus) {
        this.ocrFileStatus = ocrFileStatus;
    }
    @Override
    public String getImageFileErrorMsg() {
        return imageFileErrorMsg;
    }
    @Override
    public void setImageFileErrorMsg(String imageFileErrorMsg) {
        this.imageFileErrorMsg = imageFileErrorMsg;
    }
    @Override
    public String getOcrFileErrorMsg() {
        return ocrFileErrorMsg;
    }
    @Override
    public void setOcrFileErrorMsg(String ocrFileErrorMsg) {
        this.ocrFileErrorMsg = ocrFileErrorMsg;
    }
    @Override
    public String getTextFileErrorMsg() {
        return textFileErrorMsg;
    }
    @Override
    public void setTextFileErrorMsg(String textFileErrorMsg) {
        this.textFileErrorMsg = textFileErrorMsg;
    }
}
