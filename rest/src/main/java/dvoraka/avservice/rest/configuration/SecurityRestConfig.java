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
        auth.inMemoryAuthentication().withUser(User
                .withUsername("test")
                .password("{bcrypt}$2a$10$a4ppmQyKfWb9.NbB8In42eYTQINkqct.AOAOvEeG4Rj051oVKmA5q")
                .roles("USER")
                .build());
        auth.inMemoryAuthentication().withUser(User
                .withUsername("admin")
                .password("{bcrypt}$2a$10$ildtDVnwCObMw9KE2PVYtecuvPUm131/OD1L.BQr1jIal8zlM/6vC")
                .roles("USER", "ADMIN")
                .build());

        auth.userDetailsService(userDetailsService());
    }
}
