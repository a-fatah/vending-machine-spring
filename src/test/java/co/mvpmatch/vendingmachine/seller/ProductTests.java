package co.mvpmatch.vendingmachine.seller;

import co.mvpmatch.vendingmachine.auth.SecurityConfig;
import co.mvpmatch.vendingmachine.auth.db.User;
import co.mvpmatch.vendingmachine.auth.db.UserRepository;
import co.mvpmatch.vendingmachine.auth.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class, TestConfig.class})
public class ProductTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "seller", roles = "SELLER")
    public void testCreateProductAsSeller() throws Exception {

        when(userRepository.findByUsername("seller")).thenReturn(Optional.of(new User("seller", Role.SELLER)));

        mockMvc.perform(post("/product")
                .contentType("application/json")
                .content("{\"name\": \"Coke\", \"amountAvailable\": 10, \"cost\": 100}"))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "buyer", roles = "BUYER")
    public void testCreateProductAsBuyer() throws Exception {

        when(userRepository.findByUsername("buyer")).thenReturn(Optional.of(new User("buyer", Role.BUYER)));

        mockMvc.perform(post("/product")
                .contentType("application/json")
                .content("{\"name\": \"Coke\", \"amountAvailable\": 10, \"cost\": 100}"))
                .andExpect(status().isForbidden());

    }


}
