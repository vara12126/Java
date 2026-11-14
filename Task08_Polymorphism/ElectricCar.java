/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 08: Polymorphism
 */
public class ElectricCar extends Car {
    private final double chargingFeePerHour = 40.0;

    public ElectricCar(String registrationNumber) {
        super(registrationNumber, "ElectricCar");
    }

    @Override
    public double calculateFee(int hours) {
        // Base car fee (hours * 30) + EV charging fee (hours * 40) = hours * 70
        // For hours = 3 -> 3 * 70 = 210.0
        return super.calculateFee(hours) + (hours * chargingFeePerHour);
    }

    public double getChargingFeePerHour() {
        return chargingFeePerHour;
    }
}
