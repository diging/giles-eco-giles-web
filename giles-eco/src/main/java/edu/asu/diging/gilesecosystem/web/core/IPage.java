package edu.asu.diging.gilesecosystem.web.core;

/**
 * @deprecated
 *      Use {@link edu.asu.diging.gilesecosystem.web.domain.IPage} instead. This
 *      class is only kept for migration purposes.
 * @author jdamerow
 *
 */
@Deprecated
public interface IPage {

    public abstract int getPageNr();

    public abstract void setPageNr(int pageNr);

    public abstract String getImageFileId();

    public abstract void setImageFileId(String imageFileId);

    public abstract String getTextFileId();

    public abstract void setTextFileId(String textFileId);

    public abstract void setOcrFileId(String ocrFileId);

    public abstract String getOcrFileId();

}