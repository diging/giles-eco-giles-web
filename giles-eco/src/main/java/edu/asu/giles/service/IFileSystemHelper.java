package edu.asu.giles.service;

import java.io.IOException;
import java.net.URL;

import edu.asu.giles.core.IFile;
import edu.asu.giles.files.IFileStorageManager;

public interface IFileSystemHelper {

    public abstract byte[] getFileContent(IFile file, IFileStorageManager storageManager);

    public abstract byte[] getFileContentFromUrl(URL url) throws IOException;

}