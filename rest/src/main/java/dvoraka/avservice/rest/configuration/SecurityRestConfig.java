package dvoraka.avservice.rest.configuration;

import dvoraka.avservice.user.service.DummyUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Rest security configuration.
 */

@Configuration
@EnableWebSecurity
public class SecurityRestConfig extends WebSecurityConfigurerAdapter {

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return new DummyUserService();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/").access("hasRole('USER')")
                .antMatchers("/file/**").access("hasRole('USER')")
                .and()
                .csrf().disable()
                .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // user for testing for now
        auth.inMemoryAuthentication().withUser(User.withDefaultPasswordEncoder()
                .username("test").password("test").roles("USER"));
        auth.inMemoryAuthentication().withUser(User.withDefaultPasswordEncoder()
                .username("admin").password("admin").roles("USER", "ADMIN"));

        auth.userDetailsService(userDetailsService());
    }
}
