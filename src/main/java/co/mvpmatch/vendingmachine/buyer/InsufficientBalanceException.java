package co.mvpmatch.vendingmachine.buyer;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(int currentBalance, int requiredBalance) {
        super("Insufficient balance. Current balance: " + currentBalance + ", required balance: " + requiredBalance);
    }
}
