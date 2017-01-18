package edu.asu.diging.gilesecosystem.web.files.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.asu.diging.gilesecosystem.web.exceptions.GilesFileStorageException;
import edu.asu.diging.gilesecosystem.web.files.IFileStorageManager;

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
            String documentId) {
        StringBuffer filePath = new StringBuffer();
        filePath.append(username);
        filePath.append(File.separator);
        filePath.append(uploadId);
        filePath.append(File.separator);
        filePath.append(documentId);

        return filePath.toString();
    }
    
    public String getFolderPath(String username, String uploadId, String documentId) {
        StringBuffer path = new StringBuffer(baseDirectory + File.separator);
        
        path.append(username);
        if (uploadId == null) {
            return path.toString();
        }
        
        path.append(File.separator);
        path.append(uploadId);
        if (documentId == null) {
            return path.toString();
        }
        
        path.append(File.separator);
        path.append(documentId);
        return path.toString();
    }
    
    @Override
    public boolean deleteFile(String username, String uploadId, String documentId,
            String filename, boolean deleteEmptyFolders) {
        String path = baseDirectory + File.separator
                + getFileFolderPath(username, uploadId, documentId);
        File file = new File(path + File.separator + filename);
        
        if (file.exists()) {
            file.delete();
        }
        
        if (deleteEmptyFolders) {
            File docFolder = new File(getFolderPath(username, uploadId, documentId));
            if (docFolder.isDirectory() && docFolder.list().length == 0) {
                boolean deletedDocFolder = docFolder.delete();
                if (deletedDocFolder) {
                    File uploadFolder = new File(getFolderPath(username, uploadId, null));
                    // we now this is a folder because we just deleted docfolder from it
                    // so no need to check
                    if (uploadFolder.list().length == 0) {
                        uploadFolder.delete();
                    }
                }
            } 
        }
        
        return true;
    }

    @Override
    public String getBaseDirectory() {
        return baseDirectory;
    }

    @Override
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
