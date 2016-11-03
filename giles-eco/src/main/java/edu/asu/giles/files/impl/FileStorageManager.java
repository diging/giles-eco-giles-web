package edu.asu.giles.files.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.asu.giles.exceptions.GilesFileStorageException;
import edu.asu.giles.files.IFileStorageManager;

public class FileStorageManager implements IFileStorageManager {

    private String baseDirectory;

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.files.impl.IFileSystemManager#saveFile(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, byte[])
     */
    @Override
    public void saveFile(String username, String uploadId, String documentId,
            String filename, byte[] bytes) throws GilesFileStorageException {
        String filePath = getAndCreateStoragePath(username, uploadId,
                documentId);

        File file = new File(filePath + File.separator + filename);
        BufferedOutputStream stream;
        try {
            stream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            throw new GilesFileStorageException("Could not store file.", e);
        }
        try {
            stream.write(bytes);
            stream.close();
        } catch (IOException e) {
            throw new GilesFileStorageException("Could not store file.", e);
        }
    }

    @Override
    public String getAndCreateStoragePath(String username, String uploadId,
            String documentId) {
        String path = baseDirectory + File.separator
                + getFileFolderPath(username, uploadId, documentId);
        createDirectory(path);
        return path;
    }

    @Override
    public String getFileFolderPath(String username, String uploadId,
            String fileId) {
        StringBuffer filePath = new StringBuffer();
        filePath.append(username);
        filePath.append(File.separator);
        filePath.append(uploadId);
        filePath.append(File.separator);
        filePath.append(fileId);

        return filePath.toString();
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }

    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    private boolean createDirectory(String dirPath) {

        File dirFile = new File(dirPath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return true;
    }

}
