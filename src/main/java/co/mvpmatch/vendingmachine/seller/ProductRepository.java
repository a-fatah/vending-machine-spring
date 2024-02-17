package co.mvpmatch.vendingmachine.seller;

import co.mvpmatch.vendingmachine.seller.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
