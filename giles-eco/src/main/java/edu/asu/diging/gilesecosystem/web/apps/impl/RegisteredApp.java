package edu.asu.diging.gilesecosystem.web.apps.impl;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;

import edu.asu.diging.gilesecosystem.web.apps.IRegisteredApp;

@Entity
public class RegisteredApp implements IRegisteredApp {

    @Id private String id;
    private String name;
    private String providerId;
    @Basic(fetch = FetchType.EAGER) private List<String> tokenIds;
    private String providerClientId;
    
    /* (non-Javadoc)
     * @see edu.asu.giles.apps.impl.IRegisteredApp#getId()
     */
    @Override
    public String getId() {
        return id;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.apps.impl.IRegisteredApp#setId(java.lang.String)
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.apps.impl.IRegisteredApp#getName()
     */
    @Override
    public String getName() {
        return name;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.apps.impl.IRegisteredApp#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String getProviderId() {
        return providerId;
    }
    @Override
    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.apps.impl.IRegisteredApp#getTokenIds()
     */
    @Override
    public List<String> getTokenIds() {
        return tokenIds;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.apps.impl.IRegisteredApp#setTokenIds(java.util.List)
     */
    @Override
    public void setTokenIds(List<String> tokenIds) {
        this.tokenIds = tokenIds;
    }
    @Override
    public String getProviderClientId() {
        return providerClientId;
    }
    @Override
    public void setProviderClientId(String providerClientId) {
        this.providerClientId = providerClientId;
    }
       
    
}
