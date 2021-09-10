package edu.asu.diging.gilesecosystem.web.core.service;

import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;

public interface IMetadataUrlService {

    public abstract String getUploadCallback();

    public abstract String getFileLink(IFile file);

    public abstract String getDocumentLink(IDocument doc);

}