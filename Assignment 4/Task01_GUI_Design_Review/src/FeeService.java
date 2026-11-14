package task01_fee;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service Layer responsible for business logic, validation, and database operations.
 * Decoupled from the GUI layer to ensure testability and maintainability.
 * 
 * Student: Devi Vara Prasad | Reg: 192365032
 */
public class FeeService {

    // Simulating transactional database persistence table
    private static final Map<String, List<FeePaymentDTO>> feeLedger = new ConcurrentHashMap<>();

    /**
     * Validates input fields prior to database processing.
     */
    public void validateInput(String regNo, String amountStr) throws IllegalArgumentException {
        if (regNo == null || regNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Register Number cannot be empty.");
        }
        if (!regNo.trim().matches("^[a-zA-Z0-9]{5,15}$")) {
            throw new IllegalArgumentException("Invalid Register Number format (5-15 alphanumeric characters).");
        }
        if (amountStr == null || amountStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Fee Amount cannot be empty.");
        }

        try {
            double parsedAmount = Double.parseDouble(amountStr.trim());
            if (parsedAmount <= 0) {
                throw new IllegalArgumentException("Fee Amount must be strictly positive.");
            }
            if (parsedAmount > 500000.0) {
                throw new IllegalArgumentException("Fee Amount exceeds maximum single-transaction limit of ₹5,00,000.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid fee amount format. Please enter a valid decimal number.");
        }
    }

    /**
     * Processes payment asynchronously.
     * Uses PreparedStatement emulation to prevent SQL Injection and guarantees resource cleanup.
     */
    public FeePaymentDTO processPayment(String regNo, double amount) throws Exception {
        // Simulating network latency / bank confirmation off the EDT
        Thread.sleep(800);

        String txRef = "TXN-" + System.currentTimeMillis() + "-" + (new Random().nextInt(900) + 100);
        LocalDateTime now = LocalDateTime.now();

        // Parameterized SQL Statement logic:
        // String sql = "INSERT INTO fees (reg_no, amount, tx_ref, created_at) VALUES (?, ?, ?, ?)";
        // In real JDBC:
        // try (PreparedStatement ps = conn.prepareStatement(sql)) {
        //     ps.setString(1, regNo);
        //     ps.setDouble(2, amount);
        //     ps.setString(3, txRef);
        //     ps.setTimestamp(4, Timestamp.valueOf(now));
        //     ps.executeUpdate();
        // }

        FeePaymentDTO record = new FeePaymentDTO(regNo.trim(), amount, txRef, now, true, "Payment Successful");
        feeLedger.computeIfAbsent(regNo.trim(), k -> new ArrayList<>()).add(record);

        return record;
    }

    /**
     * Retrieves the latest receipt for a student.
     */
    public FeePaymentDTO getLatestReceipt(String regNo) {
        if (regNo == null || regNo.trim().isEmpty()) return null;
        List<FeePaymentDTO> list = feeLedger.get(regNo.trim());
        if (list == null || list.isEmpty()) return null;
        return list.get(list.size() - 1);
    }
}
