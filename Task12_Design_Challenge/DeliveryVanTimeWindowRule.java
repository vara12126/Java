/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 12: Day-4 Design Challenge
 * 
 * Delivery Van Pricing Rule:
 * - Double the car rate (2 * Rs 30 = Rs 60/hr) between 9:00 AM and 5:00 PM.
 * - Standard car rate (Rs 30/hr) outside this time window.
 * - Correctly splits the duration: does NOT apply double rate to the entire stay.
 */
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DeliveryVanTimeWindowRule implements PricingRule {

    private static final LocalTime PEAK_START = LocalTime.of(9, 0);  // 9:00 AM
    private static final LocalTime PEAK_END   = LocalTime.of(17, 0); // 5:00 PM

    private final double baseRatePerHour;   // Standard Car rate (Rs 30/hr)
    private final double peakMultiplier;    // 2.0x

    public DeliveryVanTimeWindowRule() {
        this(30.0, 2.0);
    }

    public DeliveryVanTimeWindowRule(double baseRatePerHour, double peakMultiplier) {
        this.baseRatePerHour = baseRatePerHour;
        this.peakMultiplier = peakMultiplier;
    }

    @Override
    public BigDecimal computeFee(Vehicle vehicle, LocalDateTime entryTime, LocalDateTime exitTime) {
        if (entryTime == null || exitTime == null || exitTime.isBefore(entryTime)) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        long totalMinutes = Duration.between(entryTime, exitTime).toMinutes();
        if (totalMinutes == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        long peakMinutes = 0;
        long offPeakMinutes = 0;

        // Iterate minute-by-minute across the parking duration
        LocalDateTime cursor = entryTime;
        for (long m = 0; m < totalMinutes; m++) {
            LocalTime time = cursor.toLocalTime();
            // A minute is inside peak window if time >= 09:00 and time < 17:00
            if (!time.isBefore(PEAK_START) && time.isBefore(PEAK_END)) {
                peakMinutes++;
            } else {
                offPeakMinutes++;
            }
            cursor = cursor.plusMinutes(1);
        }

        // Billable hours using ceiling policy on each component
        long peakHours = (long) Math.ceil((double) peakMinutes / 60.0);
        long offPeakHours = (long) Math.ceil((double) offPeakMinutes / 60.0);

        double peakRate = baseRatePerHour * peakMultiplier; // Rs 60/hr
        double offPeakRate = baseRatePerHour;               // Rs 30/hr

        BigDecimal peakCost = BigDecimal.valueOf(peakHours)
                .multiply(BigDecimal.valueOf(peakRate))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal offPeakCost = BigDecimal.valueOf(offPeakHours)
                .multiply(BigDecimal.valueOf(offPeakRate))
                .setScale(2, RoundingMode.HALF_UP);

        return peakCost.add(offPeakCost).setScale(2, RoundingMode.HALF_UP);
    }

    public LocalTime getPeakStart() { return PEAK_START; }
    public LocalTime getPeakEnd() { return PEAK_END; }
    public double getBaseRatePerHour() { return baseRatePerHour; }
}
