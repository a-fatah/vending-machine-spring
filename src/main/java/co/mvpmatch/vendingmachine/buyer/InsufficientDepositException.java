package co.mvpmatch.vendingmachine.buyer;

public class InsufficientDepositException extends RuntimeException {
    public InsufficientDepositException(int currentBalance, int requiredBalance) {
        super("Insufficient balance. Current balance: " + currentBalance + ", required balance: " + requiredBalance);
    }
}
