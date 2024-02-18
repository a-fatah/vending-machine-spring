package co.mvpmatch.vendingmachine.seller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public void createProduct(@Valid @RequestBody ProductDto productDto, @AuthenticationPrincipal UserDetails userDetails) {

        String sellerUsername = userDetails.getUsername();
        String productName = productDto.getName();
        int productCost = productDto.getCost();
        int productAmountAvailable = productDto.getAmountAvailable();

        productService.createProduct(sellerUsername, productName, productCost, productAmountAvailable);
    }
}

@Data
class ProductDto {
    @NotBlank
    private String name;
    @Positive
    private int cost;
    @Positive
    private int amountAvailable;
}
