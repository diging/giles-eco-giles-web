package edu.asu.giles.users;

import edu.asu.giles.exceptions.UnstorableObjectException;



public interface IUserManager {

    /**
     * Find a user by its user id.
     * 
     * @param name
     *            user id
     * @return user or null
     */
    public abstract User findUser(String name);

    public abstract User getUser(String name, String pw);

    public abstract User[] getAllUsers();

    public abstract User addUser(User user) throws UnstorableObjectException;

    public abstract void deleteUser(String username);

    public abstract void storeModifiedUser(User user);

    public abstract void storeModifiedPassword(User user);

    public abstract User findUserByEmail(String email);

    public abstract void updatePasswordEncryption(String username);

    public abstract void approveUserAccount(String username);

    public abstract void revokeUserAccount(String username);

    public void addRoleToUser(String username, GilesRole role);

    public abstract void removeRoleFromUser(String username, GilesRole role);

    public abstract User findUserByProviderUserId(String userId, String provider);

    public abstract String getUniqueUsername(String providerId);

}