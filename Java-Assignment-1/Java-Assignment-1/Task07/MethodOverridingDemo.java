package Task07;

import java.util.HashSet;
import java.util.Set;

/**
 * Task 07 Demonstration:
 * Method Overriding, @Override annotation rules, final keyword,
 * and equals/hashCode contracts for business key identity.
 *
 * Student: Kattiripalli Dhanush
 * Register Number: 192311094
 */
public class MethodOverridingDemo {

    abstract static class Vehicle {
        private final String regNo; // final field: immutable registration number

        protected Vehicle(String regNo) {
            this.regNo = regNo;
        }

        // final getter: cannot be overridden or altered by subclasses
        public final String getRegNo() { return regNo; }

        protected abstract double ratePerHour();

        public double calculateFee(int minutes) {
            int hours = (minutes + 59) / 60;
            return hours * ratePerHour();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            return o instanceof Vehicle && regNo.equals(((Vehicle) o).regNo);
        }

        @Override
        public int hashCode() {
            return regNo.hashCode();
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " [" + regNo + "]";
        }
    }

    static class TwoWheeler extends Vehicle {
        public TwoWheeler(String regNo) { super(regNo); }
        @Override protected double ratePerHour() { return 10.0; }
    }

    static class Car extends Vehicle {
        public Car(String regNo) { super(regNo); }
        @Override protected double ratePerHour() { return 30.0; }
    }

    static class ElectricCar extends Car {
        public ElectricCar(String regNo) { super(regNo); }
    }

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("  TASK 07: METHOD OVERRIDING & EQUALS/HASHCODE DEMO       ");
        System.out.println("  Student: Kattiripalli Dhanush | Reg No: 192311094      ");
        System.out.println("==========================================================\n");

        Vehicle v1 = new Car("TN02B2222");
        Vehicle v2 = new ElectricCar("TN02B2222"); // Same regNo, different subclass
        Vehicle v3 = new Car("TN07XY9999");

        System.out.println("v1: " + v1);
        System.out.println("v2: " + v2);
        System.out.println("v3: " + v3);

        System.out.println("\n--- Testing equals() and hashCode() ---");
        System.out.println("Does v1.equals(v2) [Same RegNo]? " + v1.equals(v2));
        System.out.println("Does v1.equals(v3) [Diff RegNo]? " + v1.equals(v3));
        System.out.println("v1 HashCode: " + v1.hashCode());
        System.out.println("v2 HashCode: " + v2.hashCode());
        System.out.println("Are hashCodes equal? " + (v1.hashCode() == v2.hashCode()));

        // Testing deduplication in HashSet
        Set<Vehicle> lot = new HashSet<>();
        lot.add(v1);
        boolean addedDuplicate = lot.add(v2); // Should return false because v2 is equal to v1!
        System.out.println("\nAdded v1 to set. Now attempting to add duplicate v2 with same regNo...");
        System.out.println("Was v2 added to Set? " + addedDuplicate);
        System.out.println("Total unique vehicles in Set: " + lot.size());

        System.out.println("\nEquality based on registration number works correctly across the hierarchy.");
    }
}
