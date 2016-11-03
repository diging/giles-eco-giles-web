package edu.asu.giles.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;

import edu.asu.giles.service.properties.IPropertiesManager;
import edu.asu.giles.users.LocalUserDetailsService;
import edu.asu.giles.users.SimpleSocialUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityContext extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private IPropertiesManager propertiesManager;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
        // Spring Security ignores request to static resources such as CSS or JS
        // files.
        .ignoring().antMatchers("/static/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        HeadersConfigurer<HttpSecurity> config = http.antMatcher("**").csrf().requireCsrfProtectionMatcher(new RequestMatcher() {
            
            @Override
            public boolean matches(HttpServletRequest arg0) {
                // don't require CSRF for REST calls
                if (arg0.getRequestURI().indexOf("/rest/") > -1) {
                    return false;
                }
                // mitreid connect server can't deal with additional parameteres
                if (arg0.getRequestURI().indexOf("/signin/mitreid") > -1) {
                    return false;
                }
                if (arg0.getMethod() == "GET") {
                    return false;
                }
                return true;
            }
        }).and().headers().frameOptions().sameOrigin();
        
        String iframeing = propertiesManager.getProperty(IPropertiesManager.ALLOW_IFRAMING_FROM);
        if (iframeing == null) {
            config.addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsMode.DENY));
        } else {
            String[] origins = iframeing.split(",");
            StringBuffer sb = new StringBuffer();
            sb.append("frame-ancestors ");
            
            if (origins.length == 0 || (origins.length == 1 && origins[0].isEmpty())) {
                sb.append("'none'");
            } else {
                for (String org : origins) {
                    sb.append(org);
                    sb.append(" ");
                }
            }
            config.addHeaderWriter(new StaticHeadersWriter("Content-Security-Policy", sb.toString()));
        }
        
        // Configures form login
        config.and().formLogin()
                .loginPage("/")
                .loginProcessingUrl("/login/authenticate")
                .failureUrl("/?error=bad_credentials")
                // Configures the logout function
                .and()
                .logout()
                .deleteCookies("JSESSIONID")
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .and().exceptionHandling().accessDeniedPage("/403")
                // Configures url based authorization
                .and()
                .authorizeRequests()
                // Anyone can access the urls
                .antMatchers("/auth/**", "/signin/**", "/", "/connect/**",
                        "/signup/**", "/user/register/**", "/resources/**",
                        "/rest/**").permitAll()
                // The rest of the our application is protected.
                .antMatchers("/users/**", "/admin/**").hasRole("ADMIN")
                .anyRequest().hasRole("USER")
                // Adds the SocialAuthenticationFilter to Spring Security's
                // filter chain.
                .and().apply(new SpringSocialConfigurer());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(
                passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SocialUserDetailsService socialUserDetailsService() {
        return new SimpleSocialUserDetailsService(userDetailsService());
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new LocalUserDetailsService();
    }
}