/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 12: Day-4 Design Challenge
 */
public class ElectricCar extends Car {

    public ElectricCar(String registrationNumber) {
        super(registrationNumber, "ElectricCar", new HourlyPricingRule());
    }

    @Override
    public double getBaseHourlyRate() {
        return 50.0; // Base car + standard EV charging bay fee
    }
}
