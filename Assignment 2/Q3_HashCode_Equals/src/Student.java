package q3_hashcode;

import java.util.Objects;

/**
 * Step 2: Fully compliant Student entity adhering strictly to the java.lang.Object
 * hashCode-equals contract.
 * 
 * TODO 1 Completed: Implemented consistent hashCode() based on unique studentId.
 * 
 * Student: Devi Vara Prasad (Reg: 192365032)
 */
public class Student {
    private final String studentId;
    private final String name;
    private final String department;

    public Student(String studentId, String name, String department) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty.");
        }
        this.studentId = studentId.trim();
        this.name = name;
        this.department = department;
    }

    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }

    /**
     * Equivalence Contract: Two Student objects are equal if and only if
     * their unique student IDs are identical.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(studentId, student.studentId);
    }

    /**
     * TODO 1: Implement hashCode() consistent with equals().
     * Since equals() compares studentId, hashCode() MUST compute its hash from studentId.
     */
    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    @Override
    public String toString() {
        return String.format("Student[ID=%s, Name=%s, Dept=%s, Hash=%d]",
                studentId, name, department, hashCode());
    }
}
