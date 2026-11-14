package task01_fee;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) encapsulating student fee payment information.
 * 
 * Student: Devi Vara Prasad | Reg: 192365032
 */
public class FeePaymentDTO {

    private final String registerNumber;
    private final double amount;
    private final String transactionReference;
    private final LocalDateTime timestamp;
    private final boolean successful;
    private final String message;

    public FeePaymentDTO(String registerNumber, double amount, String transactionReference,
                         LocalDateTime timestamp, boolean successful, String message) {
        this.registerNumber = registerNumber;
        this.amount = amount;
        this.transactionReference = transactionReference;
        this.timestamp = timestamp;
        this.successful = successful;
        this.message = message;
    }

    public String getRegisterNumber() { return registerNumber; }
    public double getAmount() { return amount; }
    public String getTransactionReference() { return transactionReference; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean isSuccessful() { return successful; }
    public String getMessage() { return message; }
}
