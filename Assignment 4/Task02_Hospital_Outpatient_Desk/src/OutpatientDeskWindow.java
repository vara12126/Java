package task02_hospital;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Hospital Outpatient Appointment Desk Window.
 * 
 * Features:
 * 1. Dynamic doctor list refresh upon department selection.
 * 2. Complete event delegation model (ItemListener, ActionListener, WindowListener).
 * 3. Graceful window closing confirmation when booking form is populated.
 * 
 * Student: Devi Vara Prasad | Reg: 192365032
 */
public class OutpatientDeskWindow extends JFrame {

    private final JTextField txtPatientId = new JTextField(14);
    private final JComboBox<String> cmbDepartment = new JComboBox<>();
    private final JComboBox<String> cmbDoctor = new JComboBox<>();
    private final JComboBox<String> cmbTimeSlot = new JComboBox<>();
    private final JButton btnBook = new JButton("Confirm & Book");
    private final JButton btnReset = new JButton("Clear Form");
    private final JLabel lblStatus = new JLabel("Welcome to Outpatient Desk. Please enter patient details.", JLabel.CENTER);

    private final AppointmentService appointmentService;
    private boolean isBookingInProgress = false;

    public OutpatientDeskWindow(AppointmentService appointmentService) {
        super("SIMATS Multispeciality Hospital - Outpatient Appointment Desk");
        this.appointmentService = appointmentService;
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(560, 420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1));
        pnlHeader.setBackground(new Color(16, 78, 139));
        JLabel lblTitle = new JLabel("HOSPITAL OUTPATIENT APPOINTMENT DESK", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(Color.WHITE);
        JLabel lblSub = new JLabel("CO4: Event-Driven GUI Design | Devi Vara Prasad (192365032)", JLabel.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(210, 230, 255));
        pnlHeader.add(lblTitle);
        pnlHeader.add(lblSub);
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(pnlHeader, BorderLayout.NORTH);

        // Center Form Panel
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createEmptyBorder(15, 25, 10, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Patient ID
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel l1 = new JLabel("Patient ID (e.g. PAT-1042):");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlForm.add(l1, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        pnlForm.add(txtPatientId, gbc);

        // Row 1: Department
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel l2 = new JLabel("Medical Department:");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlForm.add(l2, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        pnlForm.add(cmbDepartment, gbc);

        // Row 2: Doctor (Dynamic)
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel l3 = new JLabel("Consulting Doctor:");
        l3.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlForm.add(l3, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        pnlForm.add(cmbDoctor, gbc);

        // Row 3: Time Slot
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel l4 = new JLabel("Preferred Time Slot:");
        l4.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlForm.add(l4, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        pnlForm.add(cmbTimeSlot, gbc);

        // Row 4: Buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnBook.setBackground(new Color(34, 139, 34));
        btnBook.setForeground(Color.WHITE);
        btnBook.setFocusPainted(false);
        btnReset.setBackground(new Color(230, 230, 230));

        pnlButtons.add(btnBook);
        pnlButtons.add(btnReset);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        pnlForm.add(pnlButtons, gbc);

        add(pnlForm, BorderLayout.CENTER);

        // Status Footer
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setBorder(BorderFactory.createEmptyBorder(6, 12, 8, 12));
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        pnlFooter.add(lblStatus, BorderLayout.CENTER);
        add(pnlFooter, BorderLayout.SOUTH);

        populateStaticData();
        registerEventHandlers();
    }

    private void populateStaticData() {
        // Populate Departments
        for (String dept : appointmentService.getDepartments()) {
            cmbDepartment.addItem(dept);
        }

        // Populate Time Slots
        String[] slots = {
            "09:00 AM - 09:30 AM",
            "10:00 AM - 10:30 AM",
            "11:30 AM - 12:00 PM",
            "02:00 PM - 02:30 PM",
            "03:30 PM - 04:00 PM",
            "04:30 PM - 05:00 PM"
        };
        for (String slot : slots) {
            cmbTimeSlot.addItem(slot);
        }

        // Initialize doctors for default department
        refreshDoctorList();
    }

    private void registerEventHandlers() {
        // ItemListener: Dynamic doctor list reload on department switch
        cmbDepartment.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                refreshDoctorList();
            }
        });

        // ActionListener: Booking Button
        btnBook.addActionListener(e -> handleBooking());

        // ActionListener: Reset Button
        btnReset.addActionListener(e -> resetForm());

        // WindowListener: Safe Closing with Confirmation Guard
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
    }

    /**
     * Dynamically refreshes doctor list when department changes.
     */
    public void refreshDoctorList() {
        String selectedDept = (String) cmbDepartment.getSelectedItem();
        cmbDoctor.removeAllItems();

        if (selectedDept != null) {
            List<Doctor> doctors = appointmentService.getDoctorsCached(selectedDept);
            for (Doctor doc : doctors) {
                cmbDoctor.addItem(doc.getName() + " [" + doc.getSpecialization() + "]");
            }
            lblStatus.setText("Loaded " + doctors.size() + " doctors for department: " + selectedDept);
            lblStatus.setForeground(new Color(0, 102, 204));
        }
    }

    private void handleBooking() {
        String patId = txtPatientId.getText();
        String dept = (String) cmbDepartment.getSelectedItem();
        String doc = (String) cmbDoctor.getSelectedItem();
        String slot = (String) cmbTimeSlot.getSelectedItem();

        isBookingInProgress = true;
        try {
            AppointmentDTO appt = appointmentService.bookAppointment(patId, dept, doc, slot);
            isBookingInProgress = false;

            lblStatus.setText("Appointment Confirmed! Token: " + appt.getAppointmentId());
            lblStatus.setForeground(new Color(0, 128, 0));

            JOptionPane.showMessageDialog(this,
                    "Appointment Successfully Booked!\n\n"
                            + "Appointment ID : " + appt.getAppointmentId() + "\n"
                            + "Patient ID     : " + appt.getPatientId() + "\n"
                            + "Department     : " + appt.getDepartment() + "\n"
                            + "Doctor         : " + appt.getDoctorName() + "\n"
                            + "Time Slot      : " + appt.getTimeSlot() + "\n"
                            + "Booked On      : " + appt.getBookingTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    "Booking Confirmation", JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException ex) {
            isBookingInProgress = false;
            lblStatus.setText("Error: " + ex.getMessage());
            lblStatus.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Booking Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        txtPatientId.setText("");
        if (cmbDepartment.getItemCount() > 0) cmbDepartment.setSelectedIndex(0);
        if (cmbTimeSlot.getItemCount() > 0) cmbTimeSlot.setSelectedIndex(0);
        lblStatus.setText("Form cleared. Please enter new appointment details.");
        lblStatus.setForeground(Color.DARK_GRAY);
        isBookingInProgress = false;
    }

    private void handleWindowClosing() {
        boolean hasUnsavedData = !txtPatientId.getText().trim().isEmpty() || isBookingInProgress;

        if (hasUnsavedData) {
            int option = JOptionPane.showConfirmDialog(this,
                    "An appointment booking is currently in progress or unsaved.\nAre you sure you want to close this window?",
                    "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (option == JOptionPane.YES_OPTION) {
                dispose();
            }
        } else {
            dispose();
        }
    }

    public JTextField getTxtPatientId() { return txtPatientId; }
    public JComboBox<String> getCmbDepartment() { return cmbDepartment; }
    public JComboBox<String> getCmbDoctor() { return cmbDoctor; }
    public JComboBox<String> getCmbTimeSlot() { return cmbTimeSlot; }
    public JButton getBtnBook() { return btnBook; }
}
