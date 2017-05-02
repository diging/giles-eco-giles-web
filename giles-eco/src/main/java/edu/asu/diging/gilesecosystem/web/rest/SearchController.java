package edu.asu.diging.gilesecosystem.web.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.gilesecosystem.web.aspects.access.annotations.TokenCheck;
import edu.asu.diging.gilesecosystem.web.exceptions.NoFreddieInstanceConfigured;
import edu.asu.diging.gilesecosystem.web.exceptions.SearchException;
import edu.asu.diging.gilesecosystem.web.service.processing.helpers.RequestHelper;
import edu.asu.diging.gilesecosystem.web.service.search.FileSearchResult;
import edu.asu.diging.gilesecosystem.web.service.search.ISearchService;
import edu.asu.diging.gilesecosystem.web.users.User;

@Controller
public class SearchController {
    
    @Autowired
    private ISearchService searchService;
    
    @Autowired
    protected RequestHelper requestHelper;

    @TokenCheck
    @RequestMapping(value = "/rest/search")
    public ResponseEntity<List<FileSearchResult>> searchForUser(@RequestParam("q") String query,
            @RequestParam(defaultValue = "") String accessToken,
            HttpServletRequest request, HttpServletResponse response, User user) throws NoFreddieInstanceConfigured, SearchException {

        List<FileSearchResult> results = searchService.searchWithUsername(requestHelper.getUsernameForStorage(user), query);
        return new ResponseEntity<List<FileSearchResult>>(results, HttpStatus.OK);
    }
}
