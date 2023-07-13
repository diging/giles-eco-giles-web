package edu.asu.diging.gilesecosystem.web.core.users;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;

@Component
public class UserDatabaseClient {

    @Autowired
    private UserRepository userRepository;

    public User getUser(String name, String pw) {
        return userRepository.findByUsernameAndPassword(name, pw);
    }

    public User findUser(String name) {
        return userRepository.findByUsername(name);
    }

    public User findUserByProviderUserId(String userId, String provider) {
        return userRepository.findByUserIdOfProviderAndProvider(userId, provider);
    }

    public List<User> findUsersByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User[] getAllUser() {
        List<User> userList = userRepository.findAll();
        return userList.toArray(new User[userList.size()]);
    }

    public User addUser(User user) throws UnstorableObjectException {
        if (user.getUsername() == null || user.getUsername().isEmpty() || user.getUserIdOfProvider() == null
                || user.getUserIdOfProvider().isEmpty()) {
            throw new UnstorableObjectException("User has no username or provider username.");
        }
        return userRepository.save(user);
    }

    public void deleteUser(String name) {
        User toBeDeleted = findUser(name);
        userRepository.delete(toBeDeleted);
    }

    public void update(User user) {
        User storedUser = findUserByProviderUserId(user.getUserIdOfProvider(), user.getProvider());

        storedUser.setAccountStatus(user.getAccountStatus());
        storedUser.setIsAdmin(user.getIsAdmin());
        storedUser.setEmail(user.getEmail());
        storedUser.setFirstname(user.getFirstname());
        storedUser.setLastname(user.getLastname());
        storedUser.setPassword(user.getPassword());
        storedUser.setProvider(user.getProvider());
        storedUser.setRoles(user.getRoles());
        storedUser.setUserIdOfProvider(user.getUserIdOfProvider());
        storedUser.setUsername(user.getUsername());
        userRepository.save(storedUser);
    }

    /**
     * Find users based on role.
     * @param role role of the user
     * @return list of users with the input role
     */
    protected List<User> getUsersByRole(String role) {
        return userRepository.findByRoles(role);
    }

    /**
     * This methods generates a new 6 character long id. Note that this method
     * does not assure that the id isn't in use yet.
     * 
     * Adapted from
     * http://stackoverflow.com/questions/9543715/generating-human-readable
     * -usable-short-but-unique-ids
     * 
     * @return 12 character id
     */
    protected String generateUniqueId() {
        char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
                .toCharArray();

        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            builder.append(chars[random.nextInt(62)]);
        }

        return builder.toString();
    }
}
