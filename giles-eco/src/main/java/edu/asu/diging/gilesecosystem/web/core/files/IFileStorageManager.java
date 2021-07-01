package edu.asu.diging.gilesecosystem.web.core.files;

import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesFileStorageException;

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

    /**
     * Deletes the file with the provided filename and the provided upload id and document
     * id from the file system.
     * @param username username of the owner of the file
     * @param uploadId id of the upload that the file was part of
     * @param documentId id of the document that the file belongs to
     * @param filename name of the file to be deleted
     * @param deleteEmptyFolders if set to true, the method will attempt to delete the folder
     *        structure the file is in as well (if the folders are empty).
     * @return true if the file was successfully deleted; otherwise false.
     */
    public abstract boolean deleteFile(String username, String uploadId, String documentId,
            String filename, boolean deleteEmptyFolders);

    public abstract void setBaseDirectory(String baseDirectory);

    public abstract String getBaseDirectory();

}