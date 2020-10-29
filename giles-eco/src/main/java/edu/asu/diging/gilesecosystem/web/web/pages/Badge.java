package edu.asu.diging.gilesecosystem.web.web.pages;

public class Badge {

    private String subject;
    private String status;
    private String color;
    private int order;
    private String fileId;
    
    public Badge(String subject, String status, String color, int order) {
        super();
        this.subject = subject;
        this.status = status;
        this.color = color;
        this.order = order;
    }
    
    public Badge(String subject, String status, String color, int order, String fileId) {
        super();
        this.subject = subject;
        this.status = status;
        this.color = color;
        this.order = order;
        this.fileId = fileId;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
