package edu.asu.giles.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.github.connect.GitHubConnectionFactory;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.mitreidconnect.connect.MitreidConnectConnectionFactory;
import org.springframework.social.security.AuthenticationNameUserIdSource;

import edu.asu.giles.aspects.access.tokens.impl.GitHubChecker;
import edu.asu.giles.aspects.access.tokens.impl.GoogleChecker;
import edu.asu.giles.aspects.access.tokens.impl.MitreidChecker;
import edu.asu.giles.service.IIdentityProviderRegistry;
import edu.asu.giles.service.properties.IPropertiesManager;
import edu.asu.giles.users.IUserManager;

@Configuration
@EnableSocial
@PropertySource("classpath:/config.properties")
public class SocialContext implements SocialConfigurer {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private IUserHelper userHelper;
    
    @Autowired
    private IPropertiesManager propertyManager;
    
    @Autowired
    private IIdentityProviderRegistry identityProviderRegistry;

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer cfConfig,
            Environment env) {
        String googleClientId = propertyManager.getProperty(IPropertiesManager.GOOGLE_CLIENT_ID);
        String googleSecret = propertyManager.getProperty(IPropertiesManager.GOOGLE_SECRET);
        GoogleConnectionFactory factory = new GoogleConnectionFactory(
                googleClientId, googleSecret);
        factory.setScope("email");
        cfConfig.addConnectionFactory(factory);
        identityProviderRegistry.addProvider(factory.getProviderId());
        identityProviderRegistry.addProviderTokenChecker(factory.getProviderId(), GoogleChecker.ID);
        
        String githubClientId = propertyManager.getProperty(IPropertiesManager.GITHUB_CLIENT_ID);
        String githubSecret = propertyManager.getProperty(IPropertiesManager.GITHUB_SECRET);
        GitHubConnectionFactory githubFactory = new GitHubConnectionFactory(
                githubClientId, githubSecret);
        cfConfig.addConnectionFactory(githubFactory);
        identityProviderRegistry.addProvider(githubFactory.getProviderId());
        identityProviderRegistry.addProviderTokenChecker(githubFactory.getProviderId(), GitHubChecker.ID);
        
        String mitreidClientId = propertyManager.getProperty(IPropertiesManager.MITREID_CLIENT_ID);
        String mitreidSecret = propertyManager.getProperty(IPropertiesManager.MITREID_SECRET);
        String mitreidServer = propertyManager.getProperty(IPropertiesManager.MITREID_SERVER_URL);
        MitreidConnectConnectionFactory mitreidFactory = new MitreidConnectConnectionFactory(mitreidClientId, mitreidSecret, mitreidServer);
        cfConfig.addConnectionFactory(mitreidFactory);
        identityProviderRegistry.addProvider(mitreidFactory.getProviderId());
        identityProviderRegistry.addProviderTokenChecker(mitreidFactory.getProviderId(), MitreidChecker.ID);
    }

    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(
            ConnectionFactoryLocator connectionFactoryLocator) {
        JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(
                dataSource, connectionFactoryLocator, Encryptors.noOpText());
        repository.setConnectionSignUp(new GilesConnectionSignUp(userManager, userHelper));
        return repository;
    }

    @Bean
    public ProviderSignInController providerSignInController(
            ConnectionFactoryLocator connectionFactoryLocator,
            UsersConnectionRepository usersConnectionRepository) {
        ProviderSignInController controller = new ProviderSignInController(
                connectionFactoryLocator, usersConnectionRepository,
                new SimpleSignInAdapter(userManager, userHelper));
        return controller;
    }
}
