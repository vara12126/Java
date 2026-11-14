package parking.model;

/**
 * Encapsulated model for a single physical parking slot.
 */
public class ParkingSlot {
    private final int floor;
    private final int slotNumber;
    private boolean occupied;
    private Vehicle currentVehicle;

    public ParkingSlot(int floor, int slotNumber) {
        if (floor < 0) {
            throw new IllegalArgumentException("Floor number cannot be negative: " + floor);
        }
        if (slotNumber < 0) {
            throw new IllegalArgumentException("Slot number cannot be negative: " + slotNumber);
        }
        this.floor = floor;
        this.slotNumber = slotNumber;
        this.occupied = false;
        this.currentVehicle = null;
    }

    public int getFloor() { return floor; }
    public int getSlotNumber() { return slotNumber; }
    public boolean isOccupied() { return occupied; }
    public Vehicle getCurrentVehicle() { return currentVehicle; }

    public void assignVehicle(Vehicle vehicle) {
        if (this.occupied) {
            throw new IllegalStateException("Slot [" + floor + ", " + slotNumber + "] is already occupied!");
        }
        this.currentVehicle = vehicle;
        this.occupied = true;
    }

    public Vehicle vacate() {
        if (!this.occupied) {
            throw new IllegalStateException("Slot [" + floor + ", " + slotNumber + "] is already vacant!");
        }
        Vehicle v = this.currentVehicle;
        this.currentVehicle = null;
        this.occupied = false;
        return v;
    }

    @Override
    public String toString() {
        return String.format("Slot[F%d:S%d | %s]", floor, slotNumber, occupied ? "OCCUPIED" : "VACANT");
    }
}
