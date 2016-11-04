package edu.asu.diging.gilesecosystem.web.service;

import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;

public interface IMetadataUrlService {

    public abstract String getUploadCallback();

    public abstract String getFileLink(IFile file);

    public abstract String getDocumentLink(IDocument doc);

}