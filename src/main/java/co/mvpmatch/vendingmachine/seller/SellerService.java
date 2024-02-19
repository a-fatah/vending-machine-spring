package co.mvpmatch.vendingmachine.seller;

import co.mvpmatch.vendingmachine.auth.db.User;
import co.mvpmatch.vendingmachine.auth.db.UserRepository;
import co.mvpmatch.vendingmachine.buyer.InsufficientDepositException;
import co.mvpmatch.vendingmachine.buyer.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SellerService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final SaleRepository saleRepository;

    public SellerService(ProductRepository productRepository, UserRepository userRepository, SaleRepository saleRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.saleRepository = saleRepository;
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

    public Sale sell(String username, Long productId, Integer quantity) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        if (product.getAmountAvailable() < quantity) {
            throw new ProductNotAvailableException(product.getName());
        }

        // check balance
        int totalCost = product.getCost() * quantity;
        if (user.getDeposit() < totalCost) {
            throw new InsufficientDepositException(user.getDeposit(), totalCost);
        }

        product.setAmountAvailable(product.getAmountAvailable() - quantity);
        productRepository.save(product);

        // Calculate change and update user balance
        int change = user.getDeposit() - totalCost;
        user.setDeposit(change);
        userRepository.save(user);

        // Record the transaction
        return createSale(username, productId, quantity, totalCost, change);
    }

    private Sale createSale(String username, Long productId, Integer amount, Integer cost, int change) {
        // Get the user and product from the database
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        // Check if the user has BUYER role
        if (!user.getRole().equals("BUYER")) {
            throw new OperationNotAllowedException("User is not a buyer");
        }

        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));

        // check if the product is available
        if (product.getAmountAvailable() < amount) {
            throw new OperationNotAllowedException("Product is not available");
        }

        // Create and store the sale
        Sale sale = new Sale();
        sale.setBuyer(user);
        sale.setProduct(product);
        sale.setAmount(amount);
        sale.setCost(cost);
        sale.setChangeAmount(change);
        sale.setTimestamp(System.currentTimeMillis());

        return saleRepository.save(sale);
    }

    public static Map<Integer, Integer> calculateChangeDenominations(int amount) {
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
