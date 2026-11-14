/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 12: Day-4 Design Challenge
 * 
 * Supports Student (50% concession) and Staff (100% concession) monthly passes.
 */
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class MonthlyPass implements Pass {
    public enum HolderRole {
        STUDENT(50.0),
        STAFF(100.0);

        private final double discountRate;
        HolderRole(double discountRate) { this.discountRate = discountRate; }
        public double getDiscountRate() { return discountRate; }
    }

    private final String passId;
    private final String holderName;
    private final HolderRole role;
    private final LocalDateTime validFrom;
    private final LocalDateTime validUntil;

    public MonthlyPass(String passId, String holderName, HolderRole role, LocalDateTime validFrom, LocalDateTime validUntil) {
        this.passId = passId;
        this.holderName = holderName;
        this.role = role;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }

    @Override
    public String getPassId() { return passId; }

    @Override
    public String getPassType() { return "MonthlyPass (" + role.name() + ")"; }

    @Override
    public boolean isValid(LocalDateTime timestamp) {
        if (timestamp == null) return false;
        return !timestamp.isBefore(validFrom) && !timestamp.isAfter(validUntil);
    }

    @Override
    public BigDecimal applyDiscount(BigDecimal grossFee) {
        if (grossFee == null || grossFee.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal discountFactor = BigDecimal.valueOf(role.getDiscountRate())
                .divide(BigDecimal.valueOf(100.0), 4, RoundingMode.HALF_UP);
        BigDecimal discount = grossFee.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
        return grossFee.subtract(discount).setScale(2, RoundingMode.HALF_UP);
    }

    public HolderRole getRole() { return role; }
    public String getHolderName() { return holderName; }
}
