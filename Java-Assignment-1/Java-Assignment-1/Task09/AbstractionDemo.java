package Task09;

/**
 * Task 09 Demonstration:
 * Abstraction in Java - Abstract Classes vs Interfaces,
 * Default/Static Interface Methods, and Multiple Interface Realization.
 *
 * Student: Kattiripalli Dhanush
 * Register Number: 192311094
 */
public class AbstractionDemo {

    // 1. Interface: Chargeable with abstract, default, and static methods
    public interface Chargeable {
        double chargingFee(int kWh);

        // Default method: Estimated charging duration on a 7.4 kW AC charger
        default double estimateChargingTimeHours(int kWh) {
            return Math.round((kWh / 7.4) * 100.0) / 100.0;
        }

        // Static utility method: Standard government subsidized green tariff
        static double getGreenSubsidyDiscount(double fee) {
            return fee * 0.05; // 5% clean energy incentive
        }
    }

    // 2. Interface: Payable for automated payment gateway integration
    public interface Payable {
        boolean processPayment(double amount);
    }

    // 3. Abstract Class: Vehicle encapsulating state and constructor
    public abstract static class Vehicle {
        private final String regNo;

        protected Vehicle(String regNo) {
            if (regNo == null || regNo.trim().isEmpty()) {
                throw new IllegalArgumentException("Registration number cannot be empty");
            }
            this.regNo = regNo;
        }

        public final String getRegNo() { return regNo; }

        protected abstract double ratePerHour();

        public double calculateFee(int minutes) {
            int hours = (minutes + 59) / 60;
            return hours * ratePerHour();
        }
    }

    // 4. Concrete Subclass: Car
    public static class Car extends Vehicle {
        public Car(String regNo) { super(regNo); }
        @Override protected double ratePerHour() { return 30.0; }
    }

    // 5. Concrete Subclass: ElectricCar realizing multiple interfaces
    public static class ElectricCar extends Car implements Chargeable, Payable {
        public ElectricCar(String regNo) { super(regNo); }

        @Override
        public double chargingFee(int kWh) {
            return kWh * 12.0;
        }

        @Override
        public boolean processPayment(double amount) {
            System.out.printf("   [Payment Gateway] Successfully debited Rs %.2f from EV FastTag/Wallet.%n", amount);
            return true;
        }
    }

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("  TASK 09: ABSTRACTION & INTERFACE CAPABILITIES DEMO      ");
        System.out.println("  Student: Kattiripalli Dhanush | Reg No: 192311094      ");
        System.out.println("==========================================================\n");

        ElectricCar ev = new ElectricCar("TN03EV9999");

        System.out.println("1. Vehicle Identification: " + ev.getRegNo());
        
        // Fee computation through abstract class template method
        double parkingFee = ev.calculateFee(125); // 3 hours * 30 = 90
        System.out.println("2. Base Parking Fee (125 min): Rs " + parkingFee);

        // Charging calculation through Chargeable interface
        int consumedKWh = 15;
        double chargeCost = ev.chargingFee(consumedKWh);
        double estHours = ev.estimateChargingTimeHours(consumedKWh); // Default method
        System.out.println("3. EV Charging Fee (" + consumedKWh + " kWh @ Rs 12): Rs " + chargeCost);
        System.out.println("   Estimated Charging Duration: " + estHours + " hours");

        double totalPayable = parkingFee + chargeCost;
        double subsidy = Chargeable.getGreenSubsidyDiscount(chargeCost); // Static method
        totalPayable -= subsidy;
        System.out.printf("4. Green Energy Subsidy: -Rs %.2f%n", subsidy);
        System.out.printf("   Net Payable Amount: Rs %.2f%n", totalPayable);

        // Payment execution through Payable interface
        System.out.println("5. Invoking Payable Interface:");
        ev.processPayment(totalPayable);

        System.out.println("\nAbstraction cleanly segregated state, template logic, and pluggable capabilities.");
    }
}
