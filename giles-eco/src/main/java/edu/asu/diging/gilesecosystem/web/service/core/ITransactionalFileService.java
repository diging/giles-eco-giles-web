package edu.asu.diging.gilesecosystem.web.service.core;

import java.util.List;
import java.util.Map;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.domain.IFile;
import edu.asu.diging.gilesecosystem.web.domain.IPage;

public interface ITransactionalFileService {

    String generateRequestId();

    void saveFile(IFile file) throws UnstorableObjectException;

    String generateFileId();

    IFile getFileById(String id);

    IFile getFileByRequestId(String requestId);

    IFile getFileByPath(String path);

    String generateRequestId(String prefix);

    List<IFile> getFilesByDerivedFrom(String derivedFromId);

    Map<String, IFile> getFilesForPage(IPage page);

}