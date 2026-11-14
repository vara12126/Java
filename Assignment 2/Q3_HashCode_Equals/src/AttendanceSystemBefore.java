package q3_hashcode;

import java.util.HashSet;
import java.util.Set;

/**
 * Step 1 Execution: Student Attendance System WITHOUT hashCode() overridden.
 * 
 * Demonstrates the severe bug where duplicate students are registered in a HashSet
 * because the hashCode-equals contract is violated.
 * 
 * Student: Devi Vara Prasad (Reg: 192365032)
 */
public class AttendanceSystemBefore {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 2 - QUESTION 3: ATTENDANCE SYSTEM (WITHOUT hashCode())            ");
        System.out.println("   Student: Devi Vara Prasad | Register Number: 192365032                       ");
        System.out.println("   Status: Testing Initial State BEFORE Completing TODO 1                       ");
        System.out.println("================================================================================\n");

        Set<StudentBroken> attendanceRegister = new HashSet<>();

        StudentBroken s1 = new StudentBroken("192365032", "Devi Vara Prasad", "CSE");
        StudentBroken s2 = new StudentBroken("192365032", "Devi Vara Prasad", "CSE"); // Identical duplicate record

        System.out.println("Testing Equivalence directly:");
        System.out.println("  s1.equals(s2)             : " + s1.equals(s2) + " [Logically EQUAL]");
        System.out.println("  s1.hashCode() (System)    : " + s1.hashCode());
        System.out.println("  s2.hashCode() (System)    : " + s2.hashCode());
        System.out.println("  s1.hashCode() == s2.hashCode() : " + (s1.hashCode() == s2.hashCode()) + " [VIOLATION OF CONTRACT!]\n");

        System.out.println("Registering students into HashSet Attendance Register...");
        boolean added1 = attendanceRegister.add(s1);
        boolean added2 = attendanceRegister.add(s2);

        System.out.println("  Added s1 successfully?    : " + added1);
        System.out.println("  Added s2 (duplicate)?     : " + added2 + " <-- [BUG: Duplicate accepted by HashSet!]");
        System.out.println("  Attendance Register Size  : " + attendanceRegister.size() + " (Expected: 1, Actual: " + attendanceRegister.size() + ")");

        System.out.println("\nRegistered Student Entries in HashSet:");
        for (StudentBroken s : attendanceRegister) {
            System.out.println("  -> " + s);
        }

        System.out.println("\nRoot Cause Diagnostic Analysis:");
        System.out.println("1. HashSet is backed by a HashMap. When adding an element, it computes:");
        System.out.println("   bucketIndex = (table.length - 1) & hash(key.hashCode()).");
        System.out.println("2. Because StudentBroken did NOT override hashCode(), it inherited Object.hashCode(),");
        System.out.println("   which assigned different 32-bit identity hashes based on memory addresses.");
        System.out.println("3. The two objects were placed in completely DIFFERENT buckets, or their hash check failed.");
        System.out.println("   Therefore, equals() was NEVER even evaluated to detect the logical duplicate!");
        System.out.println("4. Behavior depends on hash distribution and execution: while different buckets");
        System.out.println("   guarantee failure, even if two objects collide in the same bucket, the preliminary");
        System.out.println("   (p.hash == hash) check fails, preventing equals() from merging them.");
        System.out.println("================================================================================\n");
    }
}
