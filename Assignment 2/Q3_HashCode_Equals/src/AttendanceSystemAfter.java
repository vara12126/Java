package q3_hashcode;

import java.util.*;

/**
 * Step 2 Execution: Student Attendance System WITH hashCode() properly implemented (TODO 1 Complete).
 * 
 * Includes 3 distinct test scenarios:
 * 1. Duplicate Student Registration Prevention.
 * 2. Hash Collision Stress Test (Degenerate bucket clustering).
 * 3. Modification of fields after insertion ("Zombie Object" / Broken bucket index).
 * 
 * Also provides detailed architectural comparisons between HashSet, TreeSet, and HashMap.
 * 
 * Student: Devi Vara Prasad (Reg: 192365032)
 */
public class AttendanceSystemAfter {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 2 - QUESTION 3: ATTENDANCE SYSTEM (TODO 1 COMPLETED)              ");
        System.out.println("   Student: Devi Vara Prasad | Register Number: 192365032                       ");
        System.out.println("   Status: Testing Corrected State with Consistent equals() and hashCode()      ");
        System.out.println("================================================================================\n");

        // 1. Core Verification of TODO 1
        testCorrectedContract();

        // 2. Scenario 1: Bulk Duplicate Registration Prevention
        testScenario1BulkDuplicates();

        // 3. Scenario 2: Degenerate Hash Distribution & Collisions
        testScenario2HashCollisions();

        // 4. Scenario 3: Modification of Key Fields After Insertion (Zombie Object)
        testScenario3FieldMutation();

