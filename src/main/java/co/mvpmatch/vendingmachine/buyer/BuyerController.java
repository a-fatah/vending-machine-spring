package co.mvpmatch.vendingmachine.buyer;

import co.mvpmatch.vendingmachine.auth.db.UserEntity;
import co.mvpmatch.vendingmachine.auth.db.UserRepository;
import co.mvpmatch.vendingmachine.seller.ProductRepository;
import co.mvpmatch.vendingmachine.seller.SaleInfo;
import co.mvpmatch.vendingmachine.seller.SellerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class BuyerController {

    @Autowired
    private BuyerService buyerService;

    @Autowired
    private SellerService sellerService;

    @PostMapping("/deposit")
    @PreAuthorize("hasRole('BUYER')")
    public void deposit(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("amount") int amount) {
        buyerService.deposit(userDetails.getUsername(), amount);
    }

    @PostMapping("/buy")
    @PreAuthorize("hasRole('BUYER')")
    public SaleInfo buy(@AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid BuyDto buyDto) {
        return sellerService.sell(userDetails.getUsername(), buyDto.getProductId(), buyDto.getQuantity());
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

@Data
class BuyDto {
    @NotBlank
    private Long productId;
    @Positive
    private int quantity;
}
