package parking.model;

public class Car extends Vehicle {

    public Car(String registrationNumber) {
        super(registrationNumber, VehicleType.CAR);
        this.securityTag = "CAR-" + this.getRegistrationNumber();
    }

    protected Car(String registrationNumber, VehicleType type) {
        super(registrationNumber, type);
        this.securityTag = type.name() + "-" + this.getRegistrationNumber();
    }

    @Override
    public double getBaseHourlyRate() {
        return 30.0;
    }
}
