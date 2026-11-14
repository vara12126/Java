package task02_exceptions;

/**
 * Custom checked exception thrown when an account number format is invalid or non-existent.
 */
public class InvalidAccountException extends BankingException {
    private final String attemptedAccount;

    public InvalidAccountException(String attemptedAccount, String message) {
        super("ERR_INVALID_ACCOUNT", "Account validation failed for '" + attemptedAccount + "': " + message);
        this.attemptedAccount = attemptedAccount;
    }

    public String getAttemptedAccount() { return attemptedAccount; }
}
