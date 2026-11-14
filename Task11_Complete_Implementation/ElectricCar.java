/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 11: Complete Smart-Parking Implementation
 */
public class ElectricCar extends Car {
    private final boolean chargingRequired;
    private static final double CHARGING_RATE_PER_HOUR = 25.0;

    public ElectricCar(String registrationNumber, String brand, boolean chargingRequired) {
        super(registrationNumber, brand, "ElectricCar");
        this.chargingRequired = chargingRequired;
    }

    public boolean isChargingRequired() {
        return chargingRequired;
    }

    public double getChargingRatePerHour() {
        return chargingRequired ? CHARGING_RATE_PER_HOUR : 0.0;
    }

    @Override
    public double getHourlyRate() {
        return super.getHourlyRate(); // Base car rate is Rs 30
    }
}
