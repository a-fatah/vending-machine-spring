package co.mvpmatch.vendingmachine.auth;


import co.mvpmatch.vendingmachine.auth.model.Role;
import co.mvpmatch.vendingmachine.auth.signup.SignupController;
import co.mvpmatch.vendingmachine.auth.signup.SignupService;
import co.mvpmatch.vendingmachine.auth.signup.validation.UsernameTakenException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import javax.sql.DataSource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(SignupController.class)
@Import({SecurityConfig.class})
class SignupTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SignupService signupService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void shouldAllowSignup() throws Exception {
        String userJson = "{" +
                "\"username\": \"admin\"," +
                "\"password\": \"admin\"," +
                "\"role\": \"BUYER\"" +
                "}";

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));

        verify(signupService).signup(anyString(), anyString(), any(Role.class));
    }

    @Test
    void shouldSendBadRequestWhenUsernameIsTaken() throws Exception {
        String userJson = "{" +
                "\"username\": \"admin\"," +
                "\"password\": \"admin\"," +
                "\"role\": \"BUYER\"" +
                "}";

        Mockito.doThrow(new UsernameTakenException("admin"))
                .when(signupService).signup(anyString(), anyString(), any(Role.class));

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("https://mvpmatch.co/docs/errors/username-already-taken"))
                .andExpect(jsonPath("$.title").value("Username is already taken"))
                .andExpect(jsonPath("$.detail").value("Username admin is already taken"));
    }

    @Test
    void shouldSendBadRequesOnValidationErrors() throws Exception {
        String userJson = "{" +
                "\"username\": \"\"," +
                "\"password\": \"\"," +
                "\"role\": \"BUYER\"" +
                "}";

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("https://mvpmatch.co/docs/errors/validation-failed"))
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields failed validation"))
                .andExpect(jsonPath("$.username").value("must not be blank"))
                .andExpect(jsonPath("$.password").value("must not be blank"));
    }

    @Test
    void shouldValidateUserRole() throws Exception {
        String userJson = "{" +
                "\"username\": \"admin\"," +
                "\"password\": \"admin\"," +
                "\"role\": \"INVALID\"" +
                "}";

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("https://mvpmatch.co/docs/errors/validation-failed"))
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields failed validation"))
                .andExpect(jsonPath("$.role").value("Role must be either BUYER or SELLER"));
    }

}
