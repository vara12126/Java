package parking.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Base Vehicle demonstrating:
 * 1. private fields (strict encapsulation).
 * 2. protected fields (accessible by subclasses).
 * 3. package-private fields (accessible only inside parking.model).
 * 4. Input validation inside setters.
 */
public abstract class Vehicle {

    // 1. Private: Accessible ONLY within this class
    private String registrationNumber;
    private VehicleType vehicleType;

    // 2. Protected: Accessible by subclasses and classes within parking.model
    protected String securityTag;

    // 3. Package-Private (Default): Accessible ONLY within parking.model package
    String internalAuditCode;

    // Validation pattern: 5-10 alphanumeric characters (e.g. TN01AB1234)
    private static final Pattern REG_PATTERN = Pattern.compile("^[A-Z0-9]{4,12}$");

    public Vehicle(String registrationNumber, VehicleType vehicleType) {
        setRegistrationNumber(registrationNumber);
        this.vehicleType = Objects.requireNonNull(vehicleType, "VehicleType cannot be null");
        this.securityTag = "TAG-" + this.registrationNumber;
        this.internalAuditCode = "AUDIT-" + System.nanoTime();
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public final void setRegistrationNumber(String registrationNumber) {
        if (registrationNumber == null) {
            throw new IllegalArgumentException("Validation Error: Registration number cannot be null.");
        }
        String cleanReg = registrationNumber.trim().toUpperCase().replaceAll("\\s+", "");
        if (!REG_PATTERN.matcher(cleanReg).matches()) {
            throw new IllegalArgumentException("Validation Error: Invalid registration format [" + registrationNumber + 
                    "]. Must be 4-12 alphanumeric characters.");
        }
        this.registrationNumber = cleanReg;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = Objects.requireNonNull(vehicleType, "VehicleType cannot be null");
    }

    public String getSecurityTag() {
        return securityTag;
    }

    public abstract double getBaseHourlyRate();

    @Override
    public String toString() {
        return String.format("[%s: %s | Tag: %s]", vehicleType, registrationNumber, securityTag);
    }
}
