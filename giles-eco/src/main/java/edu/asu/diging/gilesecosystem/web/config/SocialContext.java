package edu.asu.diging.gilesecosystem.web.config;

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
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.security.AuthenticationNameUserIdSource;

import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.aspects.access.tokens.impl.GitHubChecker;
import edu.asu.diging.gilesecosystem.web.aspects.access.tokens.impl.GoogleChecker;
import edu.asu.diging.gilesecosystem.web.aspects.access.tokens.impl.MitreidAccessTokenChecker;
import edu.asu.diging.gilesecosystem.web.aspects.access.tokens.impl.MitreidChecker;
import edu.asu.diging.gilesecosystem.web.config.social.AdjustableGithubConnectionFactory;
import edu.asu.diging.gilesecosystem.web.config.social.AdjustableGoogleConnectionFactory;
import edu.asu.diging.gilesecosystem.web.config.social.AdjustableMitreidConnectionFactory;
import edu.asu.diging.gilesecosystem.web.service.IIdentityProviderRegistry;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.users.IUserManager;

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
    
    @Autowired
    private IReloadService reloadService;

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer cfConfig,
            Environment env) {
        String googleClientId = propertyManager.getProperty(Properties.GOOGLE_CLIENT_ID);
        String googleSecret = propertyManager.getProperty(Properties.GOOGLE_SECRET);
        GoogleConnectionFactory tmpGooglefactory = new GoogleConnectionFactory(
                googleClientId, googleSecret);
        
        AdjustableGoogleConnectionFactory googleFactory = new AdjustableGoogleConnectionFactory(googleClientId, googleSecret);
       
//        factory.setScope("email");
        googleFactory.setScope("email");
        cfConfig.addConnectionFactory(tmpGooglefactory);
        reloadService.addFactory(IReloadService.GOOGLE, googleFactory);
        identityProviderRegistry.addProvider(googleFactory.getProviderId(), null);
        identityProviderRegistry.addProviderTokenChecker(googleFactory.getProviderId(), null, GoogleChecker.ID);
        
        String githubClientId = propertyManager.getProperty(Properties.GITHUB_CLIENT_ID);
        String githubSecret = propertyManager.getProperty(Properties.GITHUB_SECRET);
//        GitHubConnectionFactory githubFactory = new GitHubConnectionFactory(
//              githubClientId, githubSecret);
        AdjustableGithubConnectionFactory githubFactory = new AdjustableGithubConnectionFactory(githubClientId, githubSecret);
        
        cfConfig.addConnectionFactory(githubFactory);
        reloadService.addFactory(IReloadService.GITHUB, githubFactory);
        identityProviderRegistry.addProvider(githubFactory.getProviderId(), null);
        identityProviderRegistry.addProviderTokenChecker(githubFactory.getProviderId(), null, GitHubChecker.ID);
        
        String mitreidClientId = propertyManager.getProperty(Properties.MITREID_CLIENT_ID);
        String mitreidSecret = propertyManager.getProperty(Properties.MITREID_SECRET);
        String mitreidServer = propertyManager.getProperty(Properties.MITREID_SERVER_URL);
        AdjustableMitreidConnectionFactory mitreidFactory = new AdjustableMitreidConnectionFactory(mitreidClientId, mitreidSecret, mitreidServer);
//        MitreidConnectConnectionFactory mitreidFactory = new MitreidConnectConnectionFactory(mitreidClientId, mitreidSecret, mitreidServer);
        cfConfig.addConnectionFactory(mitreidFactory);
        reloadService.addFactory(IReloadService.MITREID, mitreidFactory);
        identityProviderRegistry.addProvider(mitreidFactory.getProviderId(), null);
        identityProviderRegistry.addProviderTokenChecker(mitreidFactory.getProviderId(), null,  MitreidChecker.ID);
        
        //new MITREid connect server provider for access token
        identityProviderRegistry.addProvider(mitreidFactory.getProviderId(), propertyManager.getProperty(Properties.AUTHORIZATION_TYPE_ACCESS_TOKEN));
        identityProviderRegistry.addProviderTokenChecker(mitreidFactory.getProviderId(), propertyManager.getProperty(Properties.AUTHORIZATION_TYPE_ACCESS_TOKEN), MitreidAccessTokenChecker.ID);
        
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
