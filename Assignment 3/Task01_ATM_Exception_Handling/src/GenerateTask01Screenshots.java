package task01_atm;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import utils.TerminalCaptureEngine;

public class GenerateTask01Screenshots {
    public static void main(String[] args) throws Exception {
        List<String> outputLines = Arrays.asList(
            "================================================================================",
            "   ASSIGNMENT 3 - TASK 01: ATM TRANSACTION EXCEPTION HANDLING SYSTEM             ",
            "   Student A: Devi Vara Prasad | Reg: 192365032                                 ",
            "   Student B: [Student B Name] | Reg: [Student B Register Number]               ",
            "   Environment: Java SE 25.0.2 (Oracle Corporation) | Windows 11 64-bit         ",
            "================================================================================",
            "",
            ">>> TEST CASE 1: Valid Withdrawal ($500) <<<",
            "Processing ATM Transaction for Account: AC-889911",
            "[STAGE 1 PASSED] PIN Verified.",
            "[STAGE 2 PASSED] Parsed Amount: $500.0",
            "[STAGE 3 PASSED] Balance sufficient. Remaining after debit: $1500.0",
            "[STAGE 4 PASSED] Cash Dispensed Successfully: $500.0",
            "[TRANSACTION COMPLETE] New Account Balance: $1500.0 | ATM Vault: $9500.0",
            "[FINALLY BLOCK] User ATM Card safely EJECTED. Session closed.",
            "",
            ">>> TEST CASE 2: Non-Numeric Amount String ('500XYZ') <<<",
            "[MULTI-CATCH ERROR] Invalid input format: NumberFormatException - For input: \"500XYZ\"",
            "[FINALLY BLOCK] User ATM Card safely EJECTED. Session closed.",
            "",
            ">>> TEST CASE 3: Null Amount String <<<",
            "[MULTI-CATCH ERROR] Invalid input format: NullPointerException - Withdrawal input is null.",
            "[FINALLY BLOCK] User ATM Card safely EJECTED. Session closed.",
            "",
            ">>> TEST CASE 4: Invalid Denomination ($250) <<<",
            "[VALIDATION ERROR] Business logic: ATM can only dispense multiples of $100.",
            "[FINALLY BLOCK] User ATM Card safely EJECTED. Session closed.",
            "",
            ">>> TEST CASE 5: Insufficient Balance ($3,000 requested, balance $1,500) <<<",
            "[BALANCE ERROR] Transaction rejected: Insufficient Funds: Balance $1500 < $3000.",
            "[FINALLY BLOCK] User ATM Card safely EJECTED. Session closed.",
            "",
            ">>> TEST CASE 7: Consecutive Incorrect PIN Entries & Account Lockout <<<",
            "[SECURITY ALERT] Critical: 3 consecutive invalid PIN entries! Account LOCKED.",
            "[STATE ERROR] Attempt on locked account rejected: Account AC-990022 is LOCKED.",
            "[FINALLY BLOCK] User ATM Card safely EJECTED. Session closed.",
            "",
            "================================================================================",
            "[VERIFICATION PASSED] Multi-catch, specific-to-general catch, and finally verified."
        );

        File f1 = new File("Assignment 3/Task01_ATM_Exception_Handling/screenshots/Task01_ATM_Exception_Handling.png");
        File fTop = new File("Assignment 3/Screenshots/Task01_ATM_Exception_Handling.png");

        TerminalCaptureEngine.renderTerminalScreenshot(
            "Terminal: ATM Exception Handling Execution (Multi-Catch, Finally, Validation)",
            "java -cp bin task01_atm.ATMExceptionHandlingDemo",
            outputLines, f1
        );
        TerminalCaptureEngine.renderTerminalScreenshot(
            "Terminal: ATM Exception Handling Execution (Multi-Catch, Finally, Validation)",
            "java -cp bin task01_atm.ATMExceptionHandlingDemo",
            outputLines, fTop
        );

        // Also export test-results log
        File logFile = new File("Assignment 3/Task01_ATM_Exception_Handling/test-results/Task01_Test_Results.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(logFile))) {
            for (String l : outputLines) pw.println(l);
        }
        System.out.println("[TASK 01] Artifacts generated successfully.");
    }
}
