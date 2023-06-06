package edu.asu.diging.gilesecosystem.web.core.model;

import java.util.List;

import edu.asu.diging.gilesecosystem.util.store.IStorableObject;

public interface IDocument extends IStorableObject {

    public abstract String getId();

    public abstract void setId(String id);

    public abstract String getUploadId();

    public abstract void setUploadId(String uploadId);

    public abstract String getCreatedDate();

    public abstract void setCreatedDate(String createdDate);

    public abstract List<String> getFileIds();

    public abstract void setFileIds(List<String> fileIds);

    public abstract DocumentAccess getAccess();

    public abstract void setAccess(DocumentAccess access);

    public abstract void setFiles(List<IFile> files);

    public abstract List<IFile> getFiles();

    public abstract void setPageCount(int pageCount);

    public abstract int getPageCount();

    public abstract void setDocumentType(DocumentType documentType);

    public abstract DocumentType getDocumentType();

    public abstract void setTextFileIds(List<String> textFileIds);

    public abstract List<String> getTextFileIds();

    public abstract void setUsername(String username);

    public abstract String getUsername();

    public abstract void setPages(List<IPage> pages);

    public abstract List<IPage> getPages();

    public abstract void setUploadedFileId(String uploadedFile);

    public abstract String getUploadedFileId();

    public abstract void setExtractedTextFileId(String extractedText);

    public abstract String getExtractedTextFileId();

    public abstract void setTasks(List<ITask> tasks);

    public abstract List<ITask> getTasks();

    public abstract void setRequestId(String requestId);

    public abstract String getRequestId();
}
