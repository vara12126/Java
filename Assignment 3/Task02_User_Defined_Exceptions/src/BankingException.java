package task02_exceptions;

import java.time.LocalDateTime;

/**
 * Base custom checked exception for all banking domain errors.
 * 
 * Student A: Devi Vara Prasad (Reg: 192365032)
 * Student B: [Student B Name] (Reg: [Student B Register Number])
 */
public abstract class BankingException extends Exception {
    private final String errorCode;
    private final LocalDateTime timestamp;

    public BankingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }

    public BankingException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }

    public String getErrorCode() { return errorCode; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("[%s @ %s] %s: %s", errorCode, timestamp, getClass().getSimpleName(), getMessage());
    }
}
