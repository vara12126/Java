package task01_atm;

/**
 * Task 01: ATM Exception Handling Demonstration.
 * 
 * Demonstrates:
 * 1. Java Exception hierarchy (Checked vs Unchecked).
 * 2. Multi-catch syntax: catch (NumberFormatException | NullPointerException e).
 * 3. Specific to general catch block ordering.
 * 4. Mandatory finally block execution for card ejection, resource cleanup, and audit logging.
 * 5. Stack trace analysis and graceful error reporting.
 * 
 * Student A: Devi Vara Prasad (Reg: 192365032)
 * Student B: [Student B Name] (Reg: [Student B Register Number])
 */
public class ATMExceptionHandlingDemo {

    public static void processAtmTransaction(Account account, ATMDispenser dispenser, String pinInput, String amountInput) {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Processing ATM Transaction for Account: " + account.getAccountNumber());
        System.out.println("Entered PIN: [" + pinInput + "] | Requested Amount: [" + amountInput + "]");
        
        boolean cardInserted = true;
        boolean sessionActive = true;

        try {
            System.out.println("[STAGE 1] Validating Security PIN...");
            boolean pinOk = account.validatePin(pinInput);
            if (!pinOk) {
                System.out.println("[FAILED] Invalid PIN. Please try again.");
                return;
            }
            System.out.println("[STAGE 1 PASSED] PIN Verified.");

            System.out.println("[STAGE 2] Parsing Withdrawal Amount...");
            // Non-numeric or null input triggers NumberFormatException or NullPointerException
            if (amountInput == null) {
                throw new NullPointerException("Withdrawal amount input string is null.");
            }
            double amount = Double.parseDouble(amountInput);
            System.out.println("[STAGE 2 PASSED] Parsed Amount: $" + amount);

            System.out.println("[STAGE 3] Checking Account Balance...");
            if (amount > account.getBalance()) {
                throw new ArithmeticException("Insufficient Funds: Account balance is $" + account.getBalance() 
                        + ", but requested withdrawal is $" + amount + ".");
            }
            System.out.println("[STAGE 3 PASSED] Balance sufficient. Remaining after debit: $" + (account.getBalance() - amount));

            System.out.println("[STAGE 4] Validating ATM Hardware Dispenser...");
            dispenser.dispense(amount);
            account.debit(amount);
            System.out.println("[STAGE 4 PASSED] Cash Dispensed Successfully: $" + amount);
            System.out.println("[TRANSACTION COMPLETE] New Account Balance: $" + account.getBalance() 
                    + " | ATM Vault Remaining: $" + dispenser.getVaultCash());

        } catch (NumberFormatException | NullPointerException e) {
            // MULTI-CATCH BLOCK: Handles both formatting and null reference faults
            System.err.println("[MULTI-CATCH ERROR] Invalid input format: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Domain validation faults (non-positive amount, invalid denomination, per-transaction cap)
            System.err.println("[VALIDATION ERROR] Business logic violation: " + e.getMessage());
        } catch (ArithmeticException e) {
            // Balance computation failure
            System.err.println("[BALANCE ERROR] Transaction rejected: " + e.getMessage());
        } catch (SecurityException e) {
            // Security threshold breach (account lockout)
            System.err.println("[SECURITY ALERT] Critical: " + e.getMessage());
        } catch (IllegalStateException e) {
            // Hardware or account state faults
            System.err.println("[STATE ERROR] System state failure: " + e.getMessage());
        } catch (Exception e) {
            // General fallback catch
            System.err.println("[UNEXPECTED ERROR] System fault: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // MANDATORY CLEANUP: Always runs regardless of normal completion or exception
            sessionActive = false;
            cardInserted = false;
            System.out.println("[FINALLY BLOCK] Cleaning up session...");
            System.out.println("  -> User ATM Card safely EJECTED.");
            System.out.println("  -> Network Session closed.");
            System.out.println("  -> Audit log record finalized.");
            System.out.println("--------------------------------------------------------------------------------\n");
        }
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 3 - TASK 01: ATM TRANSACTION EXCEPTION HANDLING SYSTEM             ");
        System.out.println("   Student A: Devi Vara Prasad | Reg: 192365032                                 ");
        System.out.println("   Student B: [Student B Name] | Reg: [Student B Register Number]               ");
        System.out.println("   Environment: Java SE " + System.getProperty("java.version") + " (" + System.getProperty("os.name") + ")");
        System.out.println("================================================================================\n");

        Account acc = new Account("AC-889911", "4321", 2000.0);
        ATMDispenser dispenser = new ATMDispenser(10000.0, 5000.0); // $10,000 vault, $5,000 max limit

        // Test Scenario 1: Normal Valid Transaction
        System.out.println(">>> TEST CASE 1: Valid Withdrawal ($500) <<<");
        processAtmTransaction(acc, dispenser, "4321", "500");

        // Test Scenario 2: Non-numeric Input String
        System.out.println(">>> TEST CASE 2: Non-Numeric Amount String ('500XYZ') <<<");
        processAtmTransaction(acc, dispenser, "4321", "500XYZ");

        // Test Scenario 3: Null Amount String
        System.out.println(">>> TEST CASE 3: Null Amount String <<<");
        processAtmTransaction(acc, dispenser, "4321", null);

        // Test Scenario 4: Invalid Denomination ($250 is not a multiple of $100)
        System.out.println(">>> TEST CASE 4: Invalid Denomination ($250) <<<");
        processAtmTransaction(acc, dispenser, "4321", "250");

        // Test Scenario 5: Insufficient Funds ($3,000 withdrawal against balance of $1,500)
        System.out.println(">>> TEST CASE 5: Insufficient Balance ($3,000 requested, balance $1,500) <<<");
        processAtmTransaction(acc, dispenser, "4321", "3000");

        // Test Scenario 6: Exceeding ATM Hardware Vault ($8,000 withdrawal against vault limit)
        System.out.println(">>> TEST CASE 6: Transaction Exceeding Per-Transaction Cap ($6,000) <<<");
        processAtmTransaction(acc, dispenser, "4321", "6000");

        // Test Scenario 7: Incorrect PIN entries leading to Security Account Lockout
        System.out.println(">>> TEST CASE 7: Consecutive Incorrect PIN Entries & Account Lockout <<<");
        Account lockedCandidate = new Account("AC-990022", "1111", 5000.0);
        processAtmTransaction(lockedCandidate, dispenser, "0000", "500"); // Fail 1
        processAtmTransaction(lockedCandidate, dispenser, "2222", "500"); // Fail 2
        processAtmTransaction(lockedCandidate, dispenser, "3333", "500"); // Fail 3 -> Lockout!
        processAtmTransaction(lockedCandidate, dispenser, "1111", "500"); // Attempt on locked account
    }
}
