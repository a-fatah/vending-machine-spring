package co.mvpmatch.vendingmachine.seller;

import co.mvpmatch.vendingmachine.auth.db.User;
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
    User seller;

    public Product(String name, Integer amountAvailable, Integer cost) {
        this.name = name;
        this.amountAvailable = amountAvailable;
        this.cost = cost;
        this.seller = seller;
    }

    public Product(String name, Integer amountAvailable, Integer cost, User seller) {
        this.name = name;
        this.amountAvailable = amountAvailable;
        this.cost = cost;
        this.seller = seller;
    }
}
