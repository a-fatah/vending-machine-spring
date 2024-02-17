package co.mvpmatch.vendingmachine.seller;

import co.mvpmatch.vendingmachine.auth.db.UserEntity;
import co.mvpmatch.vendingmachine.auth.db.UserRepository;
import co.mvpmatch.vendingmachine.buyer.InsufficientBalanceException;
import co.mvpmatch.vendingmachine.buyer.UserNotFoundException;
import co.mvpmatch.vendingmachine.transaction.OperationNotAllowedException;
import co.mvpmatch.vendingmachine.transaction.TransactionService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SellerService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;

    public SellerService(ProductRepository productRepository, UserRepository userRepository, TransactionService transactionService) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
    }

    public Product createProduct(String name, Integer amountAvailable, Integer cost) {
        Product product = new Product();
        product.setName(name);
        product.setAmountAvailable(amountAvailable);
        product.setCost(cost);
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, String name, Integer amountAvailable, Integer cost) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        product.setName(name);
        product.setAmountAvailable(amountAvailable);
        product.setCost(cost);
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public SaleInfo sell(String username, Long productId, Integer quantity) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (!user.getRole().equals("buyer")) {
            throw new OperationNotAllowedException("Only buyers can buy products");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        if (product.getAmountAvailable() < quantity) {
            throw new ProductNotAvailableException(product.getName());
        }

        // check balance
        int totalCost = product.getCost() * quantity;
        if (user.getDeposit() < totalCost) {
            throw new InsufficientBalanceException(user.getDeposit(), totalCost);
        }

        product.setAmountAvailable(product.getAmountAvailable() - quantity);
        productRepository.save(product);

        // Record the transaction
        transactionService.createTransaction(username, productId, quantity, product.getCost() * quantity);

        // Calculate change and update user balance
        int change = user.getDeposit() - totalCost;

        Map<Integer, Integer> changeCoins = calculateChange(change);

        user.setDeposit(change);

        userRepository.save(user);

        // return product purchased, money spent and change
        System.out.println("Product purchased: " + product.getName());
        System.out.println("Money spent: " + totalCost);
        System.out.println("Change: " + changeCoins);

        SaleInfo saleInfo = new SaleInfo();
        saleInfo.setProductName(product.getName());
        saleInfo.setMoneySpent(totalCost);
        saleInfo.setChange(changeCoins);


        return saleInfo;
    }

    private Map<Integer, Integer> calculateChange(int amount) {
        int[] allowedCoins = {5, 10, 20, 50, 100};
        HashMap<Integer, Integer> coinCounts = new HashMap<>();

        for (int coin : allowedCoins) {
            coinCounts.put(coin, 0);
        }

        while (amount > 0) {
            for (int coin : allowedCoins) {
                if (amount >= coin) {
                    amount -= coin;
                    coinCounts.put(coin, coinCounts.get(coin) + 1);
                    break; // Move to the next coin denomination
                }
            }
        }

        return coinCounts;
    }
}

class ProductNotAvailableException extends RuntimeException {
    public ProductNotAvailableException(String productName) {
        super("Product " + productName + " is not available");
    }
}
