/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 11: Complete Smart-Parking Implementation
 */
import java.util.Objects;
import java.util.regex.Pattern;

public abstract class Vehicle {
    private static final Pattern REG_PATTERN = Pattern.compile("^[A-Z0-9]{4,12}$");

    protected final String registrationNumber;
    protected final String brand;
    protected final String vehicleType;

    public Vehicle(String registrationNumber, String brand, String vehicleType) {
        if (registrationNumber == null) {
            throw new IllegalArgumentException("Registration number cannot be null.");
        }
        String cleanReg = registrationNumber.trim().toUpperCase().replaceAll("\\s+", "");
        if (!REG_PATTERN.matcher(cleanReg).matches()) {
            throw new IllegalArgumentException("Invalid registration [" + registrationNumber + 
                    "]. Must be 4-12 alphanumeric characters without symbols.");
        }
        this.registrationNumber = cleanReg;
        this.brand = brand != null ? brand.trim() : "Generic";
        this.vehicleType = vehicleType;
    }

    public String getRegistrationNumber() { return registrationNumber; }
    public String getBrand() { return brand; }
    public String getVehicleType() { return vehicleType; }

    public abstract double getHourlyRate();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle)) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(registrationNumber, vehicle.registrationNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrationNumber);
    }

    @Override
    public String toString() {
        return String.format("%s [%s, %s]", vehicleType, registrationNumber, brand);
    }
}
