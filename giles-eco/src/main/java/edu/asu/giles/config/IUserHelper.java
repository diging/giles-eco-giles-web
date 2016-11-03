package edu.asu.giles.config;

import org.springframework.social.connect.Connection;

import edu.asu.giles.users.User;

public interface IUserHelper {

    public abstract User createUser(Connection<?> connection);

}