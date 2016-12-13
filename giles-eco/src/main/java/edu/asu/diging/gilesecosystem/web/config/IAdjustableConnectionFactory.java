package edu.asu.diging.gilesecosystem.web.config;

import org.springframework.social.connect.support.OAuth2ConnectionFactory;

public interface IAdjustableConnectionFactory<A> {

    public abstract OAuth2ConnectionFactory<?> getDelegate();

    public abstract void update(String clientId, String clientSecret);

    public abstract void setProviderUrl(String providerUrl);

}