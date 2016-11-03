package edu.asu.giles.service;

import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.service.properties.IPropertiesManager;

public interface IMetadataUrlService {

    public abstract String getUploadCallback();

    public abstract String getFileLink(IFile file);

    public abstract String getDocumentLink(IDocument doc);

}