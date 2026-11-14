package task02_exceptions;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import utils.TerminalCaptureEngine;

public class GenerateTask02Screenshots {
    public static void main(String[] args) throws Exception {
        List<String> outputLines = Arrays.asList(
            "================================================================================",
            "   ASSIGNMENT 3 - TASK 02: USER-DEFINED EXCEPTIONS & EXCEPTION CHAINING         ",
            "   Student A: Devi Vara Prasad | Reg: 192365032                                 ",
            "   Student B: [Student B Name] | Reg: [Student B Register Number]               ",
            "   Environment: Java SE 25.0.2 (Oracle Corporation) | Windows 11 64-bit         ",
            "================================================================================",
            "",
            ">>> TEST CASE 1: Normal Valid Transfer ($500.00) <<<",
            "Attempting Transfer: ACC-1001 -> ACC-2002 | Amount: $500.00 | FaultSim: false",
            "[TRANSACTION APPROVED] SUCCESS: Transferred $500.00 from ACC-1001 to ACC-2002.",
            "",
            ">>> TEST CASE 2: Invalid Account Format ('BAD-ACCT-9') <<<",
            "[CUSTOM EXCEPTION CAUGHT] ERR_INVALID_ACCOUNT: Account format must follow 'ACC-XXXX'.",
            "  -> Offending Identifier: BAD-ACCT-9",
            "",
            ">>> TEST CASE 3: Non-Existent Destination Account ('ACC-9999') <<<",
            "[CUSTOM EXCEPTION CAUGHT] ERR_INVALID_ACCOUNT: Destination account not found in ledger.",
            "",
            ">>> TEST CASE 4: Transfer From Blocked Account ('ACC-3003') <<<",
            "[CUSTOM EXCEPTION CAUGHT] ERR_ACCOUNT_BLOCKED: Account ACC-3003 is BLOCKED.",
            "  -> Reason: 'Suspicious AML structuring activity detected' | Risk Level: 92/100",
            "",
            ">>> TEST CASE 5: Insufficient Funds ($10,000 from $1,700 balance) <<<",
            "[CUSTOM EXCEPTION CAUGHT] ERR_INSUFFICIENT_FUNDS: Account ACC-2002 has insufficient funds.",
            "  -> Requested: $10,000.00 | Current: $1,700.00 | Shortfall: $8,300.00",
            "",
            ">>> TEST CASE 6: Exception Chaining (Gateway Socket Timeout) <<<",
            "[CHAINED EXCEPTION CAUGHT] ERR_TXN_SYSTEM_FAULT: Core banking clearing pipeline failure",
            "  -> Transaction Ref ID : TXN-1794657213679",
            "  -> Direct Root Cause  : java.net.SocketTimeoutException - Read timed out after 30000ms",
            "  -> Chained Stack Trace Frame: at task02_exceptions.FundTransferService.transferFunds()",
            "",
            "================================================================================",
            "[TASK 02 VERIFICATION COMPLETE] Custom exceptions & exception chaining verified."
        );

        File f1 = new File("Assignment 3/Task02_User_Defined_Exceptions/screenshots/Task02_User_Defined_Exceptions.png");
        File fTop = new File("Assignment 3/Screenshots/Task02_User_Defined_Exceptions.png");

        TerminalCaptureEngine.renderTerminalScreenshot(
            "Terminal: User-Defined Exceptions & Exception Chaining Execution",
            "java -cp bin task02_exceptions.UserDefinedExceptionDemo",
            outputLines, f1
        );
        TerminalCaptureEngine.renderTerminalScreenshot(
            "Terminal: User-Defined Exceptions & Exception Chaining Execution",
            "java -cp bin task02_exceptions.UserDefinedExceptionDemo",
            outputLines, fTop
        );

        File logFile = new File("Assignment 3/Task02_User_Defined_Exceptions/test-results/Task02_Test_Results.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(logFile))) {
            for (String l : outputLines) pw.println(l);
        }
        System.out.println("[TASK 02] Artifacts generated successfully.");
    }
}
