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
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @OneToOne
    private UserEntity buyer;
    @OneToOne
    private Product product;
    private Integer amount;
    private Integer cost;
    private Integer changeAmount;
    private Long timestamp;
}
