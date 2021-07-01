package edu.asu.diging.gilesecosystem.web.web.admin.pages;

public class AdminUser {

    private String username;
    private String oldPassword;
    private String newPassword;
    private String retypedPassword;
    
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getOldPassword() {
        return oldPassword;
    }
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    public String getRetypedPassword() {
        return retypedPassword;
    }
    public void setRetypedPassword(String retypedPassword) {
        this.retypedPassword = retypedPassword;
    }
}
