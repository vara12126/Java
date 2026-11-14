/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 07: Method Overriding
 * 
 * Demonstrates:
 * 1. Overriding calculateFee() with super delegation.
 * 2. Final class (cannot be further subclassed).
 */
public final class ElectricCar extends Car {
    private final boolean chargingRequired;

    public ElectricCar(String registrationNumber, boolean chargingRequired) {
        super(registrationNumber, "ElectricCar");
        this.chargingRequired = chargingRequired;
    }

    @Override
    public double calculateFee(int hours) {
        if (hours <= 0) return 0.0;
        // Base car fee (Rs 30/hr) + EV charging fee if utilized (Rs 20/hr)
        double baseFee = super.calculateFee(hours);
        double evChargingFee = chargingRequired ? (hours * 20.0) : (hours * 5.0);
        return baseFee + evChargingFee;
    }

    public boolean isChargingRequired() {
        return chargingRequired;
    }
}
