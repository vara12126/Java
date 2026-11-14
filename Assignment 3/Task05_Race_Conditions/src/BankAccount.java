package task05_concurrency;

/**
 * Interface defining banking account operations for concurrency and race condition analysis.
 */
public interface BankAccount {

    void deposit(double amount);

    void withdraw(double amount);

    double getBalance();

    long getTransactionCount();

    String getImplementationName();

    void reset(double initialBalance);
}
