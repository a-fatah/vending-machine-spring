package co.mvpmatch.vendingmachine.buyer;

public class InsufficientDepositException extends RuntimeException {
    public InsufficientDepositException(int currentBalance, int requiredBalance) {
        super("Insufficient deposit. Current deposit: " + currentBalance + ", required deposit: " + requiredBalance);
    }
}
