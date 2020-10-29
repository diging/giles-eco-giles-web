package edu.asu.diging.gilesecosystem.web.core.service;

import java.io.IOException;
import java.net.URL;

import edu.asu.diging.gilesecosystem.web.core.files.IFileStorageManager;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;

public interface IFileContentHelper {

    public abstract byte[] getFileContent(IFile file, IFileStorageManager storageManager);

    public abstract byte[] getFileContentFromUrl(URL url) throws IOException;

}