package task02_hospital;

/**
 * Entity representing a medical specialist.
 * 
 * Student: Devi Vara Prasad | Reg: 192365032
 */
public class Doctor {

    private final String id;
    private final String name;
    private final String department;
    private final String specialization;

    public Doctor(String id, String name, String department, String specialization) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.specialization = specialization;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getSpecialization() { return specialization; }

    @Override
    public String toString() {
        return name + " (" + specialization + ")";
    }
}
