package co.mvpmatch.vendingmachine.transaction;

import co.mvpmatch.vendingmachine.auth.db.UserEntity;
import co.mvpmatch.vendingmachine.auth.db.UserRepository;
import co.mvpmatch.vendingmachine.auth.model.User;
import co.mvpmatch.vendingmachine.buyer.UserNotFoundException;
import co.mvpmatch.vendingmachine.seller.Product;
import co.mvpmatch.vendingmachine.seller.ProductNotFoundException;
import co.mvpmatch.vendingmachine.seller.ProductRepository;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @OneToOne
    private UserEntity buyer;
    @OneToOne
    private Product product;
    private Integer amount;
    private Integer cost;
    private Long timestamp;
}

