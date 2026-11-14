/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 12: Day-4 Design Challenge
 * 
 * Visitor One-Day Pass valid for 24 hours from issuance.
 * Provides 100% hourly concession (covered by upfront day pass purchase).
 */
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class OneDayPass implements Pass {
    private final String passId;
    private final String visitorName;
    private final LocalDateTime issuedAt;
    private final LocalDateTime expiresAt;

    public OneDayPass(String passId, String visitorName, LocalDateTime issuedAt) {
        this.passId = passId;
        this.visitorName = visitorName;
        this.issuedAt = issuedAt != null ? issuedAt : LocalDateTime.now();
        this.expiresAt = this.issuedAt.plusHours(24);
    }

    @Override
    public String getPassId() { return passId; }

    @Override
    public String getPassType() { return "Visitor One-Day Pass"; }

    @Override
    public boolean isValid(LocalDateTime timestamp) {
        if (timestamp == null) return false;
        return !timestamp.isBefore(issuedAt) && !timestamp.isAfter(expiresAt);
    }

    @Override
    public BigDecimal applyDiscount(BigDecimal grossFee) {
        // One-day pass waives hourly parking fee completely during its 24-hr validity
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public String getVisitorName() { return visitorName; }
}
