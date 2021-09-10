package edu.asu.diging.gilesecosystem.web.core.service.search;

import java.util.List;

import edu.asu.diging.gilesecosystem.web.core.exceptions.NoFreddieInstanceConfigured;
import edu.asu.diging.gilesecosystem.web.core.exceptions.SearchException;
import edu.asu.diging.gilesecosystem.web.core.service.search.impl.SearchResult;

public interface ISearchConnector {

    public abstract List<SearchResult> search(String username, String searchTerms)
            throws NoFreddieInstanceConfigured, SearchException;

}