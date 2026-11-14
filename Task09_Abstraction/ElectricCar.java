/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 09: Abstraction
 */
public class ElectricCar extends Car {
    private final boolean fastChargingActive;

    public ElectricCar(String registrationNumber, boolean fastChargingActive) {
        super(registrationNumber, "ElectricCar");
        this.fastChargingActive = fastChargingActive;
    }

    @Override
    public double calculateFee(int hours) {
        double baseFee = super.calculateFee(hours);
        double evCharging = fastChargingActive ? (hours * 35.0) : (hours * 15.0);
        return baseFee + evCharging;
    }

    @Override
    public String getDesignatedParkingZone() {
        return "Zone E (Green Bay EV Charging Stations)";
    }
}
