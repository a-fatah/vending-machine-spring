package co.mvpmatch.vendingmachine.auth.login;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    public LoginResponse login(@Valid @RequestBody LoginDto loginDto) {
        String token = loginService.login(loginDto.getUsername(), loginDto.getPassword());
        return new LoginResponse(token);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ProblemDetail handleUsernameNotFoundException(UsernameNotFoundException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid username or password");
        problemDetail.setTitle("Invalid username or password");
        problemDetail.setType(URI.create("https://mvpmatch.co/docs/errors/invalid-username-or-password"));
        problemDetail.setDetail(e.getMessage());
        return problemDetail;
    }

}

@Data
class LoginDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}

@Data
@AllArgsConstructor
class LoginResponse {
    private String token;
}