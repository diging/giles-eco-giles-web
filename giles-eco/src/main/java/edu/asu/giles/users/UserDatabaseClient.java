package edu.asu.giles.users;

import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

import edu.asu.giles.db4o.impl.DatabaseManager;
import edu.asu.giles.exceptions.UnstorableObjectException;

@Component
public class UserDatabaseClient {

    private ObjectContainer client;

    @Autowired
    @Qualifier("userDatabaseManager")
    private DatabaseManager userDatabase;

    @PostConstruct
    public void init() {
        client = userDatabase.getClient();
    }

    public User getUser(String name, String pw) {
        User user = new User();
        user.setUsername(name);
        user.setPassword(pw);

        ObjectSet<User> results = client.queryByExample(user);

        // there should only be exactly one object with this id
        if (results.size() == 1)
            return results.get(0);

        return null;
    }

    /**
     * Finds a user by its user id.
     * 
     * @param name
     *            user id.
     * @return found user or null
     */
    public User findUser(String name) {
        ObjectSet<User> results = client.query(new Predicate<User>() {

            @Override
            public boolean match(User arg0) {
                if (arg0.getUsername().equals(name)) {
                    return true;
                }
                return false;
            };
        });

        // there should only be exactly one object with this id
        if (results.size() >= 1) {
            return results.get(0);
        }

        return null;
    }
    
    public User findUserByProviderUserId(String userId, String provider) {
        User user = new User();
        user.setUserIdOfProvider(userId);
        user.setProvider(provider);
        ObjectSet<User> results = client.queryByExample(user);
        
        if (results.size() >= 1) {
            return results.get(0);
        }

        return null;
    }

    public List<User> findUsers(User exampleUser) {
        ObjectSet<User> results = client.queryByExample(exampleUser);
        return results;
    }

    public User[] getAllUser() {
        ObjectSet<User> results = client.query(User.class);
        return results.toArray(new User[results.size()]);
    }

    public User addUser(User user) throws UnstorableObjectException {
        if (user.getUsername() == null || user.getUsername().isEmpty() || user.getUserIdOfProvider() == null || user.getUserIdOfProvider().isEmpty()) {
            throw new UnstorableObjectException("User has not username or provider user name.");
        }
        client.store(user);
        client.commit();
        return user;
    }

    public void deleteUser(String name) {
        User user = new User();
        user.setUsername(name);

        ObjectSet<User> results = client.queryByExample(user);
        for (User res : results) {
            client.delete(res);
            client.commit();
        }
    }

    public void update(User user) {
        User storedUser = findUserByProviderUserId(user.getUserIdOfProvider(), user.getProvider());
        
        storedUser.setAccountStatus(user.getAccountStatus());
        storedUser.setAdmin(user.getIsAdmin());
        storedUser.setEmail(user.getEmail());
        storedUser.setFirstname(user.getFirstname());
        storedUser.setLastname(user.getLastname());
        storedUser.setPassword(user.getPassword());
        storedUser.setProvider(user.getProvider());
        storedUser.setRoles(user.getRoles());
        storedUser.setUserIdOfProvider(user.getUserIdOfProvider());
        storedUser.setUsername(user.getUsername());
        
        client.store(storedUser);
        client.commit();
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