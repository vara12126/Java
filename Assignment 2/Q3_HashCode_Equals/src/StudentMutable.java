package q3_hashcode;

import java.util.Objects;

/**
 * Mutable Student model designed to demonstrate Scenario 3:
 * The "Zombie Object" memory leak bug when fields participating in hashCode()
 * are modified AFTER insertion into a HashSet.
 */
public class StudentMutable {
    private String studentId;
    private String name;
    private String department;

    public StudentMutable(String studentId, String name, String department) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentMutable that = (StudentMutable) o;
        return Objects.equals(studentId, that.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    @Override
    public String toString() {
        return String.format("StudentMutable[ID=%s, Name=%s, Hash=%d]", studentId, name, hashCode());
    }
}
