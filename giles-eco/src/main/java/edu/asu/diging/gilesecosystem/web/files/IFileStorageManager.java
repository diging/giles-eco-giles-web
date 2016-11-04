package edu.asu.diging.gilesecosystem.web.files;

import edu.asu.diging.gilesecosystem.web.exceptions.GilesFileStorageException;

public interface IFileStorageManager {

    public abstract void saveFile(String username, String uploadId,
            String documentId, String filename, byte[] bytes)
            throws GilesFileStorageException;

    /**
     * Method that returns the path of a file in the digilib folder structure.
     * Note, this method does not return an absolute path and it does not
     * include the digilib base directory.
     * 
     */
    public abstract String getFileFolderPath(String username, String uploadId,
            String documentId);

    /**
     * Method to get the absolute path to a file directory. This method makes
     * sure that the path exists and all necessary directories are created.
     * 
     * @param username
     *            username of user who uploaded an image
     * @param uploadId
     *            id of upload a file was part of
     * @param documentId
     *            id of document
     * @return absolute path to the file directory
     */
    public abstract String getAndCreateStoragePath(String username,
            String uploadId, String documentId);

}