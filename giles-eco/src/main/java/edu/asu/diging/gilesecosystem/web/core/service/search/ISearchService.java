package edu.asu.diging.gilesecosystem.web.core.service.search;

import java.util.List;

import edu.asu.diging.gilesecosystem.web.core.exceptions.NoFreddieInstanceConfigured;
import edu.asu.diging.gilesecosystem.web.core.exceptions.SearchException;

public interface ISearchService {

    public abstract List<FileSearchResult> searchWithUsername(String username,
            String query) throws NoFreddieInstanceConfigured, SearchException;

}