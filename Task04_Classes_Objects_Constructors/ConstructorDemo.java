/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 04: Classes, Objects and Constructors
 * 
 * Demonstrates:
 * 1. Default, parameterized, and overloaded constructors.
 * 2. Constructor chaining with this(...).
 * 3. Copy constructor vs reference assignment (Stack vs Heap).
 * 4. Comparison: Constructor vs Setters vs Static Factory Method.
 */
public class ConstructorDemo {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("TASK 04: Classes, Objects and Constructors Demo");
        System.out.println("==================================================\n");

        // 1. Default and Overloaded Constructors with Chaining
        System.out.println("--- 1. Constructor Overloading & this(...) Chaining ---");
        Vehicle vDefault = new Vehicle();
        Vehicle vTwoArg = new Vehicle("TN01AA1111", "Car");
        Vehicle vFull = new Vehicle("TN02BB2222", "TwoWheeler", "Dr. Sharma");

        System.out.println("Default Constructor: " + vDefault);
        System.out.println("2-Arg Constructor:    " + vTwoArg);
        System.out.println("3-Arg Constructor:    " + vFull);

        // 2. Reference Assignment vs Copy Constructor (Stack vs Heap)
        System.out.println("\n--- 2. Reference Copy vs Copy Constructor (Heap Allocation) ---");
        // Reference assignment: Both variables in Stack point to the EXACT SAME object in Heap
        Vehicle vRefAlias = vFull;
        System.out.println("Original Object (vFull):    " + vFull);
        System.out.println("Reference Alias (vRefAlias): " + vRefAlias);
        System.out.println("Are both references identical (vFull == vRefAlias)? " + (vFull == vRefAlias));

        // Mutating the alias alters the original
        vRefAlias.setOwnerName("Dr. Sharma (Updated via Alias)");
        System.out.println("After modifying vRefAlias -> vFull.getOwnerName() = " + vFull.getOwnerName());

        // Copy Constructor: Creates an INDEPENDENT clone on Heap
        Vehicle vCloned = new Vehicle(vFull);
        System.out.println("\nCloned Object via Copy Constructor: " + vCloned);
        System.out.println("Are references identical (vFull == vCloned)? " + (vFull == vCloned));
        vCloned.setOwnerName("Independent Guest");
        System.out.println("After modifying vCloned: ");
        System.out.println("  vFull.getOwnerName()   = " + vFull.getOwnerName());
        System.out.println("  vCloned.getOwnerName() = " + vCloned.getOwnerName());

        // 3. Static Factory Method Pattern
        System.out.println("\n--- 3. Static Factory Method Demonstration ---");
        Vehicle ev = Vehicle.createElectricCar("TN03EV9999", "Prof. Raman");
        System.out.println("Created via Static Factory Method: " + ev);

        // 4. Integrated Facility & Parking Ticket Creation
        System.out.println("\n--- 4. Parking Facility & Ticket Generation ---");
        ParkingFacility facility = ParkingFacility.createStandardCampusFacility();
        System.out.println("Facility Name: " + facility.getFacilityName());
        System.out.println("Total Floors:  " + facility.getFloorCount());

        ParkingTicket t1 = facility.parkVehicle(vFull, "TICK-101");
        ParkingTicket t2 = facility.parkVehicle(ev, "TICK-102");

        System.out.println("Issued Ticket 1: " + t1);
        System.out.println("Issued Ticket 2: " + t2);
        System.out.println("==================================================");
    }
}
