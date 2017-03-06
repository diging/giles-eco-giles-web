package edu.asu.diging.gilesecosystem.web.service.search;

import java.util.List;

import edu.asu.diging.gilesecosystem.web.exceptions.NoFreddieInstanceConfigured;
import edu.asu.diging.gilesecosystem.web.exceptions.SearchException;
import edu.asu.diging.gilesecosystem.web.service.search.impl.SearchResult;

public interface ISearchConnector {

    public abstract List<SearchResult> search(String username, String searchTerms)
            throws NoFreddieInstanceConfigured, SearchException;

}