package edu.asu.diging.gilesecosystem.web.service.search.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.web.exceptions.NoFreddieInstanceConfigured;
import edu.asu.diging.gilesecosystem.web.exceptions.SearchException;
import edu.asu.diging.gilesecosystem.web.service.search.FileSearchResult;
import edu.asu.diging.gilesecosystem.web.service.search.IFileSearchResultFactory;
import edu.asu.diging.gilesecosystem.web.service.search.ISearchConnector;
import edu.asu.diging.gilesecosystem.web.service.search.ISearchService;

@Service
public class SearchService implements ISearchService {

    @Autowired
    private ISearchConnector searchConnector;
    
    @Autowired
    private IFileSearchResultFactory fileResultFactory;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.search.impl.ISearchService#searchWithUsername(java.lang.String, java.lang.String)
     */
    @Override
    public List<FileSearchResult> searchWithUsername(String username, String query) throws NoFreddieInstanceConfigured, SearchException {
        List<SearchResult> results = searchConnector.search(username, query);
        
        List<FileSearchResult> fileResults = new ArrayList<FileSearchResult>();
        for (SearchResult result : results) {
            FileSearchResult searchResult = fileResultFactory.createSearchResult(result.getFileId());
            if (searchResult != null) {
                fileResults.add(searchResult);
            }
        }
        
        return fileResults;
    }
}
