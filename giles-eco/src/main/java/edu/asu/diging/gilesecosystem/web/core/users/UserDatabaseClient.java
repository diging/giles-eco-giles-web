package edu.asu.diging.gilesecosystem.web.core.users;

import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;

@Transactional
@Component
public class UserDatabaseClient {

    @PersistenceContext(unitName="entityManagerFactory")
    private EntityManager em;


    public User getUser(String name, String pw) {
        User user = new User();
        user.setUsername(name);
        user.setPassword(pw);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username=:username AND u.password = :password", User.class);
        query.setParameter("username", name);
        query.setParameter("password", pw);
        List<User> users = query.getResultList();
        
        // there should only be exactly one object with this id
        if (users.size() == 1) {
            return users.get(0);
        }

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
        
        List<User> results = searchByProperty("username", name);

        // there should only be exactly one object with this id
        if (results.size() >= 1) {
            return results.get(0);
        }

        return null;
    }
    
    public User findUserByProviderUserId(String userId, String provider) {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.userIdOfProvider=:userId AND u.provider=:provider", User.class);
        query.setParameter("provider", provider);
        query.setParameter("userId", userId);
        List<User> results = query.getResultList();
        
        if (results.size() >= 1) {
            return results.get(0);
        }

        return null;
    }

    public List<User> findUsersByEmail(String email) {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email=:email", User.class);
        query.setParameter("email", email);
        return query.getResultList();
    }

    public User[] getAllUser() {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
        List<User> results = query.getResultList();
        return results.toArray(new User[results.size()]);
    }

    public User addUser(User user) throws UnstorableObjectException {
        if (user.getUsername() == null || user.getUsername().isEmpty() || user.getUserIdOfProvider() == null || user.getUserIdOfProvider().isEmpty()) {
            throw new UnstorableObjectException("User has not username or provider user name.");
        }
        em.persist(user);
        em.flush();
        return user;
    }

    public void deleteUser(String name) {
        User toBeDeleted = findUser(name);
        em.remove(toBeDeleted);
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
        
    }
    
    protected List<User> searchByProperty(String propName, String propValue) {
        TypedQuery<User> docs = em.createQuery("SELECT t FROM User t WHERE t." + propName + " = '" + propValue + "'", User.class);
        return docs.getResultList();
    }
    
    /**
     * Find users based on role.
     *
     * @param role role of the user
     * @return list of users with input role
     */
    protected List<User> getUsersByRole(String role) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
        Root<User> user = criteriaQuery.from(User.class);
        Predicate hasUserRole = builder.and(user.join("roles").in(role));
        criteriaQuery.where(hasUserRole);
        TypedQuery<User> userList = em.createQuery(criteriaQuery);
        return userList.getResultList();
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