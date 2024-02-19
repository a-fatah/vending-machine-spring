package co.mvpmatch.vendingmachine.buyer;

import co.mvpmatch.vendingmachine.auth.db.UserRepository;
import co.mvpmatch.vendingmachine.seller.ProductRepository;
import co.mvpmatch.vendingmachine.seller.SaleRepository;
import co.mvpmatch.vendingmachine.seller.SellerService;
import org.mockito.Mock;
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

    @MockBean
    private SaleRepository saleRepository;

    @Bean
    public BuyerService buyerService(SellerService sellerService) {
        return new BuyerServiceImpl(userRepository, productRepository, sellerService);
    }

    @Bean
    public SellerService sellerService() {
        return new SellerService(productRepository, userRepository, saleRepository);
    }
}
