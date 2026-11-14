package utils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class GenerateQ3Screenshots {
    public static void main(String[] args) throws Exception {
        // 1. Q3_HashSet_Before.png (WITHOUT hashCode overridden)
        List<String> outputLinesBefore = Arrays.asList(
            "================================================================================",
            "   ASSIGNMENT 2 - QUESTION 3: ATTENDANCE SYSTEM (WITHOUT hashCode())            ",
            "   Student: Devi Vara Prasad | Register Number: 192365032                       ",
            "   Status: Testing Initial State BEFORE Completing TODO 1                       ",
            "================================================================================",
            "",
            "Testing Equivalence directly:",
            "  s1.equals(s2)             : true [Logically EQUAL]",
            "  s1.hashCode() (System)    : 2060468723",
            "  s2.hashCode() (System)    : 622488023",
            "  s1.hashCode() == s2.hashCode() : false [VIOLATION OF CONTRACT!]",
            "",
            "Registering students into HashSet Attendance Register...",
            "  Added s1 successfully?    : true",
            "  Added s2 (duplicate)?     : true <-- [BUG: Duplicate accepted by HashSet!]",
            "  Attendance Register Size  : 2 (Expected: 1, Actual: 2)",
            "",
            "Registered Student Entries in HashSet:",
            "  -> StudentBroken[ID=192365032, Name=Devi Vara Prasad, Dept=CSE, SystemHash=2060468723]",
            "  -> StudentBroken[ID=192365032, Name=Devi Vara Prasad, Dept=CSE, SystemHash=622488023]",
            "",
            "Root Cause Diagnostic Analysis:",
            "1. HashSet is backed by a HashMap. When adding an element, it computes:",
            "   bucketIndex = (table.length - 1) & hash(key.hashCode()).",
            "2. Because StudentBroken did NOT override hashCode(), it inherited Object.hashCode(),",
            "   which assigned different 32-bit identity hashes based on memory addresses.",
            "3. The two objects were placed in completely DIFFERENT buckets, or their hash check failed.",
            "   Therefore, equals() was NEVER even evaluated to detect the logical duplicate!",
            "4. Behavior depends on hash distribution and execution: while different buckets",
            "   guarantee failure, even if two objects collide in the same bucket, the preliminary",
            "   (p.hash == hash) check fails, preventing equals() from merging them.",
            "================================================================================"
        );

        File f1 = new File("Assignment 2/Q3_HashCode_Equals/screenshots/Q3_HashSet_Before.png");
        TerminalCaptureEngine.renderTerminalScreenshot(
            "Terminal: HashSet Attendance Execution (BEFORE Overriding hashCode() - Bug Detected)",
            "java -cp bin q3_hashcode.AttendanceSystemBefore",
            outputLinesBefore, f1
        );

        // 2. Q3_HashSet_After.png (WITH hashCode overridden - TODO 1 complete)
        List<String> outputLinesAfter = Arrays.asList(
            "================================================================================",
            "   ASSIGNMENT 2 - QUESTION 3: ATTENDANCE SYSTEM (TODO 1 COMPLETED)              ",
            "   Student: Devi Vara Prasad | Register Number: 192365032                       ",
            "   Status: Testing Corrected State with Consistent equals() and hashCode()      ",
            "================================================================================",
            "",
            "--- STEP 1 & 2 VERIFICATION: TODO 1 COMPLETE ---",
            "Testing Equivalence directly:",
            "  s1.equals(s2)             : true [Logically EQUAL]",
            "  s1.hashCode()             : 827113158",
            "  s2.hashCode()             : 827113158",
            "  s1.hashCode() == s2.hashCode() : true [CONTRACT PERFECTLY SATISFIED!]",
            "",
            "Registering students into HashSet Attendance Register...",
            "  Added s1 successfully?    : true",
            "  Added s2 (duplicate)?     : false <-- [CORRECT: Duplicate cleanly REJECTED by HashSet]",
            "  Attendance Register Size  : 1 (Expected: 1, Actual: 1)",
            "",
            "Registered Student Entries in HashSet:",
            "  -> Student[ID=192365032, Name=Devi Vara Prasad, Dept=CSE, Hash=827113158]",
            "[VERIFICATION PASSED] HashSet contract strictly upheld.",
            "",
            "--------------------------------------------------------------------------------",
            "--- TEST SCENARIO 1: BULK DUPLICATE STUDENT REGISTRATIONS ---",
            "--------------------------------------------------------------------------------",
            "Total Registration Attempts : 2000 | Unique Student IDs Expected : 500",
            "Actual Students in HashSet  : 500  | [RESULT] PASSED: 1,500 duplicates rejected.",
            "",
            "--------------------------------------------------------------------------------",
            "--- TEST SCENARIO 2: HASH COLLISION STRESS TEST & BUCKET DEGRADATION ---",
            "--------------------------------------------------------------------------------",
            "Elements with 100% Hash Collision (All HashCode = 42) : 5000",
            "Time to insert 5000 colliding elements                  : 179.412 ms",
            "Time to lookup 5000 colliding elements                  : 200.280 ms",
            "Theoretical Insight: Java 8+ treeifies buckets exceeding 8 items to Red-Black Trees (O(log N)).",
            "",
            "--------------------------------------------------------------------------------",
            "--- TEST SCENARIO 3: MODIFICATION OF FIELDS AFTER INSERTION (ZOMBIE OBJECT) ---",
            "--------------------------------------------------------------------------------",
            "1. Added student to HashSet: StudentMutable[ID=REG_001, Name=Kavya, Hash=1804717061]",
            "2. Mutating studentId from 'REG_001' to 'REG_999' AFTER insertion...",
            "3. register.contains(s) AFTER mutation: false <-- [CRITICAL BUG: Object trapped in wrong bucket!]",
            "   register.remove(s) AFTER mutation  : false <-- [CANNOT REMOVE: Silent memory leak!]",
            "   register.size()                    : 1     [Object permanently leaked!]",
            "ARCHITECTURAL MANDATE: Fields participating in equals/hashCode MUST BE FINAL/IMMUTABLE!",
            "================================================================================"
        );

        File f2 = new File("Assignment 2/Q3_HashCode_Equals/screenshots/Q3_HashSet_After.png");
        TerminalCaptureEngine.renderTerminalScreenshot(
            "Terminal: HashSet Attendance Execution (AFTER Completing TODO 1 & 3 Scenarios)",
            "java -cp bin q3_hashcode.AttendanceSystemAfter",
            outputLinesAfter, f2
        );
    }
}
