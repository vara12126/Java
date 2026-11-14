package q6_triage;

import java.util.Objects;

/**
 * Domain model representing a triage patient in an Emergency Department.
 * 
 * Severity Levels (Manchester / ESI Triage System):
 * 1 = Immediate / Resuscitation (Highest Priority)
 * 2 = Very Urgent / Emergent
 * 3 = Urgent
 * 4 = Standard / Less Urgent
 * 5 = Non-Urgent (Lowest Priority)
 * 
 * Student: Devi Vara Prasad (Reg: 192365032)
 */
public class Patient {
    private final String name;
    private int severity;
    private final long arrivalOrder;
    private int waitTimeMinutes;

    public Patient(String name, int severity, long arrivalOrder) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient name cannot be null or empty.");
        }
        if (severity < 1 || severity > 5) {
            throw new IllegalArgumentException("Severity must be between 1 (Critical) and 5 (Non-urgent).");
        }
        this.name = name.trim();
        this.severity = severity;
        this.arrivalOrder = arrivalOrder;
        this.waitTimeMinutes = 0;
    }

    public String getName() { return name; }
    public int getSeverity() { return severity; }
    public void setSeverity(int severity) { this.severity = severity; }
    public long getArrivalOrder() { return arrivalOrder; }
    public int getWaitTimeMinutes() { return waitTimeMinutes; }
    public void setWaitTimeMinutes(int waitTimeMinutes) { this.waitTimeMinutes = waitTimeMinutes; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient)) return false;
        Patient patient = (Patient) o;
        return arrivalOrder == patient.arrivalOrder && Objects.equals(name, patient.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, arrivalOrder);
    }

    /**
     * Exact required representation format: Name(S<Severity>)
     * e.g. "Meena(S1)"
     */
    @Override
    public String toString() {
        return String.format("%s(S%d)", name, severity);
    }
}
