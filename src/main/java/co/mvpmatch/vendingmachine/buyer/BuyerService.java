package co.mvpmatch.vendingmachine.buyer;

public interface BuyerService {
    void deposit(String username, int amount);

    void resetDeposit(String username);
}
