package co.mvpmatch.vendingmachine.buyer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BuyerController.class)
@EnableWebSecurity
public class DepositTests {

    @Autowired
    WebApplicationContext context;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private BuyerService buyerService;

    private MockMvc mockMvc;

    // setup mockmvc

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "buyer", roles = "BUYER")
    public void testDeposit() throws Exception {

        mockMvc.perform(post("/deposit")
                .param("amount", "100"))
                .andExpect(status().isOk());

        verify(buyerService, never()).deposit(any(), anyInt());
        verify(userDetailsService, never()).loadUserByUsername(any());

    }

}
