package co.mvpmatch.vendingmachine.auth;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@ContextConfiguration(classes = {SecurityConfig.class, SignupController.class})
class UserSignupTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SignupService signupService;

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

        doThrow(new UsernameTakenException("admin"))
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
    void shouldSendBadRequestWhenRoleIsInvalid() throws Exception {
        String userJson = "{" +
                "\"username\": \"admin\"," +
                "\"password\": \"admin\"," +
                "\"role\": \"INVALID\"" +
                "}";

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());
    }
}
