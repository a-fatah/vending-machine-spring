package co.mvpmatch.vendingmachine.seller;

import lombok.Data;

import java.util.Map;

@Data
public class SaleInfo {
    private String productName;
    private int moneySpent;
    private Map<Integer, Integer> change;
}
