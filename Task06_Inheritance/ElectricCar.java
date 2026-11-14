/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 06: Inheritance
 * 
 * Demonstrates Multilevel inheritance (Vehicle -> Car -> ElectricCar).
 */
public class ElectricCar extends Car {
    private final double batteryCapacityKWh;
    private final boolean chargingRequired;

    public ElectricCar(String registrationNumber, String brand, int seatingCapacity,
                       double batteryCapacityKWh, boolean chargingRequired) {
        super(registrationNumber, brand, "ElectricCar", seatingCapacity);
        System.out.println("  -> [3. ElectricCar Constructor] Initializing ElectricCar: battery=" 
                + batteryCapacityKWh + "kWh, charging=" + chargingRequired);
        this.batteryCapacityKWh = batteryCapacityKWh;
        this.chargingRequired = chargingRequired;
    }

    @Override
    public double getHourlyRate() {
        // Base car rate (Rs 30) + EV premium / charging rate (Rs 20)
        return super.getHourlyRate() + (chargingRequired ? 20.0 : 10.0);
    }

    public double getBatteryCapacityKWh() { return batteryCapacityKWh; }
    public boolean isChargingRequired() { return chargingRequired; }
}
