package task04_lifecycle;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;

/**
 * Worker task that simulates portfolio statement generation for investors.
 * Implements Runnable to allow separation of task logic from thread execution.
 */
public class StatementWorker implements Runnable {

    private final String investorId;
    private final int portfolioHoldings;
    private final int simulatedDelayMs;
    private final List<String> completionOrderTracker;
    private final AtomicInteger globalCounter;

    public StatementWorker(String investorId, int portfolioHoldings, int simulatedDelayMs,
                           List<String> completionOrderTracker, AtomicInteger globalCounter) {
        this.investorId = investorId;
        this.portfolioHoldings = portfolioHoldings;
        this.simulatedDelayMs = simulatedDelayMs;
        this.completionOrderTracker = completionOrderTracker;
        this.globalCounter = globalCounter;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        // Step 1: Computation simulation
        long hash = 0;
        for (int i = 0; i < portfolioHoldings * 1000; i++) {
            hash += (i * 31L);
        }

        // Step 2: Timed waiting simulation (e.g. fetching external stock quotes or disk write)
        try {
            if (simulatedDelayMs > 0) {
                Thread.sleep(simulatedDelayMs);
            }
        } catch (InterruptedException e) {
            System.err.println("[" + threadName + "] Interrupted during sleep.");
            Thread.currentThread().interrupt();
            return;
        }

        // Step 3: Record completion order
        int order = globalCounter.incrementAndGet();
        if (completionOrderTracker != null) {
            completionOrderTracker.add(threadName + " (" + investorId + ")");
        }
    }

    public String getInvestorId() {
        return investorId;
    }
}
