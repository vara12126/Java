/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 12: Day-4 Design Challenge
 */
import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class Vehicle {
    protected final String registrationNumber;
    protected final String vehicleType;
    protected final PricingRule pricingRule;

    public Vehicle(String registrationNumber, String vehicleType, PricingRule pricingRule) {
        this.registrationNumber = registrationNumber.trim().toUpperCase();
        this.vehicleType = vehicleType;
        this.pricingRule = pricingRule;
    }

    public String getRegistrationNumber() { return registrationNumber; }
    public String getVehicleType() { return vehicleType; }
    public PricingRule getPricingRule() { return pricingRule; }

    public BigDecimal computeFee(LocalDateTime entryTime, LocalDateTime exitTime) {
        return pricingRule.computeFee(this, entryTime, exitTime);
    }

    public abstract double getBaseHourlyRate();

    @Override
    public String toString() {
        return String.format("[%s: %s | Rule: %s]",
                vehicleType, registrationNumber, pricingRule.getClass().getSimpleName());
    }
}
