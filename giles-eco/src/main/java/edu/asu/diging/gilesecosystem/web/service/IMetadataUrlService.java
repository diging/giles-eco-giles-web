package edu.asu.diging.gilesecosystem.web.service;

import edu.asu.diging.gilesecosystem.web.domain.IDocument;
import edu.asu.diging.gilesecosystem.web.domain.IFile;

public interface IMetadataUrlService {

    public abstract String getUploadCallback();

    public abstract String getFileLink(IFile file);

    public abstract String getDocumentLink(IDocument doc);

}