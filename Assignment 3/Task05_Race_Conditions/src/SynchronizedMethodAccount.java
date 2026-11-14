package task05_concurrency;

/**
 * Bank Account using coarse-grained synchronized methods.
 * Acquires the intrinsic lock of 'this' for the entire method execution.
 */
public class SynchronizedMethodAccount implements BankAccount {

    private double balance;
    private long transactionCount;

    public SynchronizedMethodAccount(double initialBalance) {
        this.balance = initialBalance;
        this.transactionCount = 0;
    }

    @Override
    public synchronized void deposit(double amount) {
        double current = this.balance;
        if (amount > 500) {
            Thread.yield();
        }
        this.balance = current + amount;
        this.transactionCount++;
    }

    @Override
    public synchronized void withdraw(double amount) {
        double current = this.balance;
        if (amount > 500) {
            Thread.yield();
        }
        this.balance = current - amount;
        this.transactionCount++;
    }

    @Override
    public synchronized double getBalance() {
        return this.balance;
    }

    @Override
    public synchronized long getTransactionCount() {
        return this.transactionCount;
    }

    @Override
    public String getImplementationName() {
        return "2. Synchronized Method (Intrinsic Lock)";
    }

    @Override
    public synchronized void reset(double initialBalance) {
        this.balance = initialBalance;
        this.transactionCount = 0;
    }
}
