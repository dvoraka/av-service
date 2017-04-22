package dvoraka.avservice.user.service;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * LDAP user service.
 */
public class LdapUserService implements UserService {

    @Override
    public UserDetails loadUserByUsername(String username) {
        return null;
    }
}
