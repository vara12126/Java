package task05_concurrency;

/**
 * Bank Account using fine-grained synchronized blocks.
 * Reduces critical section footprint by holding locks only during state mutation.
 */
public class SynchronizedBlockAccount implements BankAccount {

    private double balance;
    private long transactionCount;
    private final Object lock = new Object();

    public SynchronizedBlockAccount(double initialBalance) {
        this.balance = initialBalance;
        this.transactionCount = 0;
    }

    @Override
    public void deposit(double amount) {
        // Pre-processing / validation outside the critical section
        if (amount < 0) throw new IllegalArgumentException("Negative deposit");

        synchronized (lock) {
            double current = this.balance;
            if (amount > 500) {
                Thread.yield();
            }
            this.balance = current + amount;
            this.transactionCount++;
        }
    }

    @Override
    public void withdraw(double amount) {
        if (amount < 0) throw new IllegalArgumentException("Negative withdrawal");

        synchronized (lock) {
            double current = this.balance;
            if (amount > 500) {
                Thread.yield();
            }
            this.balance = current - amount;
            this.transactionCount++;
        }
    }

    @Override
    public double getBalance() {
        synchronized (lock) {
            return this.balance;
        }
    }

    @Override
    public long getTransactionCount() {
        synchronized (lock) {
            return this.transactionCount;
        }
    }

    @Override
    public String getImplementationName() {
        return "3. Synchronized Block (Fine-Grained Lock)";
    }

    @Override
    public void reset(double initialBalance) {
        synchronized (lock) {
            this.balance = initialBalance;
            this.transactionCount = 0;
        }
    }
}
