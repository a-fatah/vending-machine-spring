package co.mvpmatch.vendingmachine.seller;

import co.mvpmatch.vendingmachine.auth.db.UserRepository;
import co.mvpmatch.vendingmachine.buyer.BuyerService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.sql.DataSource;

@Configuration
public class TestConfig {
    @MockBean
    private DataSource dataSource;
    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ProductRepository productRepository;

    @Bean
    public ProductService productService() {
        return new ProductServiceImpl(productRepository, userRepository);
    }
}
