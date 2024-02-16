package co.mvpmatch.vendingmachine.auth;

import co.mvpmatch.vendingmachine.auth.model.Role;
import co.mvpmatch.vendingmachine.auth.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@EnableWebSecurity
@ContextConfiguration(classes = {SecurityConfig.class, JwtAuthFilter.class, JwtService.class, UserService.class})
public class JwtAuthFilterTest {

    private static final String MOCK_USER = "testUser";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataSource dataSource;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @Test
    public void testWithValidJwtToken() throws Exception {

        UserDetails userDetails = new User(MOCK_USER, null, Role.BUYER);

        String jwtToken = jwtService.generateToken(MOCK_USER);

        when(userService.loadUserByUsername(anyString())).thenReturn(userDetails);

        mockMvc.perform(get("/api/hello").header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testWithInvalidJwtToken() throws Exception {
        mockMvc.perform(get("/api").header("Authorization", "Bearer invalidToken"))
                .andExpect(status().isBadRequest());

    }
}
