package edu.asu.diging.gilesecosystem.web.core.users;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String>{
    User findByUsernameAndPassword(String username, String password);
    
    User findByUsername(String username);
    
    User findByUserIdOfProviderAndProvider(String userId, String provider);
    
    List<User> findByEmail(String email);
    
    List<User> findByRoles(String role);
}
