package task05_concurrency;

import java.util.concurrent.atomic.AtomicLong;

/**
 * High-performance lock-free Bank Account using AtomicLong (stored in cents)
 * and Compare-And-Swap (CAS) CPU instructions.
 */
public class AtomicAccount implements BankAccount {

    // Balance stored in cents to eliminate floating-point precision loss
    private final AtomicLong balanceInCents;
    private final AtomicLong transactionCount;

    public AtomicAccount(double initialBalance) {
        this.balanceInCents = new AtomicLong(Math.round(initialBalance * 100.0));
        this.transactionCount = new AtomicLong(0);
    }

    @Override
    public void deposit(double amount) {
        long cents = Math.round(amount * 100.0);
        balanceInCents.addAndGet(cents);
        transactionCount.incrementAndGet();
    }

    @Override
    public void withdraw(double amount) {
        long cents = Math.round(amount * 100.0);
        balanceInCents.addAndGet(-cents);
        transactionCount.incrementAndGet();
    }

    @Override
    public double getBalance() {
        return balanceInCents.get() / 100.0;
    }

    @Override
    public long getTransactionCount() {
        return transactionCount.get();
    }

    @Override
    public String getImplementationName() {
        return "4. Atomic Lock-Free (CAS Hardware Operations)";
    }

    @Override
    public void reset(double initialBalance) {
        this.balanceInCents.set(Math.round(initialBalance * 100.0));
        this.transactionCount.set(0);
    }
}
