package co.mvpmatch.vendingmachine.transaction;

import co.mvpmatch.vendingmachine.auth.db.UserEntity;
import co.mvpmatch.vendingmachine.auth.db.UserRepository;
import co.mvpmatch.vendingmachine.buyer.UserNotFoundException;
import co.mvpmatch.vendingmachine.seller.Product;
import co.mvpmatch.vendingmachine.seller.ProductNotFoundException;
import co.mvpmatch.vendingmachine.seller.ProductRepository;
import org.springframework.stereotype.Service;

public interface TransactionService {
    Transaction createTransaction(String username, Long productId, Integer amount, Integer cost);
}

@Service
class TransactionServiceImpl implements TransactionService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(UserRepository userRepository, ProductRepository productRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction createTransaction(String username, Long productId, Integer amount, Integer cost) {
        // Get the user and product from the database
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        // Check if the user has BUYER role
        if (!user.getRole().equals("BUYER")) {
            throw new OperationNotAllowedException("User is not a buyer");
        }

        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));

        // check if the product is available
        if (product.getAmountAvailable() < amount) {
            throw new OperationNotAllowedException("Product is not available");
        }

        // Create the transaction
        Transaction transaction = new Transaction();
        transaction.setBuyer(user);
        transaction.setProduct(product);
        transaction.setAmount(amount);
        transaction.setCost(cost);
        transaction.setTimestamp(System.currentTimeMillis());

        // Save the transaction to the database
        return transactionRepository.save(transaction);
    }
}