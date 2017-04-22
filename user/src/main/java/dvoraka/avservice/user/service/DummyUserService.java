package dvoraka.avservice.user.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Dummy user service.
 */
public class DummyUserService implements UserService {

    @Override
    public UserDetails loadUserByUsername(String username) {
        throw new UsernameNotFoundException("User not found: " + username);
    }
}
