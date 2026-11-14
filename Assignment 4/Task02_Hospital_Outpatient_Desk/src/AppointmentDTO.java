package task02_hospital;

import java.time.LocalDateTime;

/**
 * Value object representing a confirmed outpatient appointment.
 * 
 * Student: Devi Vara Prasad | Reg: 192365032
 */
public class AppointmentDTO {

    private final String appointmentId;
    private final String patientId;
    private final String department;
    private final String doctorName;
    private final String timeSlot;
    private final LocalDateTime bookingTimestamp;

    public AppointmentDTO(String appointmentId, String patientId, String department, 
                          String doctorName, String timeSlot, LocalDateTime bookingTimestamp) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.department = department;
        this.doctorName = doctorName;
        this.timeSlot = timeSlot;
        this.bookingTimestamp = bookingTimestamp;
    }

    public String getAppointmentId() { return appointmentId; }
    public String getPatientId() { return patientId; }
    public String getDepartment() { return department; }
    public String getDoctorName() { return doctorName; }
    public String getTimeSlot() { return timeSlot; }
    public LocalDateTime getBookingTimestamp() { return bookingTimestamp; }
}
