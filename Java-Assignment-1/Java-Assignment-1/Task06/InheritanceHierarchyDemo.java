package Task06;

/**
 * Task 06 Demonstration:
 * Inheritance Hierarchy, Super Keyword Usage, Constructor Execution Tracing,
 * and Inheritance vs Composition Comparison.
 *
 * Student: Kattiripalli Dhanush
 * Register Number: 192311094
 */
public class InheritanceHierarchyDemo {

    // --- 1. Inheritance Hierarchy with Constructor Tracing ---

    static abstract class Vehicle {
        private final String regNo;

        protected Vehicle(String regNo) {
            System.out.println("   [Step 1] Vehicle constructor executing. regNo=" + regNo);
            this.regNo = regNo;
        }

        public String getRegNo() { return regNo; }
        protected abstract double ratePerHour();

        public double calculateFee(int minutes) {
            int hours = (minutes + 59) / 60;
            return hours * ratePerHour();
        }
    }

    static class TwoWheeler extends Vehicle {
        public TwoWheeler(String regNo) {
            super(regNo);
            System.out.println("   [Step 2] TwoWheeler constructor executing.");
        }

        @Override
        protected double ratePerHour() { return 10.0; }
    }

    static class Car extends Vehicle {
        public Car(String regNo) {
            super(regNo);
            System.out.println("   [Step 2] Car constructor executing.");
        }

        @Override
        protected double ratePerHour() { return 30.0; }
    }

    static class ElectricCar extends Car {
        public ElectricCar(String regNo) {
            super(regNo);
            System.out.println("   [Step 3] ElectricCar constructor executing.");
        }

        public double chargingFee(int kWh) {
            return kWh * 12.0;
        }
    }

    // --- 2. Composition Alternative ---

    static class VehicleType {
        private final String name;
        private final double rate;

        public VehicleType(String name, double rate) {
            this.name = name;
            this.rate = rate;
        }
        public double getRate() { return rate; }
        public String getName() { return name; }
    }

    static class ComposedVehicle {
        private final String regNo;
        private final VehicleType type; // Has-a relationship

        public ComposedVehicle(String regNo, VehicleType type) {
            this.regNo = regNo;
            this.type = type;
        }

        public double calculateFee(int minutes) {
            int hours = (minutes + 59) / 60;
            return hours * type.getRate();
        }
    }

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("  TASK 06: INHERITANCE HIERARCHY & CONSTRUCTOR TRACE DEMO ");
        System.out.println("  Student: Kattiripalli Dhanush | Reg No: 192311094      ");
        System.out.println("==========================================================\n");

        System.out.println("--- Tracing Constructor Execution Order for ElectricCar ---");
        System.out.println("Executing: Vehicle v = new ElectricCar(\"TN03C3333\");");
        Vehicle ev = new ElectricCar("TN03C3333");
        System.out.println("Constructor chain completed successfully!\n");

        System.out.println("--- Verifying Inherited and Specialized Calculations ---");
        System.out.println("Vehicle RegNo: " + ev.getRegNo());
        System.out.println("Fee for 130 min: Rs " + ev.calculateFee(130));

        System.out.println("\n--- Demonstrating Composition Alternative ---");
        VehicleType suvType = new VehicleType("SUV", 40.0);
        ComposedVehicle composed = new ComposedVehicle("TN09XY9999", suvType);
        System.out.println("Composed Vehicle (" + suvType.getName() + ") Fee for 130 min: Rs " 
            + composed.calculateFee(130));
    }
}
