package q6_triage;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Hospital Emergency Department Triage System using java.util.PriorityQueue.
 * 
 * Rules:
 * 1. Lower severity number means higher clinical priority (Severity 1 before 2).
 * 2. Equal severity served strictly in arrival order (FIFO tie-breaker).
 * 
 * Completed TODO: PriorityQueue comparator and patient scheduling.
 * Guaranteed Exact Output:
 * Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)
 * 
 * Student: Devi Vara Prasad (Reg: 192365032)
 */
public class TriageSystem {
    private static long arrivalCounter = 0;

    public static synchronized Patient registerPatient(String name, int severity) {
        return new Patient(name, severity, ++arrivalCounter);
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 2 - QUESTION 6: HOSPITAL EMERGENCY TRIAGE SYSTEM                   ");
        System.out.println("   Student: Devi Vara Prasad | Register Number: 192365032                       ");
        System.out.println("   Environment: Java SE " + System.getProperty("java.version") + " (" + System.getProperty("os.name") + ")");
        System.out.println("================================================================================\n");

        // -------------------------------------------------------------------------
        // TODO: Construct PriorityQueue Comparator
        // Rule 1: Lower severity number -> Higher Priority (Ascending)
        // Rule 2: Earlier arrival order -> Higher Priority (Ascending FIFO)
        // -------------------------------------------------------------------------
        Comparator<Patient> triageComparator = Comparator
                .comparingInt(Patient::getSeverity)
                .thenComparingLong(Patient::getArrivalOrder);

        PriorityQueue<Patient> triageQueue = new PriorityQueue<>(triageComparator);

        // Register incoming patients in chronological arrival sequence
        Patient asha = registerPatient("Asha", 5);   // Arrival 1
        Patient ravi = registerPatient("Ravi", 3);   // Arrival 2
        Patient lata = registerPatient("Lata", 2);   // Arrival 3
        Patient meena = registerPatient("Meena", 1); // Arrival 4
        Patient john = registerPatient("John", 3);   // Arrival 5
        Patient kumar = registerPatient("Kumar", 1); // Arrival 6

        // Insert into PriorityQueue
        triageQueue.offer(asha);
        triageQueue.offer(ravi);
        triageQueue.offer(lata);
        triageQueue.offer(meena);
        triageQueue.offer(john);
        triageQueue.offer(kumar);

        System.out.println("1. Patients Admitted Chronologically at Triage Reception:");
        System.out.println("   Arrival #1: " + asha);
        System.out.println("   Arrival #2: " + ravi);
        System.out.println("   Arrival #3: " + lata);
        System.out.println("   Arrival #4: " + meena);
        System.out.println("   Arrival #5: " + john);
        System.out.println("   Arrival #6: " + kumar);

        // Crucial Investigation: Why printing PriorityQueue directly is NOT sorted
        System.out.println("\n2. Direct Queue Print (System.out.println(triageQueue)):");
        System.out.println("   " + triageQueue);
        System.out.println("   EXPLANATION WHY DIRECT PRINT IS NOT SORTED:");
        System.out.println("   PriorityQueue is implemented as a binary min-heap stored in a flat array (Object[] queue).");
        System.out.println("   Its toString() and iterator() traverse the underlying array sequentially in level-order,");
        System.out.println("   satisfying only the min-heap parent-child condition (A[parent] <= A[child]), NOT a fully");
        System.out.println("   sorted total order. Sorted priority extraction is ONLY achieved by sequential poll() calls.");

        System.out.println("\n3. Treating Patients in Strict Priority Order (poll() Sequence):");
        System.out.print("   ");
        StringBuilder outputBuilder = new StringBuilder();
        while (!triageQueue.isEmpty()) {
            Patient treated = triageQueue.poll();
            System.out.print(treated + " ");
            outputBuilder.append(treated).append(" ");
        }
        System.out.println("\n");

        System.out.println("Exact Output Verification:");
        System.out.println("Target   : Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)");
        System.out.println("Produced : " + outputBuilder.toString().trim());
        boolean matched = outputBuilder.toString().trim().equals("Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)");
        System.out.println("Result   : " + (matched ? "[VERIFICATION PASSED - 100% MATCH]" : "[MISMATCH]"));

        // Extended Verification with ~8 Patients
        testExtendedEightPatients(triageComparator);
    }

    private static void testExtendedEightPatients(Comparator<Patient> comparator) {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("--- EXTENDED VERIFICATION: 8 PATIENT CLINICAL TRIAGE VALIDATION ---");
        System.out.println("--------------------------------------------------------------------------------");
        PriorityQueue<Patient> queue8 = new PriorityQueue<>(comparator);

        Patient p1 = registerPatient("Asha", 5);
        Patient p2 = registerPatient("Ravi", 3);
        Patient p3 = registerPatient("Lata", 2);
        Patient p4 = registerPatient("Meena", 1);
        Patient p5 = registerPatient("John", 3);
        Patient p6 = registerPatient("Kumar", 1);
        Patient p7 = registerPatient("Priya", 2);   // S2 tie-breaker with Lata
        Patient p8 = registerPatient("Vikram", 4);  // S4

        queue8.offer(p1); queue8.offer(p2); queue8.offer(p3); queue8.offer(p4);
        queue8.offer(p5); queue8.offer(p6); queue8.offer(p7); queue8.offer(p8);

        System.out.println("Clinical Triage Order for 8 Admitted Patients:");
        int step = 1;
        while (!queue8.isEmpty()) {
            Patient p = queue8.poll();
            System.out.printf("  Step %d: Treat %-12s | Severity: S%d | Arrival Order: #%d%n",
                    step++, p.getName(), p.getSeverity(), p.getArrivalOrder());
        }
        System.out.println("--------------------------------------------------------------------------------\n");
    }
}
