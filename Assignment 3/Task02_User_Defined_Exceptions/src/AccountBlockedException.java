package task02_exceptions;

/**
 * Custom checked exception thrown when transactions are attempted on a frozen/blocked account.
 */
public class AccountBlockedException extends BankingException {
    private final String accountNumber;
    private final String reason;
    private final int riskScore;

    public AccountBlockedException(String accountNumber, String reason, int riskScore) {
        super("ERR_ACCOUNT_BLOCKED", String.format(
                "Account %s is BLOCKED. Reason: '%s' (AML Risk Score: %d/100)",
                accountNumber, reason, riskScore));
        this.accountNumber = accountNumber;
        this.reason = reason;
        this.riskScore = riskScore;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getReason() { return reason; }
    public int getRiskScore() { return riskScore; }
}
