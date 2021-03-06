package edu.asu.diging.gilesecosystem.web.core.users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;

@Entity
public class User implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1019105087386557957L;

    @Id
    private String username;
    private String firstname;
    private String lastname;
    private String name;
    private boolean isAdmin;
    private String email;
    private String provider;
    private String userIdOfProvider;
    @ElementCollection(fetch=FetchType.EAGER) private List<String> roles;
    private String password;
    private AccountStatus accountStatus;
    
    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getUserIdOfProvider() {
        return userIdOfProvider;
    }

    public void setUserIdOfProvider(String userIdOfProvider) {
        this.userIdOfProvider = userIdOfProvider;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public List<String> getRoles() {
        if (roles == null) {
            roles = new ArrayList<String>();
        }
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getFullname() {
        if (name != null && !name.isEmpty()) {
            return name;
        }
        return firstname + " " + lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }    
}
