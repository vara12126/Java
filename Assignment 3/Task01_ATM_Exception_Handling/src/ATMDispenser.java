package task01_atm;

/**
 * Simulates physical ATM cash dispenser hardware with finite currency notes.
 */
public class ATMDispenser {
    private double vaultCash;
    private final double maxPerTransactionLimit;

    public ATMDispenser(double initialVaultCash, double maxPerTransactionLimit) {
        this.vaultCash = initialVaultCash;
        this.maxPerTransactionLimit = maxPerTransactionLimit;
    }

    public double getVaultCash() { return vaultCash; }

    public void dispense(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be strictly positive (Amount: $" + amount + ").");
        }
        if (amount % 100 != 0) {
            throw new IllegalArgumentException("Invalid Denomination: ATM can only dispense in multiples of $100 (Requested: $" + amount + ").");
        }
        if (amount > maxPerTransactionLimit) {
            throw new IllegalArgumentException("Per-Transaction Limit Exceeded: Max limit is $" + maxPerTransactionLimit + " (Requested: $" + amount + ").");
        }
        if (amount > vaultCash) {
            throw new IllegalStateException("ATM Hardware Out-of-Cash: Requested $" + amount + " but vault contains only $" + vaultCash + ".");
        }
        vaultCash -= amount;
    }
}
