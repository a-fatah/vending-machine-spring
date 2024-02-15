package co.mvpmatch.vendingmachine.auth;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@ContextConfiguration(classes = {SecurityConfig.class, SignupController.class})
class UserSignupTest {

    @Autowired
    private MockMvc mockMvc;

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
                .andExpect(status().isCreated());
    }

    @Test
    void shouldSendBadRequestWhenUsernameIsTaken() throws Exception {
        String userJson = "{" +
                "\"username\": \"admin\"," +
                "\"password\": \"admin\"," +
                "\"role\": \"BUYER\"" +
                "}";

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldSendBadRequestWhenUsernameIsInvalid() throws Exception {
        String userJson = "{" +
                "\"username\": \"\"," +
                "\"password\": \"admin\"," +
                "\"role\": \"BUYER\"" +
                "}";

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldSendBadRequestWhenPasswordIsInvalid() throws Exception {
        String userJson = "{" +
                "\"username\": \"admin\"," +
                "\"password\": \"\"," +
                "\"role\": \"BUYER\"" +
                "}";

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());
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
