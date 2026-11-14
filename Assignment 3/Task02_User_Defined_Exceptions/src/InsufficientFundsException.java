package task02_exceptions;

/**
 * Custom checked exception thrown when a withdrawal or transfer amount exceeds available funds.
 */
public class InsufficientFundsException extends BankingException {
    private final String accountNumber;
    private final double requestedAmount;
    private final double currentBalance;
    private final double shortfall;

    public InsufficientFundsException(String accountNumber, double requestedAmount, double currentBalance) {
        super("ERR_INSUFFICIENT_FUNDS", String.format(
                "Account %s has insufficient funds. Requested: $%.2f, Available: $%.2f, Shortfall: $%.2f",
                accountNumber, requestedAmount, currentBalance, (requestedAmount - currentBalance)));
        this.accountNumber = accountNumber;
        this.requestedAmount = requestedAmount;
        this.currentBalance = currentBalance;
        this.shortfall = requestedAmount - currentBalance;
    }

    public String getAccountNumber() { return accountNumber; }
    public double getRequestedAmount() { return requestedAmount; }
    public double getCurrentBalance() { return currentBalance; }
    public double getShortfall() { return shortfall; }
}
