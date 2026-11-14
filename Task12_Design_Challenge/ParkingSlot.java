/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 12: Day-4 Design Challenge
 */
public class ParkingSlot {
    private final int floor;
    private final int slotNumber;
    private boolean occupied;
    private Vehicle parkedVehicle;

    public ParkingSlot(int floor, int slotNumber) {
        this.floor = floor;
        this.slotNumber = slotNumber;
        this.occupied = false;
        this.parkedVehicle = null;
    }

    public int getFloor() { return floor; }
    public int getSlotNumber() { return slotNumber; }
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
        return String.format("[F%d:S%d | %s]", floor, slotNumber, 
                occupied ? parkedVehicle.getRegistrationNumber() : "FREE");
    }
}
