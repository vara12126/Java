/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 12: Day-4 Design Challenge
 */
public class TwoWheeler extends Vehicle {

    public TwoWheeler(String registrationNumber) {
        super(registrationNumber, "TwoWheeler", new HourlyPricingRule());
    }

    @Override
    public double getBaseHourlyRate() {
        return 10.0;
    }
}
