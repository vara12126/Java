/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 06: Inheritance
 * 
 * Abstract root of the vehicle inheritance hierarchy.
 */
public abstract class Vehicle {
    protected final String registrationNumber;
    protected final String brand;
    protected final String vehicleType;

    public Vehicle(String registrationNumber, String brand, String vehicleType) {
        System.out.println("  -> [1. Vehicle Constructor] Initializing root Vehicle: " + registrationNumber);
        this.registrationNumber = registrationNumber;
        this.brand = brand;
        this.vehicleType = vehicleType;
    }

    public abstract double getHourlyRate();

    public String getRegistrationNumber() { return registrationNumber; }
    public String getBrand() { return brand; }
    public String getVehicleType() { return vehicleType; }

    public void displayInfo() {
        System.out.printf("[%s] Reg: %s | Brand: %s | Base Rate: Rs %.1f/hr%n",
                vehicleType, registrationNumber, brand, getHourlyRate());
    }
}
