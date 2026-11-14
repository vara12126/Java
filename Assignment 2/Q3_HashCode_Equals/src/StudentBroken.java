package q3_hashcode;

import java.util.Objects;

/**
 * Step 1: Broken Student implementation where equals() is overridden based on studentId,
 * but hashCode() is NOT overridden (inheriting java.lang.Object.hashCode()).
 * 
 * Student: Devi Vara Prasad (Reg: 192365032)
 */
public class StudentBroken {
    private final String studentId;
    private final String name;
    private final String department;

    public StudentBroken(String studentId, String name, String department) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
    }

    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentBroken that = (StudentBroken) o;
        return Objects.equals(studentId, that.studentId);
    }

    // Notice: hashCode() is intentionally NOT overridden here!
    // Inherits default Object.hashCode() which computes a distinct memory-address-based hash.

    @Override
    public String toString() {
        return String.format("StudentBroken[ID=%s, Name=%s, Dept=%s, SystemHash=%d]",
                studentId, name, department, System.identityHashCode(this));
    }
}
