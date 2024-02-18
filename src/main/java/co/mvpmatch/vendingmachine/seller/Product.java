package co.mvpmatch.vendingmachine.seller;

import co.mvpmatch.vendingmachine.auth.db.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    String name;

    Integer amountAvailable;

    Integer cost;

    @ManyToOne
    @JoinColumn(name = "seller")
    UserEntity seller;

    public Product(String name, Integer amountAvailable, Integer cost, UserEntity seller) {
        this.name = name;
        this.amountAvailable = amountAvailable;
        this.cost = cost;
        this.seller = seller;
    }
}
