package co.mvpmatch.vendingmachine.buyer;

import co.mvpmatch.vendingmachine.auth.db.UserEntity;
import co.mvpmatch.vendingmachine.auth.db.UserRepository;
import co.mvpmatch.vendingmachine.seller.ProductRepository;
import co.mvpmatch.vendingmachine.seller.SellerService;
import org.springframework.stereotype.Service;

public interface BuyerService {
    void deposit(String username, int amount);
}

@Service
class BuyerServiceImpl implements BuyerService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    private final SellerService sellerService;

    BuyerServiceImpl(UserRepository userRepository, ProductRepository productRepository, SellerService sellerService) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.sellerService = sellerService;
    }

    @Override
    public void deposit(String username, int amount) {
        if (!isValidDepositAmount(amount)) {
            throw new InvalidDepositAmountException(amount);
        }

        // retrieve user
        UserEntity buyer = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        // update user balance
        int currentBalance = buyer.getDeposit() != null ? buyer.getDeposit() : 0;
        buyer.setDeposit(currentBalance + amount);

        userRepository.save(buyer);
    }

    private boolean isValidDepositAmount(int amount) {
        if (amount < 0) {
            return false;
        }

        return amount % 5 == 0;
    }
}