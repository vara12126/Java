package task07_executor;

/**
 * Immutable value object representing the outcome of a financial clearing transaction.
 */
public class TransactionResult {

    public enum Status {
        SUCCESS,
        REJECTED_COMPLIANCE,
        TIMEOUT_BREACH,
        GATEWAY_FAILURE
    }

    private final String transactionId;
    private final double amount;
    private final Status status;
    private final long latencyMs;
    private final double clearingFee;
    private final String message;

    public TransactionResult(String transactionId, double amount, Status status, 
                             long latencyMs, double clearingFee, String message) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.status = status;
        this.latencyMs = latencyMs;
        this.clearingFee = clearingFee;
        this.message = message;
    }

    public String getTransactionId() { return transactionId; }
    public double getAmount() { return amount; }
    public Status getStatus() { return status; }
    public long getLatencyMs() { return latencyMs; }
    public double getClearingFee() { return clearingFee; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return String.format("[%s | $%-9.2f | %-19s | %3d ms | Fee: $%.2f | %s]",
                transactionId, amount, status, latencyMs, clearingFee, message);
    }
}
