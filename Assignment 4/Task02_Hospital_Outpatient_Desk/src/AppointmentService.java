package task02_hospital;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service managing outpatient appointments and doctor directory.
 * 
 * Demonstrates:
 * 1. Fast in-memory caching via Map<String, List<Doctor>> for O(1) department lookups.
 * 2. Alternative database simulation for complexity benchmark.
 * 
 * Student: Devi Vara Prasad | Reg: 192365032
 */
public class AppointmentService {

    private final Map<String, List<Doctor>> doctorCatalog = new LinkedHashMap<>();
    private final Map<String, AppointmentDTO> confirmedBookings = new ConcurrentHashMap<>();

    public AppointmentService() {
        seedDoctorCatalog();
    }

    private void seedDoctorCatalog() {
        doctorCatalog.put("Cardiology", Arrays.asList(
            new Doctor("DOC-C1", "Dr. A. R. Sharma", "Cardiology", "Interventional Cardiologist"),
            new Doctor("DOC-C2", "Dr. Priya Sundaram", "Cardiology", "Electrophysiologist")
        ));

        doctorCatalog.put("Neurology", Arrays.asList(
            new Doctor("DOC-N1", "Dr. Rajesh Venkat", "Neurology", "Neurosurgeon"),
            new Doctor("DOC-N2", "Dr. Sneha Nair", "Neurology", "Pediatric Neurologist")
        ));

        doctorCatalog.put("Orthopedics", Arrays.asList(
            new Doctor("DOC-O1", "Dr. Vikram Seth", "Orthopedics", "Joint Replacement Specialist"),
            new Doctor("DOC-O2", "Dr. Ananya Reddy", "Orthopedics", "Spine Surgeon")
        ));

        doctorCatalog.put("Pediatrics", Arrays.asList(
            new Doctor("DOC-P1", "Dr. Meera Krishnan", "Pediatrics", "Neonatologist"),
            new Doctor("DOC-P2", "Dr. Karthik Rao", "Pediatrics", "Child Specialist")
        ));

        doctorCatalog.put("General Medicine", Arrays.asList(
            new Doctor("DOC-G1", "Dr. Sunita Patel", "General Medicine", "Consultant Physician"),
            new Doctor("DOC-G2", "Dr. Harish Kumar", "General Medicine", "Internal Medicine")
        ));
    }

    public List<String> getDepartments() {
        return new ArrayList<>(doctorCatalog.keySet());
    }

    /**
     * In-Memory O(1) retrieval of doctors for a department.
     */
    public List<Doctor> getDoctorsCached(String department) {
        return doctorCatalog.getOrDefault(department, Collections.emptyList());
    }

    /**
     * Alternative Concept: Simulates querying database over network for comparison.
     */
    public List<Doctor> getDoctorsFromDatabaseSimulated(String department) throws Exception {
        // Simulating network round-trip + SQL query execution latency
        Thread.sleep(45);
        return getDoctorsCached(department);
    }

    /**
     * Validates and registers an appointment.
     */
    public AppointmentDTO bookAppointment(String patientId, String department, String doctorName, String timeSlot) throws IllegalArgumentException {
        if (patientId == null || patientId.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient ID is required.");
        }
        if (!patientId.trim().matches("^PAT-\\d{4,8}$")) {
            throw new IllegalArgumentException("Patient ID must follow format 'PAT-XXXX' (e.g. PAT-1042).");
        }
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Please select a valid medical department.");
        }
        if (doctorName == null || doctorName.trim().isEmpty() || doctorName.startsWith("--")) {
            throw new IllegalArgumentException("Please select an available doctor.");
        }
        if (timeSlot == null || timeSlot.trim().isEmpty()) {
            throw new IllegalArgumentException("Please select an appointment time slot.");
        }

        String slotKey = doctorName + "@" + timeSlot;
        if (confirmedBookings.containsKey(slotKey)) {
            throw new IllegalArgumentException("Time slot '" + timeSlot + "' with " + doctorName + " is already booked! Please select another slot.");
        }

        String apptId = "APT-" + System.currentTimeMillis() % 100000;
        AppointmentDTO appt = new AppointmentDTO(apptId, patientId.trim(), department, doctorName, timeSlot, LocalDateTime.now());
        confirmedBookings.put(slotKey, appt);

        return appt;
    }
}
