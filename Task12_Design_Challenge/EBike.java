/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 12: Day-4 Design Challenge
 * 
 * E-Bike entity adhering to EBikeFlatPricingRule.
 */
public class EBike extends Vehicle {

    public EBike(String registrationNumber) {
        super(registrationNumber, "EBike", new EBikeFlatPricingRule());
    }

    public EBike(String registrationNumber, EBikeFlatPricingRule customFlatRule) {
        super(registrationNumber, "EBike", customFlatRule);
    }

    @Override
    public double getBaseHourlyRate() {
        return 0.0; // Flat pricing rule governs calculation
    }
}
