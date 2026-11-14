package task07_executor;

import java.util.concurrent.Callable;

/**
 * Clearing task implementing Callable<TransactionResult>.
 * Simulates financial clearing network latency, fraud screening, and gateway interactions.
 */
public class TransactionTask implements Callable<TransactionResult> {

    private final String transactionId;
    private final double amount;
    private final int simulatedLatencyMs;
    private final boolean forceFailure;

    public TransactionTask(String transactionId, double amount, int simulatedLatencyMs, boolean forceFailure) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.simulatedLatencyMs = simulatedLatencyMs;
        this.forceFailure = forceFailure;
    }

    @Override
    public TransactionResult call() throws Exception {
        long start = System.currentTimeMillis();

        // 1. Simulate network and bank gateway latency
        if (simulatedLatencyMs > 0) {
            Thread.sleep(simulatedLatencyMs);
        }

        // 2. Simulated Gateway Outage
        if (forceFailure) {
            long latency = System.currentTimeMillis() - start;
            throw new IllegalStateException("Federal ACH Gateway timeout/connection reset for " + transactionId);
        }

        // 3. Compliance and AML threshold check
        if (amount > 1_000_000.0) {
            long latency = System.currentTimeMillis() - start;
            return new TransactionResult(transactionId, amount, TransactionResult.Status.REJECTED_COMPLIANCE,
                    latency, 0.0, "Blocked: Exceeds single-batch AML threshold ($1,000,000.00)");
        }

        // 4. Successful clearing calculation
        double fee = Math.max(1.50, amount * 0.0005); // 0.05% clearing fee, minimum $1.50
        long latency = System.currentTimeMillis() - start;

        return new TransactionResult(transactionId, amount, TransactionResult.Status.SUCCESS,
                latency, fee, "Clearing settled through Fedwire");
    }

    public String getTransactionId() {
        return transactionId;
    }
}
