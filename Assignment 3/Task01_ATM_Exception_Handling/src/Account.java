package task01_atm;

/**
 * Bank Account entity holding account number, PIN hash, and current balance.
 * 
 * Student A: Devi Vara Prasad (Reg: 192365032)
 * Student B: [Student B Name] (Reg: [Student B Register Number])
 */
public class Account {
    private final String accountNumber;
    private final String pin;
    private double balance;
    private boolean locked;
    private int failedPinAttempts;

    public Account(String accountNumber, String pin, double initialBalance) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = initialBalance;
        this.locked = false;
        this.failedPinAttempts = 0;
    }

    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public boolean isLocked() { return locked; }

    public boolean validatePin(String enteredPin) {
        if (locked) {
            throw new IllegalStateException("Account " + accountNumber + " is LOCKED due to excessive failed attempts.");
        }
        if (enteredPin == null || enteredPin.trim().isEmpty()) {
            throw new IllegalArgumentException("PIN cannot be null or empty.");
        }
        if (this.pin.equals(enteredPin)) {
            failedPinAttempts = 0;
            return true;
        } else {
            failedPinAttempts++;
            if (failedPinAttempts >= 3) {
                locked = true;
                throw new SecurityException("Security Alert: 3 consecutive invalid PIN entries! Account " + accountNumber + " has been LOCKED.");
            }
            return false;
        }
    }

    public void debit(double amount) {
        this.balance -= amount;
    }
}
