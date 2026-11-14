/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 12: Day-4 Design Challenge
 * 
 * Strategy Pattern Interface for Dynamic Parking Fee Rules.
 */
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PricingRule {
    BigDecimal computeFee(Vehicle vehicle, LocalDateTime entryTime, LocalDateTime exitTime);
}
