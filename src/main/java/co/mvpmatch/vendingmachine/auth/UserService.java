package co.mvpmatch.vendingmachine.auth;

import co.mvpmatch.vendingmachine.auth.db.UserEntity;
import co.mvpmatch.vendingmachine.auth.db.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserDetailsManager userDetailsManager;

    public UserService(UserRepository userRepository, UserDetailsManager userDetailsManager) {
        this.userRepository = userRepository;
        this.userDetailsManager = userDetailsManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity entity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return userDetailsManager.loadUserByUsername(username);
    }
}

