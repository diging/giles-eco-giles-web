package edu.asu.diging.gilesecosystem.web.config;

import org.springframework.http.ResponseEntity;
import org.springframework.social.connect.Connection;

import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.users.User;

public interface IUserHelper {

    public abstract User createUser(Connection<?> connection);

    String createUsername(String username, String providerId);
    
    /**
        Checks whether the user associated with the provided Citesphere token has permission to access the given document.
        @param document The document for which to check the user permission.
        @param citesphereToken The Citesphere token for the user.
        @return {@code true} if the user has permission to access the document, {@code false} otherwise.
    */
    public abstract boolean checkUserPermission(IDocument document, CitesphereToken citesphereToken);
}
