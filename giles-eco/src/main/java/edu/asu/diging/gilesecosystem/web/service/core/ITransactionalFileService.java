package edu.asu.diging.gilesecosystem.web.service.core;

import java.util.List;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.domain.IFile;

public interface ITransactionalFileService {

    String generateRequestId();

    void saveFile(IFile file) throws UnstorableObjectException;

    String generateFileId();

    IFile getFileById(String id);

    IFile getFileByRequestId(String requestId);

    IFile getFileByPath(String path);

    String generateRequestId(String prefix);

    List<IFile> getFilesByDerivedFrom(String derivedFromId);

}