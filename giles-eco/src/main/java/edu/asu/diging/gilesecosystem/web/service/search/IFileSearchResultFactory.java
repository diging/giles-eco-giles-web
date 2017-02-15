package edu.asu.diging.gilesecosystem.web.service.search;


public interface IFileSearchResultFactory {

    public abstract FileSearchResult createSearchResult(String fileId);

}