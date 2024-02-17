package co.mvpmatch.vendingmachine.buyer;

import co.mvpmatch.vendingmachine.auth.db.UserEntity;
import co.mvpmatch.vendingmachine.auth.db.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class BuyerController {

    @Autowired
    private BuyerService buyerService;

    @PostMapping("/deposit")
    @PreAuthorize("hasRole('BUYER')")
    public void deposit(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("amount") int amount) {
        buyerService.deposit(userDetails.getUsername(), amount);
    }

    @ExceptionHandler(InvalidDepositAmountException.class)
    public ProblemDetail handleInvalidDepositAmountException(InvalidDepositAmountException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid deposit amount");
        problemDetail.setTitle("Invalid deposit amount");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setType(URI.create("https://vendingmachine.co/docs/errors/invalid-deposit-amount"));
        return problemDetail;
    }

}

interface BuyerService {
    void deposit(String username, int amount);
}

@Service
class BuyerServiceImpl implements BuyerService {

    private final UserRepository userRepository;

    BuyerServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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


class InvalidDepositAmountException extends RuntimeException {
    public InvalidDepositAmountException(int amount) {
        super("Invalid deposit amount: " + amount);
    }
}

class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super("User not found: " + username);
    }
}


