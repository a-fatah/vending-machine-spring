package co.mvpmatch.vendingmachine.seller;

import co.mvpmatch.vendingmachine.auth.db.User;
import co.mvpmatch.vendingmachine.auth.db.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

public interface ProductService {
    Product createProduct(String seller, String name, int cost, int amountAvailable);
}

@Slf4j
@Service
class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    ProductServiceImpl(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Product createProduct(String username, String name, int cost, int amountAvailable) {
        log.info("User {} is creating product", username);
        log.info("Creating product with name: {}, cost: {}, amountAvailable: {}", name, cost, amountAvailable);

        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = new Product(name, amountAvailable, cost, seller);

        return productRepository.save(product);
    }
}
