package co.mvpmatch.vendingmachine.auth;

import co.mvpmatch.vendingmachine.auth.db.User;
import co.mvpmatch.vendingmachine.auth.db.UserRepository;
import co.mvpmatch.vendingmachine.auth.model.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomUserDetailsManager implements UserDetailsManager {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public void createUser(UserDetails user) {
        log.debug("Creating user: {}", user);

        List<String> authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // remove ROLE_ prefix
        authorities = authorities.stream().map(s -> s.replace("ROLE_", ""))
                .collect(Collectors.toList());

        // ensure that authorities are not other than ROLE_SELLER and ROLE_BUYER
        var allowedRoles = Arrays.stream(Role.values()).map(Enum::name).collect(Collectors.toList());

        if (!allowedRoles.containsAll(authorities)) {
            throw new IllegalArgumentException("Invalid role");
        }

        User entity = new User();
        entity.setUsername(user.getUsername());
        entity.setPassword(passwordEncoder.encode(user.getPassword()));
        entity.setRole(Role.valueOf(authorities.get(0)));

        userRepository.save(entity);
    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return false;
    }
}
