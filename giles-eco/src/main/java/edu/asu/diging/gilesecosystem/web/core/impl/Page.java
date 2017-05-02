package edu.asu.diging.gilesecosystem.web.core.impl;

import javax.persistence.Embeddable;

import edu.asu.diging.gilesecosystem.web.core.IPage;

/**
 * @deprecated
 *      Use {@link edu.asu.diging.gilesecosystem.web.domain.impl.Page} instead. This
 *      class is only kept for migration purposes.
 * @author jdamerow
 *
 */
@Deprecated
@Embeddable
public class Page implements IPage {
    
    private int pageNr;
    private String imageFileId;
    private String textFileId;
    private String ocrFileId;
    
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
    
}
