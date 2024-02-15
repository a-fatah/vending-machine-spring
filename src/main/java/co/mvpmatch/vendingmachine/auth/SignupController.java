package co.mvpmatch.vendingmachine.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class SignupController {

    private final SignupService signupService;

    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@Valid @RequestBody UserDto userDto) {
        signupService.signup(userDto.getUsername(), userDto.getPassword(), Role.valueOf(userDto.getRole()));
    }

    @ExceptionHandler(UsernameTakenException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleUserAlreadyExistsException(UsernameTakenException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Username is already taken");
        problemDetail.setTitle("Username is already taken");
        problemDetail.setType(URI.create("https://mvpmatch.co/docs/errors/username-already-taken"));
        problemDetail.setDetail(e.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problemDetail.setTitle("Validation failed");
        problemDetail.setType(URI.create("https://mvpmatch.co/docs/errors/validation-failed"));

        Map<String, Object> validationErrors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        problemDetail.setDetail("One or more fields failed validation");
        problemDetail.setProperties(validationErrors);
        return problemDetail;
    }
}

interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}

interface SignupService {
    void signup(String username, String password, Role role);
}

@Service
class SignupServiceImpl implements SignupService {

    private final UserRepository userRepository;

    SignupServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void signup(String username, String password, Role role) {

        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            throw new UsernameTakenException(username);
        }

        userRepository.save(new User(username, password, role));
    }
}

class UsernameTakenException extends RuntimeException {
    UsernameTakenException(String username) {
        super("Username " + username + " is already taken");
    }
}

@Data
class UserDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    @AllowedValues(values = {"BUYER", "SELLER"}, message = "Role must be either BUYER or SELLER")
    private String role;
}
