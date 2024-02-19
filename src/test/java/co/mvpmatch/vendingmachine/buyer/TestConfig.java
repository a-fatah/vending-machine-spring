package co.mvpmatch.vendingmachine.buyer;

import co.mvpmatch.vendingmachine.auth.db.UserRepository;
import co.mvpmatch.vendingmachine.seller.ProductRepository;
import co.mvpmatch.vendingmachine.seller.SellerService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.sql.DataSource;

@Configuration
public class TestConfig {
    @MockBean
    private SellerService sellerService;
    @MockBean
    private DataSource dataSource;
    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ProductRepository productRepository;

    @Bean
    public BuyerService buyerService() {
        return new BuyerServiceImpl(userRepository, productRepository, sellerService);
    }
}
