/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 09: Abstraction
 * 
 * Abstract class implementing multiple interfaces (Chargeable, Trackable).
 * Provides common state, concrete methods, and abstract hooks.
 */
public abstract class Vehicle implements Chargeable, Trackable {
    protected final String registrationNumber;
    protected final String vehicleType;

    public Vehicle(String registrationNumber, String vehicleType) {
        this.registrationNumber = registrationNumber;
        this.vehicleType = vehicleType;
    }

    // Concrete getter methods
    public String getRegistrationNumber() { return registrationNumber; }
    public String getVehicleType() { return vehicleType; }

    // Abstract method: Every specific vehicle class MUST define its designated parking zone
    public abstract String getDesignatedParkingZone();

    // Implementation of Trackable
    @Override
    public String getTrackingTag() {
        return "RFID-" + vehicleType.toUpperCase() + "-" + registrationNumber;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s | Zone: %s]", vehicleType, registrationNumber, getDesignatedParkingZone());
    }
}
