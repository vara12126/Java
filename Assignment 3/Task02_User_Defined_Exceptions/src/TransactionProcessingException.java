package task02_exceptions;

/**
 * High-level system exception demonstrating EXCEPTION CHAINING.
 * Wraps low-level database, network, or third-party gateway failures while
 * preserving the complete underlying cause chain via Throwable.getCause().
 */
public class TransactionProcessingException extends BankingException {
    private final String transactionReferenceId;

    public TransactionProcessingException(String transactionReferenceId, String message, Throwable cause) {
        super("ERR_TXN_SYSTEM_FAULT", "Transaction [" + transactionReferenceId + "] aborted: " + message, cause);
        this.transactionReferenceId = transactionReferenceId;
    }

    public String getTransactionReferenceId() {
        return transactionReferenceId;
    }
}
