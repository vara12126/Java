/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 12: Day-4 Design Challenge
 * 
 * E-Bike Flat Pricing Rule:
 * Charges a single flat fee (e.g. Rs 20.00) regardless of the duration parked.
 */
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class EBikeFlatPricingRule implements PricingRule {

    private final BigDecimal flatFee;

    public EBikeFlatPricingRule() {
        this(new BigDecimal("20.00"));
    }

    public EBikeFlatPricingRule(BigDecimal flatFee) {
        this.flatFee = flatFee.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal computeFee(Vehicle vehicle, LocalDateTime entryTime, LocalDateTime exitTime) {
        if (entryTime == null || exitTime == null || exitTime.isBefore(entryTime)) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return flatFee;
    }

    public BigDecimal getFlatFee() {
        return flatFee;
    }
}
