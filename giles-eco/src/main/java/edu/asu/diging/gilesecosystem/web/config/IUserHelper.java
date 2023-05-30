package edu.asu.diging.gilesecosystem.web.config;

import org.springframework.http.ResponseEntity;
import org.springframework.social.connect.Connection;

import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.users.User;

public interface IUserHelper {

    public abstract User createUser(Connection<?> connection);

    String createUsername(String username, String providerId);

    public abstract boolean checkUserPermission(IDocument document, CitesphereToken citesphereToken);

    public abstract ResponseEntity<String> generateUnauthorizedUserResponse();

}
