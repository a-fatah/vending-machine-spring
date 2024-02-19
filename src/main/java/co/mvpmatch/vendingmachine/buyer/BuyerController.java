package co.mvpmatch.vendingmachine.buyer;

import co.mvpmatch.vendingmachine.seller.ProductNotFoundException;
import co.mvpmatch.vendingmachine.seller.Sale;
import co.mvpmatch.vendingmachine.seller.SellerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

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
    public SaleDto buy(@AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid BuyDto buyDto) {
        Sale sale = sellerService.sell(userDetails.getUsername(), buyDto.getProductId(), buyDto.getQuantity());

        var saleDto = new SaleDto();
        saleDto.setProductName(sale.getProduct().getName());
        saleDto.setMoneySpent(sale.getCost());
        saleDto.setChange(SellerService.calculateChangeDenominations(sale.getChangeAmount()));
        return saleDto;
    }

    @PostMapping("/reset")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('BUYER')")
    public void resetDeposit(@AuthenticationPrincipal UserDetails userDetails) {
        buyerService.resetDeposit(userDetails.getUsername());
    }

    @ExceptionHandler(InvalidDepositAmountException.class)
    public ProblemDetail handleInvalidDepositAmountException(InvalidDepositAmountException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid deposit amount");
        problemDetail.setTitle("Invalid deposit amount");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setType(URI.create("https://vendingmachine.co/docs/errors/invalid-deposit-amount"));
        return problemDetail;
    }

    @ExceptionHandler(InsufficientDepositException.class)
    public ProblemDetail handleInsufficientDepositException(InsufficientDepositException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Insufficient deposit");
        problemDetail.setTitle("Insufficient Deposit");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setType(URI.create("https://vendingmachine.co/docs/errors/insufficient-deposit"));
        return problemDetail;
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ProblemDetail handleProductNotFoundException(ProductNotFoundException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Product not found");
        problemDetail.setTitle("Product not found");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setType(URI.create("https://vendingmachine.co/docs/errors/product-not-found"));
        return problemDetail;
    }

}

@Data
class BuyDto {
    @NotNull
    private Long productId;
    @Positive
    private int quantity;
}

@Data
class SaleDto {
    private String productName;
    private int moneySpent;
    private Map<Integer, Integer> change;
}
