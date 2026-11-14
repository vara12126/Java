/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 12: Day-4 Design Challenge
 * 
 * Standard Hourly Pricing Rule with ceiling slab.
 */
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

public class HourlyPricingRule implements PricingRule {

    @Override
    public BigDecimal computeFee(Vehicle vehicle, LocalDateTime entryTime, LocalDateTime exitTime) {
        if (entryTime == null || exitTime == null || exitTime.isBefore(entryTime)) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        long minutes = Duration.between(entryTime, exitTime).toMinutes();
        if (minutes == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        long billableHours = (long) Math.ceil((double) minutes / 60.0);
        BigDecimal rate = BigDecimal.valueOf(vehicle.getBaseHourlyRate());
        return rate.multiply(BigDecimal.valueOf(billableHours)).setScale(2, RoundingMode.HALF_UP);
    }
}
