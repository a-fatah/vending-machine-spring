package co.mvpmatch.vendingmachine.auth.signup.validation;

public class UsernameTakenException extends RuntimeException {
    public UsernameTakenException(String username) {
        super("Username " + username + " is already taken");
    }
}
