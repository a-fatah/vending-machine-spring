package co.mvpmatch.vendingmachine.buyer;

import co.mvpmatch.vendingmachine.auth.SecurityConfig;
import co.mvpmatch.vendingmachine.auth.db.User;
import co.mvpmatch.vendingmachine.auth.db.UserRepository;
import co.mvpmatch.vendingmachine.auth.model.Role;
import co.mvpmatch.vendingmachine.seller.Product;
import co.mvpmatch.vendingmachine.seller.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BuyerController.class)
@Import({SecurityConfig.class, TestConfig.class})
public class BuyTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Test
    @WithMockUser(username = "buyer", roles = "BUYER")
    void buyInvalidProductTest() throws Exception {
        var buyer = new User("buyer", "password", Role.BUYER);
        when(userRepository.findByUsername("buyer")).thenReturn(Optional.of(buyer));

        mockMvc.perform(post("/buy")
                .contentType("application/json")
                .content("{\"productId\": 1, \"quantity\": 1}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("https://vendingmachine.co/docs/errors/product-not-found"))
                .andExpect(jsonPath("$.title").value("Product not found"))
                .andExpect(jsonPath("$.detail").value("Product with id 1 not found"));
    }
}
