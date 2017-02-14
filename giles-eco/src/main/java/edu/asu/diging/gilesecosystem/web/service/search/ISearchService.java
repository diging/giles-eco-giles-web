package edu.asu.diging.gilesecosystem.web.service.search;

import java.util.List;

import edu.asu.diging.gilesecosystem.web.exceptions.NoFreddieInstanceConfigured;
import edu.asu.diging.gilesecosystem.web.exceptions.SearchException;

public interface ISearchService {

    public abstract List<FileSearchResult> searchWithUsername(String username,
            String query) throws NoFreddieInstanceConfigured, SearchException;

}