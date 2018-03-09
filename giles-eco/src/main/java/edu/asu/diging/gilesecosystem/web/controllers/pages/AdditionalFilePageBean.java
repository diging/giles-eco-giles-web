package edu.asu.diging.gilesecosystem.web.controllers.pages;

public class AdditionalFilePageBean {

    private String fileId;
    private String filename;
    private String processor;
    
    public AdditionalFilePageBean(String fileId) {
        this.fileId = fileId;
    }
    
    public AdditionalFilePageBean(String fileId, String filename, String processor) {
        super();
        this.fileId = fileId;
        this.filename = filename;
        this.processor = processor;
    }

    public String getFileId() {
        return fileId;
    }
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getProcessor() {
        return processor;
    }
    public void setProcessor(String processor) {
        this.processor = processor;
    }
    
}
