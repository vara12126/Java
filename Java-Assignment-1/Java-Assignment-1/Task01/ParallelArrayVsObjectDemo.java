package Task01;

/**
 * Task 01 Demonstration:
 * Comparing Object-Oriented representation versus Parallel Arrays representation
 * for vehicle modeling in a Smart-Parking System.
 *
 * Student: Kattiripalli Dhanush
 * Register Number: 192311094
 */
public class ParallelArrayVsObjectDemo {

    // --- Object-Oriented Representation ---
    static class VehicleObject {
        private final String regNo;
        private final String type;
        private final long entryTime;

        public VehicleObject(String regNo, String type, long entryTime) {
            this.regNo = regNo;
            this.type = type;
            this.entryTime = entryTime;
        }

        public String getRegNo() { return regNo; }
        public String getType() { return type; }
        public long getEntryTime() { return entryTime; }

        @Override
        public String toString() {
            return "VehicleObject[regNo=" + regNo + ", type=" + type + ", entryTime=" + entryTime + "]";
        }
    }

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("  TASK 01: OBJECT-ORIENTED MODEL VS PARALLEL ARRAYS DEMO  ");
        System.out.println("  Student: Kattiripalli Dhanush | Reg No: 192311094      ");
        System.out.println("==========================================================\n");

        int capacity = 3;

        // 1. Parallel Arrays Representation
        System.out.println("--- 1. Parallel Arrays Representation ---");
        String[] parallelRegNo = new String[capacity];
        String[] parallelType = new String[capacity];
        long[] parallelEntryTime = new long[capacity];

        // Inserting data into parallel arrays
        parallelRegNo[0] = "TN01A1111"; parallelType[0] = "TwoWheeler"; parallelEntryTime[0] = 1000L;
        parallelRegNo[1] = "TN02B2222"; parallelType[1] = "Car";        parallelEntryTime[1] = 1015L;
        parallelRegNo[2] = "TN03C3333"; parallelType[2] = "ElectricCar"; parallelEntryTime[2] = 1030L;

        // Searching in parallel arrays
        String searchTarget = "TN02B2222";
        int foundIndex = -1;
        for (int i = 0; i < capacity; i++) {
            if (parallelRegNo[i] != null && parallelRegNo[i].equals(searchTarget)) {
                foundIndex = i;
                break;
            }
        }
        if (foundIndex != -1) {
            System.out.println("Parallel Array Search Found: index=" + foundIndex 
                + ", RegNo=" + parallelRegNo[foundIndex] 
                + ", Type=" + parallelType[foundIndex] 
                + ", EntryTime=" + parallelEntryTime[foundIndex]);
        }

        // 2. Object-Oriented Representation
        System.out.println("\n--- 2. Object-Oriented Representation ---");
        VehicleObject[] objectList = new VehicleObject[capacity];
        objectList[0] = new VehicleObject("TN01A1111", "TwoWheeler", 1000L);
        objectList[1] = new VehicleObject("TN02B2222", "Car", 1015L);
        objectList[2] = new VehicleObject("TN03C3333", "ElectricCar", 1030L);

        // Searching in object array
        VehicleObject foundObject = null;
        for (VehicleObject v : objectList) {
            if (v != null && v.getRegNo().equals(searchTarget)) {
                foundObject = v;
                break;
            }
        }
        System.out.println("Object Search Found: " + foundObject);

        System.out.println("\nKey Takeaway: The Object-Oriented approach encapsulates all related data");
        System.out.println("into a cohesive type, preventing data desynchronization and simplifying extensibility.");
    }
}
