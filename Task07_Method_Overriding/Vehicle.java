/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 07: Method Overriding
 */
import java.util.Objects;

public abstract class Vehicle {
    // 1. Final variable: Immutable after initialization
    protected final String registrationNumber;
    protected final String vehicleType;

    public Vehicle(String registrationNumber, String vehicleType) {
        this.registrationNumber = registrationNumber != null ? registrationNumber.trim().toUpperCase() : "";
        this.vehicleType = vehicleType;
    }

    // 2. Final method: Subclasses are forbidden from altering the identity getter
    public final String getRegistrationNumber() {
        return registrationNumber;
    }

    public final String getVehicleType() {
        return vehicleType;
    }

    // Abstract method to be overridden polymorphically by each vehicle subclass
    public abstract double calculateFee(int hours);

    // Overriding Object.toString()
    @Override
    public String toString() {
        return String.format("[%s] Registration: %s", vehicleType, registrationNumber);
    }

    // Overriding Object.equals(): Two vehicles are logically equal if their registration numbers match
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Reference identity check
        if (o == null || !(o instanceof Vehicle)) return false; // Type check
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(this.registrationNumber, vehicle.registrationNumber);
    }

    // Overriding Object.hashCode(): Must maintain the contract: if v1.equals(v2), v1.hashCode() == v2.hashCode()
    @Override
    public int hashCode() {
        return Objects.hash(registrationNumber);
    }
}
