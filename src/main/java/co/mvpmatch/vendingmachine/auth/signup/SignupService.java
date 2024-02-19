package co.mvpmatch.vendingmachine.auth.signup;

import co.mvpmatch.vendingmachine.auth.model.Role;
import co.mvpmatch.vendingmachine.auth.signup.validation.UsernameTakenException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

public interface SignupService {
    void signup(String username, String password, Role role);
}


@Service
class SignupServiceImpl implements SignupService {

    private final UserDetailsManager userDetailsManager;

    public SignupServiceImpl(UserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }

    @Override
    public void signup(String username, String password, Role role) {

        if (userDetailsManager.userExists(username)) {
            throw new UsernameTakenException(username);
        }

        UserDetails user = org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password(password)
                .roles(role.name())
                .build();

        userDetailsManager.createUser(user);
    }
}
