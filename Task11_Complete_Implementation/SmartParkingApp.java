/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 11: Complete Smart-Parking Implementation
 * 
 * Interactive Menu-Driven Application.
 */
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class SmartParkingApp {

    private final ParkingFacility facility;
    private final Scanner scanner;

    public SmartParkingApp(ParkingFacility facility, Scanner scanner) {
        this.facility = facility;
        this.scanner = scanner;
    }

    public void run() {
        boolean running = true;
        System.out.println("==================================================");
        System.out.println("WELCOME TO " + facility.getName().toUpperCase());
        System.out.println("==================================================");

        while (running) {
            printMenu();
            System.out.print("Select an option (1-6): ");
            String input = scanner.hasNextLine() ? scanner.nextLine().trim() : "6";

            switch (input) {
                case "1":
                    handleVehicleEntry();
                    break;
                case "2":
                    handleVehicleExit();
                    break;
                case "3":
                    facility.displayGrid();
                    break;
                case "4":
                    handleViewActiveTickets();
                    break;
                case "5":
                    handleDailyCollection();
                    break;
                case "6":
                    System.out.println("\nThank you for using Campus Smart-Parking System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("[Error] Invalid choice. Please select between 1 and 6.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n-------------------------------------------");
        System.out.println("         SMART PARKING OPERATIONS");
        System.out.println("-------------------------------------------");
        System.out.println("1. Vehicle Entry");
        System.out.println("2. Vehicle Exit");
        System.out.println("3. Display Parking Slots");
        System.out.println("4. View Active Tickets");
        System.out.println("5. Calculate Daily Collection");
        System.out.println("6. Exit Application");
        System.out.println("-------------------------------------------");
    }

    private void handleVehicleEntry() {
        System.out.println("\n--- 1. VEHICLE ENTRY REGISTRATION ---");
        try {
            System.out.print("Enter Registration Number (e.g. TN01AB1234): ");
            String reg = scanner.nextLine().trim();

            System.out.println("Select Vehicle Type:");
            System.out.println("  1. Two-Wheeler (Rs 10/hr)");
            System.out.println("  2. Car (Rs 30/hr)");
            System.out.println("  3. Electric Car (Rs 30/hr base + Rs 25/hr charging)");
            System.out.print("Choice: ");
            String typeChoice = scanner.nextLine().trim();

            Vehicle vehicle;
            boolean needsEvCharging = false;
            switch (typeChoice) {
                case "1":
                    vehicle = new TwoWheeler(reg, "Honda/Hero");
                    break;
                case "2":
                    vehicle = new Car(reg, "Maruti/Hyundai");
                    break;
                case "3":
                    System.out.print("Require EV charging plug? (yes/no): ");
                    String evAns = scanner.nextLine().trim();
                    needsEvCharging = evAns.equalsIgnoreCase("yes") || evAns.equalsIgnoreCase("y");
                    vehicle = new ElectricCar(reg, "Tata EV", needsEvCharging);
                    break;
                default:
                    System.out.println("[Error] Invalid vehicle type selection.");
                    return;
            }

            System.out.println("Select Campus Pass Concession:");
            System.out.println("  1. None / Visitor");
            System.out.println("  2. Student Monthly Pass (50% Concession)");
            System.out.println("  3. Staff Monthly Pass (100% Concession)");
            System.out.println("  4. Visitor One-Day Pass (100% Hourly Concession)");
            System.out.print("Choice: ");
            String passChoice = scanner.nextLine().trim();

            PassType passType = PassType.VISITOR;
            if ("2".equals(passChoice)) passType = PassType.STUDENT_MONTHLY;
            else if ("3".equals(passChoice)) passType = PassType.STAFF_MONTHLY;
            else if ("4".equals(passChoice)) passType = PassType.VISITOR_ONE_DAY;

            ParkingTicket ticket = facility.parkVehicle(vehicle, passType, needsEvCharging, LocalDateTime.now());
            if (ticket == null) {
                System.out.println("[ALERT] PARKING FULL! No available slots for the requested vehicle.");
            } else {
                System.out.println("\n[SUCCESS] Ticket Issued Successfully!");
                System.out.println("  Ticket ID:    " + ticket.getTicketId());
                System.out.println("  Registration: " + ticket.getRegistrationNumber());
                System.out.println("  Assigned Bay: Floor " + ticket.getFloor() + ", Slot " + ticket.getSlot());
                System.out.println("  Pass Type:    " + ticket.getPassType().getDescription());
                System.out.println("  Entry Time:   " + ticket.getEntryTime());
            }

        } catch (Exception e) {
            System.out.println("[Entry Failed] " + e.getMessage());
        }
    }

    private void handleVehicleExit() {
        System.out.println("\n--- 2. VEHICLE EXIT & PAYMENT PROCESSING ---");
        try {
            System.out.print("Enter Ticket ID or Vehicle Registration Number: ");
            String id = scanner.nextLine().trim();

            System.out.print("Simulate parking duration in minutes (or press enter for immediate exit): ");
            String minStr = scanner.nextLine().trim();
            long mins = minStr.isEmpty() ? 0 : Long.parseLong(minStr);

            LocalDateTime exitTime = LocalDateTime.now().plusMinutes(mins);
            ParkingTicket ticket = facility.exitVehicle(id, exitTime);

            System.out.println("\n[SUCCESS] Exit Receipt Generated!");
            System.out.println("==================================================");
            System.out.println("  Ticket ID:       " + ticket.getTicketId());
            System.out.println("  Registration:    " + ticket.getRegistrationNumber());
            System.out.println("  Vehicle Type:    " + ticket.getVehicleType());
            System.out.println("  Vacated Bay:     Floor " + ticket.getFloor() + ", Slot " + ticket.getSlot());
            System.out.println("  Duration:        " + ticket.getDurationMinutes() + " minutes");
            System.out.println("  Base Hourly Fee: Rs " + ticket.getBaseFee());
            if (ticket.isEvChargingUsed()) {
                System.out.println("  EV Charging Fee: Rs " + ticket.getEvChargingFee());
            }
            if (ticket.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("  Pass Discount: - Rs " + ticket.getDiscountAmount() + 
                                   " (" + ticket.getPassType().getDescription() + ")");
            }
            System.out.println("  ------------------------------------------------");
            System.out.println("  TOTAL NET PAID:  Rs " + ticket.getNetFeePaid());
            System.out.println("==================================================");

        } catch (Exception e) {
            System.out.println("[Exit Failed] " + e.getMessage());
        }
    }

    private void handleViewActiveTickets() {
        System.out.println("\n--- 4. ACTIVE PARKING TICKETS ---");
        List<ParkingTicket> tickets = facility.getActiveTicketsList();
        if (tickets.isEmpty()) {
            System.out.println("No active vehicles currently parked.");
            return;
        }
        System.out.printf("%-12s | %-12s | %-12s | %-10s | %-20s%n",
                "Ticket ID", "Reg Number", "Vehicle Type", "Location", "Pass Type");
        System.out.println("----------------------------------------------------------------------");
        for (ParkingTicket t : tickets) {
            System.out.printf("%-12s | %-12s | %-12s | F%d:S%-7d | %-20s%n",
                    t.getTicketId(), t.getRegistrationNumber(), t.getVehicleType(),
                    t.getFloor(), t.getSlot(), t.getPassType().name());
        }
    }

    private void handleDailyCollection() {
        System.out.println("\n--- 5. DAILY REVENUE TELEMETRY ---");
        System.out.println("Cumulative Revenue Collected Today: Rs " + facility.getDailyTotalCollection());
    }

    public static void main(String[] args) {
        // Default standard campus facility: 3 floors with varying slot capacities
        int[] capacities = {4, 3, 2};
        int[] evSlots = {2, 1, 0}; // Floor 0 has 2 EV bays, Floor 1 has 1 EV bay
        ParkingFacility facility = new ParkingFacility("IIT Campus Central Parking", capacities, evSlots);

        Scanner scanner = new Scanner(System.in);
        SmartParkingApp app = new SmartParkingApp(facility, scanner);
        app.run();
    }
}
