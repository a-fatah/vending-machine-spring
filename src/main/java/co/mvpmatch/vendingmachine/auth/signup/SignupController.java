package co.mvpmatch.vendingmachine.auth.signup;

import co.mvpmatch.vendingmachine.auth.model.Role;
import co.mvpmatch.vendingmachine.auth.signup.validation.AllowedValues;
import co.mvpmatch.vendingmachine.auth.signup.validation.UsernameTakenException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

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