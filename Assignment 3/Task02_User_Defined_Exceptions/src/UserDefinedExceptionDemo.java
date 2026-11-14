package task02_exceptions;

/**
 * Task 02: User-Defined Exceptions and Exception Chaining Demonstration.
 * 
 * Demonstrates:
 * 1. Creating custom checked exceptions extending Exception.
 * 2. throw and throws propagation across layers.
 * 3. Detailed diagnostic fields on custom exceptions.
 * 4. Exception Chaining with getCause() inspection and chained stack trace.
 * 
 * Student A: Devi Vara Prasad (Reg: 192365032)
 * Student B: [Student B Name] (Reg: [Student B Register Number])
 */
public class UserDefinedExceptionDemo {

    public static void executeTransfer(FundTransferService service, String src, String dst, double amount, boolean simulateFault) {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("Attempting Transfer: %s -> %s | Amount: $%.2f | FaultSim: %b%n", src, dst, amount, simulateFault);
        try {
            String receipt = service.transferFunds(src, dst, amount, simulateFault);
            System.out.println("[TRANSACTION APPROVED] " + receipt);

        } catch (InsufficientFundsException e) {
            System.err.println("[CUSTOM EXCEPTION CAUGHT] " + e.getErrorCode() + ": " + e.getMessage());
            System.err.printf("  -> Account: %s | Shortfall: $%.2f | Current Balance: $%.2f%n",
                    e.getAccountNumber(), e.getShortfall(), e.getCurrentBalance());

        } catch (AccountBlockedException e) {
            System.err.println("[CUSTOM EXCEPTION CAUGHT] " + e.getErrorCode() + ": " + e.getMessage());
            System.err.printf("  -> Account: %s | Reason: '%s' | Risk Level: %d/100%n",
                    e.getAccountNumber(), e.getReason(), e.getRiskScore());

        } catch (InvalidAccountException e) {
            System.err.println("[CUSTOM EXCEPTION CAUGHT] " + e.getErrorCode() + ": " + e.getMessage());
            System.err.println("  -> Offending Identifier: " + e.getAttemptedAccount());

        } catch (TransactionProcessingException e) {
            System.err.println("[CHAINED EXCEPTION CAUGHT] " + e.getErrorCode() + ": " + e.getMessage());
            System.err.println("  -> Transaction Ref ID : " + e.getTransactionReferenceId());
            System.err.println("  -> Direct Root Cause  : " + e.getCause().getClass().getName() + " - " + e.getCause().getMessage());
            System.err.println("  -> Chained Cause Stack Trace (First 2 frames):");
            StackTraceElement[] frames = e.getCause().getStackTrace();
            for (int i = 0; i < Math.min(2, frames.length); i++) {
                System.err.println("     at " + frames[i]);
            }

        } catch (Exception e) {
            System.err.println("[GENERAL EXCEPTION] " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 3 - TASK 02: USER-DEFINED EXCEPTIONS & EXCEPTION CHAINING         ");
        System.out.println("   Student A: Devi Vara Prasad | Reg: 192365032                                 ");
        System.out.println("   Student B: [Student B Name] | Reg: [Student B Register Number]               ");
        System.out.println("   Environment: Java SE " + System.getProperty("java.version") + " (" + System.getProperty("os.name") + ")");
        System.out.println("================================================================================\n");

        FundTransferService service = new FundTransferService();

        // 1. Valid Transfer
        System.out.println(">>> TEST CASE 1: Normal Valid Transfer ($500.00) <<<");
        executeTransfer(service, "ACC-1001", "ACC-2002", 500.0, false);

        // 2. Invalid Account Format
        System.out.println("\n>>> TEST CASE 2: Invalid Account Format ('BAD-ACCT-9') <<<");
        executeTransfer(service, "BAD-ACCT-9", "ACC-2002", 200.0, false);

        // 3. Non-existent Account
        System.out.println("\n>>> TEST CASE 3: Non-Existent Destination Account ('ACC-9999') <<<");
        executeTransfer(service, "ACC-1001", "ACC-9999", 300.0, false);

        // 4. Blocked / Frozen Account
        System.out.println("\n>>> TEST CASE 4: Transfer From Blocked Account ('ACC-3003') <<<");
        executeTransfer(service, "ACC-3003", "ACC-1001", 1000.0, false);

        // 5. Insufficient Funds
        System.out.println("\n>>> TEST CASE 5: Insufficient Funds ($10,000 from $1,700 balance) <<<");
        executeTransfer(service, "ACC-2002", "ACC-1001", 10000.0, false);

        // 6. Exception Chaining Demonstration
        System.out.println("\n>>> TEST CASE 6: Exception Chaining (Gateway Socket Timeout) <<<");
        executeTransfer(service, "ACC-1001", "ACC-2002", 150.0, true);

        System.out.println("\n================================================================================");
        System.out.println("[TASK 02 VERIFICATION COMPLETE] All custom exceptions and chaining verified.");
        System.out.println("================================================================================");
    }
}
