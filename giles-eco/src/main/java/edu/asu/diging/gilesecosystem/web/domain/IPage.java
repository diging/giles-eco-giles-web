package edu.asu.diging.gilesecosystem.web.domain;


public interface IPage {

    public abstract int getPageNr();

    public abstract void setPageNr(int pageNr);

    public abstract String getImageFileId();

    public abstract void setImageFileId(String imageFileId);

    public abstract String getTextFileId();

    public abstract void setTextFileId(String textFileId);

    public abstract void setOcrFileId(String ocrFileId);

    public abstract String getOcrFileId();

    void setDocument(IDocument document);

    IDocument getDocument();

    void setOcrFileStatus(PageStatus ocrFileStatus);

    PageStatus getOcrFileStatus();

    void setTextFileStatus(PageStatus textFileStatus);

    PageStatus getTextFileStatus();

    void setImageFileStatus(PageStatus imageFileStatus);

    PageStatus getImageFileStatus();

    void setTextFileErrorMsg(String textFileErrorMsg);

    String getTextFileErrorMsg();

    void setOcrFileErrorMsg(String ocrFileErrorMsg);

    String getOcrFileErrorMsg();

    void setImageFileErrorMsg(String imageFileErrorMsg);

    String getImageFileErrorMsg();

}