package task02_exceptions;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Service Layer demonstrating method declaration throws, explicit throw,
 * layered exception propagation, and exception chaining.
 */
public class FundTransferService {

    public static class AccountRecord {
        public String accountId;
        public double balance;
        public boolean isBlocked;
        public String blockReason;
        public int riskScore;

        public AccountRecord(String accountId, double balance, boolean isBlocked, String blockReason, int riskScore) {
            this.accountId = accountId;
            this.balance = balance;
            this.isBlocked = isBlocked;
            this.blockReason = blockReason;
            this.riskScore = riskScore;
        }
    }

    private final Map<String, AccountRecord> accounts = new HashMap<>();

    public FundTransferService() {
        accounts.put("ACC-1001", new AccountRecord("ACC-1001", 5000.0, false, "", 10));
        accounts.put("ACC-2002", new AccountRecord("ACC-2002", 1200.0, false, "", 15));
        accounts.put("ACC-3003", new AccountRecord("ACC-3003", 50000.0, true, "Suspicious AML structuring activity detected", 92));
    }

    /**
     * Executes an end-to-end fund transfer with layered exception propagation.
     * 
     * @throws InvalidAccountException if account syntax or lookup fails
     * @throws AccountBlockedException if either account is frozen
     * @throws InsufficientFundsException if source balance is inadequate
     * @throws TransactionProcessingException if low-level infrastructure crashes (Chained)
     */
    public String transferFunds(String sourceAcc, String destAcc, double amount, boolean simulateNetworkTimeout)
            throws InvalidAccountException, AccountBlockedException, InsufficientFundsException, TransactionProcessingException {

        // 1. Validate Account Syntactic Pattern
        validateAccountFormat(sourceAcc);
        validateAccountFormat(destAcc);

        // 2. Validate Existence
        AccountRecord source = accounts.get(sourceAcc);
        AccountRecord dest = accounts.get(destAcc);

        if (source == null) throw new InvalidAccountException(sourceAcc, "Source account not found in core ledger.");
        if (dest == null) throw new InvalidAccountException(destAcc, "Destination account not found in core ledger.");

        // 3. Validate Account State
        if (source.isBlocked) {
            throw new AccountBlockedException(source.accountId, source.blockReason, source.riskScore);
        }
        if (dest.isBlocked) {
            throw new AccountBlockedException(dest.accountId, dest.blockReason, dest.riskScore);
        }

        // 4. Validate Balance
        if (amount > source.balance) {
            throw new InsufficientFundsException(source.accountId, amount, source.balance);
        }

        // 5. Simulate Low-Level Network / Database Failure (Exception Chaining)
        if (simulateNetworkTimeout) {
            try {
                // Low-level Java IO / Socket timeout fault
                throw new SocketTimeoutException("Read timed out after 30000ms while contacting Payment Gateway clearing house.");
            } catch (IOException lowLevelCause) {
                // EXCEPTION CHAINING: Wrap low-level cause in domain TransactionProcessingException
                String txnRef = "TXN-" + System.currentTimeMillis();
                throw new TransactionProcessingException(txnRef, "Core banking clearing pipeline failure", lowLevelCause);
            }
        }

        // Execute ledger debit & credit
        source.balance -= amount;
        dest.balance += amount;

        return String.format("SUCCESS: Transferred $%.2f from %s (New Bal: $%.2f) to %s (New Bal: $%.2f)",
                amount, sourceAcc, source.balance, destAcc, dest.balance);
    }

    private void validateAccountFormat(String acc) throws InvalidAccountException {
        if (acc == null || !acc.matches("^ACC-\\d{4}$")) {
            throw new InvalidAccountException(acc, "Account format must strictly follow 'ACC-XXXX' where X is a numeric digit.");
        }
    }
}
