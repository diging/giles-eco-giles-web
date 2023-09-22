package edu.asu.diging.gilesecosystem.web.config;

import java.io.IOException;
import java.net.URI;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesView;

import com.nimbusds.jose.util.Base64;

import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.septemberutil.service.impl.SystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.files.impl.FileStorageManager;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;

/**
 * Class to initialize rest template for MITREid connect server
 * with protected resource access keys and to initialize
 * message handler class to send exception as kafka topic.
 * 
 * @author snilapwa
 *
 */
@Configuration
@EnableAspectJAutoProxy
@EnableAsync
@PropertySource("classpath:/config.properties")
@ComponentScan({ "edu.asu.diging.gilesecosystem.web", "edu.asu.diging.gilesecosystem.requests", "edu.asu.diging.gilesecosystem.util.store", "edu.asu.diging.gilesecosystem.util.properties", "edu.asu.diging.gilesecosystem.util.files", "edu.asu.diging.gilesecosystem.kafka.util", "edu.asu.diging.gilesecosystem.web.core.service" })
public class GilesConfig implements WebMvcConfigurer {
    
    @Value("${giles_files_tmp_dir}")
    private String gilesFilesTmpDir;
    
    @Value("${file.upload.max.size}")
    private long maxFileSize;


    @Autowired
    private IPropertiesManager propertyManager;

    @Bean(name = "accessTokenRestTemplate")
    public RestTemplate getAccessTokenRestTemplate() {
        // use mitreid connect client with protected resource access keys
        final String clientId = propertyManager.getProperty(Properties.MITREID_INTROSPECT_CLIENT_ID);
        final String clientSecret = propertyManager.getProperty(Properties.MITREID_INTROSPECT_SECRET);

        HttpClient httpClient = HttpClientBuilder.create().useSystemProperties().build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(factory) {
            @Override
            protected ClientHttpRequest createRequest(URI url, HttpMethod method) throws IOException {
                ClientHttpRequest httpRequest = super.createRequest(url, method);
                httpRequest.getHeaders().add("Authorization",
                        String.format("Basic %s", Base64.encode(String.format("%s:%s", clientId, clientSecret))));
                return httpRequest;
            }
        };

    }
    
    @Bean
    public ISystemMessageHandler getMessageHandler() {
        return new SystemMessageHandler(propertyManager.getProperty(Properties.APPLICATION_ID));
    }
    
    @Bean(name="tmpStorageManager")
    public FileStorageManager tmpStorageManager() {
        FileStorageManager manager = new FileStorageManager();
        manager.setBaseDirectory(gilesFilesTmpDir);
        return manager;
    }
    
    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasename("classpath:locale/messages");
        source.setFallbackToSystemLocale(false);
        return source;
    }
    
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }

    @Bean(name="tilesViewResolver")
    public UrlBasedViewResolver tilesViewResolver() {
        UrlBasedViewResolver resolver = new UrlBasedViewResolver();
        resolver.setViewClass(TilesView.class);
        return resolver;
    }
    
    @Bean(name="tilesConfigurer")
    public TilesConfigurer tilesConfigurer() {
        TilesConfigurer configurer = new TilesConfigurer();
        configurer.setDefinitions("/WEB-INF/tiles-defs.xml");
        return configurer;
    }
    
    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(new Long(maxFileSize));
        return multipartResolver;
    }
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
