package edu.asu.diging.gilesecosystem.web.config.impl;

import java.util.List;

public class TokenInfo {
    
    private boolean active;
    private long exp;
    private String user_name;
    private List<String> authorities;
    private String client_id;
    private List<String> scope;
    
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public long getExp() {
        return exp;
    }
    public void setExp(long exp) {
        this.exp = exp;
    }
    public String getUser_name() {
        return user_name;
    }
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
    public List<String> getAuthorities() {
        return authorities;
    }
    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
    public String getClient_id() {
        return client_id;
    }
    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
    public List<String> getScope() {
        return scope;
    }
    public void setScope(List<String> scope) {
        this.scope = scope;
    }
}
