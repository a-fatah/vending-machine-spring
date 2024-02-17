package co.mvpmatch.vendingmachine.buyer;

public class InvalidDepositAmountException extends RuntimeException {
    public InvalidDepositAmountException(int amount) {
        super("Invalid deposit amount: " + amount);
    }
}