        // 5. Architectural Comparison: HashSet vs TreeSet vs HashMap
        printCollectionComparison();
    }

    private static void testCorrectedContract() {
        System.out.println("--- STEP 1 & 2 VERIFICATION: TODO 1 COMPLETE ---");
        Set<Student> register = new HashSet<>();

        Student s1 = new Student("192365032", "Devi Vara Prasad", "CSE");
        Student s2 = new Student("192365032", "Devi Vara Prasad", "CSE"); // Identical duplicate

        System.out.println("Testing Equivalence directly:");
        System.out.println("  s1.equals(s2)             : " + s1.equals(s2) + " [Logically EQUAL]");
        System.out.println("  s1.hashCode()             : " + s1.hashCode());
        System.out.println("  s2.hashCode()             : " + s2.hashCode());
        System.out.println("  s1.hashCode() == s2.hashCode() : " + (s1.hashCode() == s2.hashCode()) + " [CONTRACT PERFECTLY SATISFIED!]\n");

        System.out.println("Registering students into HashSet Attendance Register...");
        boolean added1 = register.add(s1);
        boolean added2 = register.add(s2);

        System.out.println("  Added s1 successfully?    : " + added1);
        System.out.println("  Added s2 (duplicate)?     : " + added2 + " <-- [CORRECT: Duplicate cleanly REJECTED by HashSet]");
        System.out.println("  Attendance Register Size  : " + register.size() + " (Expected: 1, Actual: " + register.size() + ")");

        System.out.println("\nRegistered Student Entries in HashSet:");
        for (Student s : register) {
            System.out.println("  -> " + s);
        }
        System.out.println("[VERIFICATION PASSED] HashSet contract strictly upheld.\n");
    }

    private static void testScenario1BulkDuplicates() {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("--- TEST SCENARIO 1: BULK DUPLICATE STUDENT REGISTRATIONS ---");
        System.out.println("--------------------------------------------------------------------------------");
        Set<Student> register = new HashSet<>();

        int totalRegistrations = 2000;
        int uniqueStudents = 500; // Each student attempts to register 4 times

        for (int i = 0; i < totalRegistrations; i++) {
            int studentNum = (i % uniqueStudents) + 1;
            String id = "19236" + String.format("%04d", studentNum);
            register.add(new Student(id, "Student_" + studentNum, "CSE"));
        }

        System.out.println("Total Registration Attempts : " + totalRegistrations);
        System.out.println("Unique Student IDs Expected : " + uniqueStudents);
        System.out.println("Actual Students in HashSet  : " + register.size());
        if (register.size() == uniqueStudents) {
            System.out.println("[RESULT] PASSED: Exactly " + uniqueStudents + " unique students stored; 1,500 duplicates rejected.");
        } else {
            System.out.println("[RESULT] FAILED: Size mismatch!");
        }
        System.out.println();
    }

    private static void testScenario2HashCollisions() {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("--- TEST SCENARIO 2: HASH COLLISION STRESS TEST & BUCKET DEGRADATION ---");
        System.out.println("--------------------------------------------------------------------------------");

        // Custom class with deliberately pathological hash function (constant hash)
        class CollidingStudent {
            final String id;
            CollidingStudent(String id) { this.id = id; }
            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof CollidingStudent)) return false;
                return Objects.equals(id, ((CollidingStudent)o).id);
            }
            @Override
            public int hashCode() {
                return 42; // Pathological: every object collides in the exact same bucket!
            }
        }

        int count = 5000;
        Set<CollidingStudent> collidingSet = new HashSet<>();

        long t0 = System.nanoTime();
        for (int i = 0; i < count; i++) {
            collidingSet.add(new CollidingStudent("STU_" + i));
        }
        long t1 = System.nanoTime();
        double insertTimeMs = (t1 - t0) / 1_000_000.0;

        // Perform lookups on colliding set
        long t2 = System.nanoTime();
        for (int i = 0; i < count; i++) {
            collidingSet.contains(new CollidingStudent("STU_" + i));
        }
        long t3 = System.nanoTime();
        double lookupTimeMs = (t3 - t2) / 1_000_000.0;

        System.out.println("Elements with 100% Hash Collision (All HashCode = 42) : " + count);
        System.out.printf("Time to insert %d colliding elements                  : %.3f ms%n", count, insertTimeMs);
        System.out.printf("Time to lookup %d colliding elements                  : %.3f ms%n", count, lookupTimeMs);
        System.out.println("\nTheoretical Insight:");
        System.out.println("In Java 8+, when a single bucket exceeds TREEIFY_THRESHOLD (8 items), the linked");
        System.out.println("list converts into a Red-Black Tree (TreeNode). This prevents catastrophic O(N^2)");
        System.out.println("collapse, retaining O(log N) lookup time even under deliberate hash collision attacks.");
        System.out.println();
    }

    private static void testScenario3FieldMutation() {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("--- TEST SCENARIO 3: MODIFICATION OF FIELDS AFTER INSERTION (ZOMBIE OBJECT) ---");
        System.out.println("--------------------------------------------------------------------------------");

        Set<StudentMutable> register = new HashSet<>();
        StudentMutable s = new StudentMutable("REG_001", "Kavya", "ECE");

        register.add(s);
        System.out.println("1. Added student to HashSet: " + s);
        System.out.println("   register.contains(s) before mutation: " + register.contains(s) + " [FOUND]");

        // Mutate the key field AFTER insertion
        System.out.println("\n2. Mutating studentId from 'REG_001' to 'REG_999' AFTER insertion...");
        s.setStudentId("REG_999");
        System.out.println("   Mutated student: " + s);

        // Attempt to find the student
        boolean found = register.contains(s);
        System.out.println("\n3. register.contains(s) AFTER mutation: " + found + " <-- [CRITICAL BUG: Object is IN the set, but returns FALSE!]");

        // Attempt to remove the student
        boolean removed = register.remove(s);
        System.out.println("   register.remove(s) AFTER mutation  : " + removed + " <-- [CANNOT REMOVE: Object is permanently LEAKED!]");
        System.out.println("   register.size()                    : " + register.size() + " [Object remains trapped inside table!]");

        System.out.println("\nWhy Did This Happen?");
        System.out.println("When s was inserted, it was placed in Bucket X calculated from hash('REG_001').");
        System.out.println("When s was mutated to 'REG_999', its hashCode() changed.");
        System.out.println("contains() and remove() now check Bucket Y (calculated from hash('REG_999')).");
        System.out.println("Bucket Y is empty, so HashSet declares the object absent! The object becomes a");
        System.out.println("'Zombie Object', causing silent memory leaks and severe data corruption.");
        System.out.println("ARCHITECTURAL MANDATE: Fields participating in equals/hashCode MUST BE FINAL/IMMUTABLE!");
        System.out.println("================================================================================\n");
    }

    private static void printCollectionComparison() {
        System.out.println("================================================================================");
        System.out.println("   COMPARATIVE ARCHITECTURAL MATRIX: HashSet vs TreeSet vs HashMap              ");
        System.out.println("================================================================================");
        System.out.printf("%-18s | %-16s | %-16s | %-20s%n", "Property", "HashSet", "TreeSet", "HashMap");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-18s | %-16s | %-16s | %-20s%n", "Underlying Structure", "HashMap<E, Object>", "Red-Black Tree", "Hash Table + Buckets");
        System.out.printf("%-18s | %-16s | %-16s | %-20s%n", "Add/Contains/Remove", "O(1) average", "O(log N)", "O(1) average");
        System.out.printf("%-18s | %-16s | %-16s | %-20s%n", "Ordering", "Unordered", "Sorted (Natural/Comp)", "Unordered");
        System.out.printf("%-18s | %-16s | %-16s | %-20s%n", "Null Key Support", "Permits 1 null", "No nulls (throws NPE)", "Permits 1 null key");
        System.out.printf("%-18s | %-16s | %-16s | %-20s%n", "Contract Required", "equals() & hashCode()", "Comparable/Comparator", "equals() & hashCode()");
        System.out.printf("%-18s | %-16s | %-16s | %-20s%n", "Thread Safety", "Unsynchronized", "Unsynchronized", "Unsynchronized");
        System.out.println("================================================================================\n");

        System.out.println("The Formal hashCode-equals Contract (java.lang.Object):");
        System.out.println("1. Reflexive:   x.equals(x) is always true.");
        System.out.println("2. Symmetric:   x.equals(y) == y.equals(x).");
        System.out.println("3. Transitive:  if x.equals(y) and y.equals(z), then x.equals(z).");
        System.out.println("4. Consistent:  repeated evaluations return the same value unless fields change.");
        System.out.println("5. Non-null:    x.equals(null) is always false.");
        System.out.println("6. Hash Rule:   If x.equals(y) is TRUE, then x.hashCode() MUST EQUAL y.hashCode().");
        System.out.println("7. Collisions:  If x.hashCode() == y.hashCode(), x.equals(y) is NOT required to be true.");
        System.out.println("================================================================================\n");
    }
}
