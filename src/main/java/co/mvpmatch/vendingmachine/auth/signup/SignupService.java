package co.mvpmatch.vendingmachine.auth.signup;

import co.mvpmatch.vendingmachine.auth.Role;
import co.mvpmatch.vendingmachine.auth.signup.validation.UsernameTakenException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

public interface SignupService {
    void signup(String username, String password, Role role);
}


@Service
class SignupServiceImpl implements SignupService {

    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    public SignupServiceImpl(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void signup(String username, String password, Role role) {

        if (userDetailsManager.userExists(username)) {
            throw new UsernameTakenException(username);
        }

        UserDetails user = org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(role.name())
                .build();

        userDetailsManager.createUser(user);
    }
}
