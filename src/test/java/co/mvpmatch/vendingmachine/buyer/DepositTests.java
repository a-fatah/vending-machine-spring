package co.mvpmatch.vendingmachine.buyer;

import co.mvpmatch.vendingmachine.auth.SecurityConfig;
import co.mvpmatch.vendingmachine.auth.db.User;
import co.mvpmatch.vendingmachine.auth.db.UserRepository;
import co.mvpmatch.vendingmachine.auth.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BuyerController.class)
@Import({SecurityConfig.class, TestConfig.class})
@AutoConfigureMockMvc
public class DepositTests {
    private final UserRepository userRepository;
    private final MockMvc mockMvc;

    @Autowired
    public DepositTests(UserRepository userRepository, MockMvc mockMvc) {
        this.userRepository = userRepository;
        this.mockMvc = mockMvc;
    }

    @Test
    @WithMockUser(username = "buyer", roles = "BUYER")
    public void testDeposit() throws Exception {

        when(userRepository.findByUsername("buyer")).thenReturn(
                Optional.of(new User("buyer", "password", Role.BUYER)));

        mockMvc.perform(post("/deposit")
                .param("amount", "100"))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "buyer", roles = "BUYER")
    public void testDepositWithNegativeAmount() throws Exception {

        mockMvc.perform(post("/deposit")
                .param("amount", "-100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("https://vendingmachine.co/docs/errors/invalid-deposit-amount"))
                .andExpect(jsonPath("$.title").value("Invalid deposit amount"));
    }

    @Test
    @WithMockUser(username = "seller", roles = "SELLER")
    public void testDepositAsSeller() throws Exception {

        mockMvc.perform(post("/deposit")
                .param("amount", "100"))
                .andExpect(status().isForbidden());

    }

}
