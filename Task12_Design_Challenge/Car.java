/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 12: Day-4 Design Challenge
 */
public class Car extends Vehicle {

    public Car(String registrationNumber) {
        super(registrationNumber, "Car", new HourlyPricingRule());
    }

    protected Car(String registrationNumber, String customType, PricingRule pricingRule) {
        super(registrationNumber, customType, pricingRule);
    }

    @Override
    public double getBaseHourlyRate() {
        return 30.0;
    }
}
