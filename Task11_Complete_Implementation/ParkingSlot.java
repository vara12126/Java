/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 11: Complete Smart-Parking Implementation
 */
public class ParkingSlot {
    private final int floor;
    private final int slotNumber;
    private final boolean evChargingCapable;
    private boolean occupied;
    private Vehicle parkedVehicle;

    public ParkingSlot(int floor, int slotNumber, boolean evChargingCapable) {
        this.floor = floor;
        this.slotNumber = slotNumber;
        this.evChargingCapable = evChargingCapable;
        this.occupied = false;
        this.parkedVehicle = null;
    }

    public int getFloor() { return floor; }
    public int getSlotNumber() { return slotNumber; }
    public boolean isEvChargingCapable() { return evChargingCapable; }
    public boolean isOccupied() { return occupied; }
    public Vehicle getParkedVehicle() { return parkedVehicle; }

    public void assignVehicle(Vehicle vehicle) {
        this.parkedVehicle = vehicle;
        this.occupied = true;
    }

    public Vehicle vacate() {
        Vehicle v = this.parkedVehicle;
        this.parkedVehicle = null;
        this.occupied = false;
        return v;
    }

    @Override
    public String toString() {
        if (!occupied) {
            return String.format("[F%d:S%d (Free%s)]", floor, slotNumber, evChargingCapable ? "-EV" : "");
        }
        return String.format("[F%d:S%d (%s)]", floor, slotNumber, parkedVehicle.getRegistrationNumber());
    }
}
