package edu.asu.diging.gilesecosystem.web.core.files.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.store.objectdb.DatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.files.IFilesDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.impl.File;
import edu.asu.diging.gilesecosystem.web.core.repository.FileRepository;

@Component
public class FilesDatabaseClient extends DatabaseClient<IFile> implements
        IFilesDatabaseClient {
    
    private final FileRepository fileRepository;

    @Autowired
    public FilesDatabaseClient(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.files.IFilesDatabaseClient#addFile(edu.asu.giles.core.impl
     * .File)
     */
    @Override
    public IFile saveFile(IFile file) throws IllegalArgumentException, UnstorableObjectException {
        IFile existingFile = getFileById(file.getId());
        if(existingFile == null) {
            if (file.getId() == null) {
                throw new UnstorableObjectException("The object does not have an id.");
            }
        }
        return fileRepository.save((File) file);
    }

    @Override
    public IFile getFileById(String id) {
        return fileRepository.findById(id).orElse(null);
    }
    
    @Override
    public List<IFile> getFilesForIds(List<String> ids) {
        List<File> files = fileRepository.findByIdIn(ids);
        return files.stream().map(file -> (IFile) file).collect(Collectors.toList());
    }

    @Override
    public List<IFile> getFilesByUploadId(String uploadId) {
        return fileRepository.findByUploadId(uploadId);
    }

    @Override
    public List<IFile> getFilesByUsername(String username) {
        return fileRepository.findByUsername(username);
    }

    @Override
    public List<IFile> getFilesByPath(String path) {
        return fileRepository.findByFilepath(path);
    }
    
    @Override
    public List<IFile> getFilesByDerivedFrom(String derivedFromId) {
        return fileRepository.findByDerivedFrom(derivedFromId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.files.IFilesDatabaseClient#getFile(java.lang.String)
     */
    @Override
    public IFile getFile(String filename) {
        return fileRepository.findByFilename(filename);
    }

    @Override
    public IFile getFileByRequestId(String requestId) {
        return fileRepository.findByRequestId(requestId);
    }

    @Override
    protected String getIdPrefix() {
        return "FILE";
    }

    @Override
    protected IFile getById(String id) {
        return getFileById(id);
    }

    @Override
    protected EntityManager getClient() {
        return null;
    }

}
