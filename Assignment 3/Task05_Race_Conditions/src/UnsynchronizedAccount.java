package task05_concurrency;

/**
 * Unsynchronized Bank Account implementation vulnerable to race conditions,
 * read-modify-write hazards, and lost updates.
 */
public class UnsynchronizedAccount implements BankAccount {

    private double balance;
    private long transactionCount;

    public UnsynchronizedAccount(double initialBalance) {
        this.balance = initialBalance;
        this.transactionCount = 0;
    }

    @Override
    public void deposit(double amount) {
        // Read-modify-write race condition
        double current = this.balance;
        this.balance = current + amount;
        long tc = this.transactionCount;
        this.transactionCount = tc + 1;
    }

    @Override
    public void withdraw(double amount) {
        // Read-modify-write race condition
        double current = this.balance;
        this.balance = current - amount;
        long tc = this.transactionCount;
        this.transactionCount = tc + 1;
    }

    @Override
    public double getBalance() {
        return this.balance;
    }

    @Override
    public long getTransactionCount() {
        return this.transactionCount;
    }

    @Override
    public String getImplementationName() {
        return "1. Unsynchronized (Data Race)";
    }

    @Override
    public void reset(double initialBalance) {
        this.balance = initialBalance;
        this.transactionCount = 0;
    }
}
