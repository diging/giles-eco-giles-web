package edu.asu.giles.core;


public interface IPage {

    public abstract int getPageNr();

    public abstract void setPageNr(int pageNr);

    public abstract String getImageFileId();

    public abstract void setImageFileId(String imageFileId);

    public abstract String getTextFileId();

    public abstract void setTextFileId(String textFileId);

}