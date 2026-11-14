package parking.model;

public class ElectricCar extends Car {
    private boolean chargingRequested;

    public ElectricCar(String registrationNumber, boolean chargingRequested) {
        super(registrationNumber, VehicleType.ELECTRIC_CAR);
        this.chargingRequested = chargingRequested;
        this.securityTag = "EV-" + this.getRegistrationNumber();
    }

    public boolean isChargingRequested() {
        return chargingRequested;
    }

    public void setChargingRequested(boolean chargingRequested) {
        this.chargingRequested = chargingRequested;
    }

    @Override
    public double getBaseHourlyRate() {
        return chargingRequested ? 50.0 : 35.0;
    }
}
