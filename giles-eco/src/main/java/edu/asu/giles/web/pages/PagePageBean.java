package edu.asu.giles.web.pages;

import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IPage;

public class PagePageBean implements IPage {

    private int pageNr;
    private String imageFileId;
    private String textFileId;
    
    private IFile imageFile;
    private IFile textFile;
    
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
    
    

}
