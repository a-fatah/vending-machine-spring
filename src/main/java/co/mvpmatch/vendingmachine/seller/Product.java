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

    @OneToOne
    UserEntity seller;
}
