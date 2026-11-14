/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 08: Polymorphism
 */
public abstract class Vehicle implements Chargeable {
    protected final String registrationNumber;
    protected final String vehicleType;

    public Vehicle(String registrationNumber, String vehicleType) {
        this.registrationNumber = registrationNumber;
        this.vehicleType = vehicleType;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    // Abstract method for runtime polymorphism (dynamic method dispatch)
    @Override
    public abstract double calculateFee(int hours);

    // Overloaded method demonstrating compile-time polymorphism
    @Override
    public double calculateFee(int hours, double discountPercentage) {
        double baseFee = calculateFee(hours);
        return baseFee - (baseFee * (discountPercentage / 100.0));
    }

    // Additional overloaded method with pass status
    public double calculateFee(int hours, boolean hasStudentPass) {
        return calculateFee(hours, hasStudentPass ? 50.0 : 0.0);
    }
}
