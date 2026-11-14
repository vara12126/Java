package task06_deadlock_queue;

/**
 * Account entity with unique account ID for transfer operations and lock ordering.
 */
public class Account {

    private final String id;
    private double balance;

    public Account(String id, double balance) {
        this.id = id;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public synchronized double getBalance() {
        return balance;
    }

    public synchronized void debit(double amount) {
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient funds on " + id);
        }
        balance -= amount;
    }

    public synchronized void credit(double amount) {
        balance += amount;
    }

    @Override
    public String toString() {
        return String.format("[%s: Balance $%.2f]", id, balance);
    }
}
