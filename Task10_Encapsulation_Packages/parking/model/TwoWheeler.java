package parking.model;

public class TwoWheeler extends Vehicle {

    public TwoWheeler(String registrationNumber) {
        super(registrationNumber, VehicleType.TWO_WHEELER);
        // Can access protected securityTag from superclass
        this.securityTag = "BIKE-" + this.getRegistrationNumber();
    }

    @Override
    public double getBaseHourlyRate() {
        return 10.0;
    }
}
