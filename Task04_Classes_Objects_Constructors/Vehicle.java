/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 04: Classes, Objects and Constructors
 */
public class Vehicle {
    private String registrationNumber;
    private String vehicleType;
    private String ownerName;

    // 1. Default (No-Argument) Constructor: Chains to parameterized constructor using `this(...)`
    public Vehicle() {
        this("UNKNOWN", "Standard", "Guest");
    }

    // 2. Overloaded Constructor: 2 parameters (defaults owner to "Guest")
    public Vehicle(String registrationNumber, String vehicleType) {
        this(registrationNumber, vehicleType, "Guest"); // Constructor chaining
    }

    // 3. Fully Parameterized Constructor
    public Vehicle(String registrationNumber, String vehicleType, String ownerName) {
        // Using `this` to resolve variable shadowing
        this.registrationNumber = registrationNumber != null ? registrationNumber.toUpperCase() : "UNKNOWN";
        this.vehicleType = vehicleType != null ? vehicleType : "Standard";
        this.ownerName = ownerName != null ? ownerName : "Guest";
    }

    // 4. Copy Constructor (Creates a distinct new object with cloned state)
    public Vehicle(Vehicle other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot copy from null vehicle");
        }
        this.registrationNumber = other.registrationNumber;
        this.vehicleType = other.vehicleType;
        this.ownerName = other.ownerName;
    }

    // Static Factory Method pattern
    public static Vehicle createElectricCar(String regNumber, String owner) {
        return new Vehicle(regNumber, "ElectricCar", owner);
    }

    // Getters and Setters
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    @Override
    public String toString() {
        return String.format("Vehicle[Reg=%s, Type=%s, Owner=%s, ObjectRef=@%s]",
                registrationNumber, vehicleType, ownerName, Integer.toHexString(System.identityHashCode(this)));
    }
}
