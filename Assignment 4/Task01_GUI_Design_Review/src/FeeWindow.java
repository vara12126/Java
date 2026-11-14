package task01_fee;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;

/**
 * Redesigned Fee Payment Window.
 * 
 * Architectural Improvements:
 * 1. Strict separation of presentation (FeeWindow) and business logic (FeeService).
 * 2. Background processing via SwingWorker prevents Event Dispatch Thread (EDT) freezing.
 * 3. Individual button listeners eliminating error-prone if-else String comparisons.
 * 4. Comprehensive visual feedback via status banners and modal dialogs.
 * 5. Full handling of Receipt and Clear actions.
 * 6. Graceful window closing.
 * 
 * Student: Devi Vara Prasad | Reg: 192365032
 */
public class FeeWindow extends JFrame {

    private final JTextField txtRegNo = new JTextField(15);
    private final JTextField txtAmount = new JTextField(15);
    private final JButton btnPay = new JButton("Pay Fee");
    private final JButton btnClear = new JButton("Clear");
    private final JButton btnReceipt = new JButton("View Receipt");
    private final JLabel lblStatus = new JLabel("Enter student details and click Pay Fee.", JLabel.CENTER);
    private final JProgressBar progressBar = new JProgressBar();

    private final FeeService feeService;

    public FeeWindow(FeeService feeService) {
        super("SIMATS University - Student Fee Payment Portal");
        this.feeService = feeService;
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(480, 320);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1));
        pnlHeader.setBackground(new Color(24, 43, 73));
        JLabel lblTitle = new JLabel("STUDENT ONLINE FEE PAYMENT", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        JLabel lblSub = new JLabel("CO4: Event-Driven GUI Design | Devi Vara Prasad (192365032)", JLabel.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(200, 220, 245));
        pnlHeader.add(lblTitle);
        pnlHeader.add(lblSub);
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(pnlHeader, BorderLayout.NORTH);

        // Form Panel
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel l1 = new JLabel("Student Register No:");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlForm.add(l1, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        pnlForm.add(txtRegNo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel l2 = new JLabel("Fee Amount (₹):");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlForm.add(l2, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        pnlForm.add(txtAmount, gbc);

        // Button Row
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        btnPay.setBackground(new Color(34, 139, 34));
        btnPay.setForeground(Color.WHITE);
        btnPay.setFocusPainted(false);
        btnClear.setBackground(new Color(220, 220, 220));
        btnReceipt.setBackground(new Color(30, 144, 255));
        btnReceipt.setForeground(Color.WHITE);
        btnReceipt.setFocusPainted(false);

        pnlButtons.add(btnPay);
        pnlButtons.add(btnClear);
        pnlButtons.add(btnReceipt);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        pnlForm.add(pnlButtons, gbc);

        add(pnlForm, BorderLayout.CENTER);

        // Status & Progress Footer
        JPanel pnlFooter = new JPanel(new BorderLayout(5, 5));
        pnlFooter.setBorder(BorderFactory.createEmptyBorder(5, 10, 8, 10));
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblStatus.setForeground(new Color(50, 50, 50));
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);

        pnlFooter.add(lblStatus, BorderLayout.NORTH);
        pnlFooter.add(progressBar, BorderLayout.SOUTH);
        add(pnlFooter, BorderLayout.SOUTH);

        // Register Individual Dedicated Listeners
        registerHandlers();
    }

    private void registerHandlers() {
        // Individual Listener for Pay Button
        btnPay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePayment();
            }
        });

        // Individual Listener for Clear Button
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtRegNo.setText("");
                txtAmount.setText("");
                lblStatus.setText("Input fields cleared.");
                lblStatus.setForeground(Color.DARK_GRAY);
            }
        });

        // Individual Listener for Receipt Button
        btnReceipt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleViewReceipt();
            }
        });
    }

    private void handlePayment() {
        String reg = txtRegNo.getText();
        String amtStr = txtAmount.getText();

        try {
            // Service layer validation
            feeService.validateInput(reg, amtStr);
            double amount = Double.parseDouble(amtStr.trim());

            // Non-blocking background worker: EDT remains fluid!
            btnPay.setEnabled(false);
            btnClear.setEnabled(false);
            progressBar.setVisible(true);
            lblStatus.setText("Connecting to bank payment gateway... Please wait.");
            lblStatus.setForeground(new Color(0, 102, 204));

            SwingWorker<FeePaymentDTO, Void> worker = new SwingWorker<FeePaymentDTO, Void>() {
                @Override
                protected FeePaymentDTO doInBackground() throws Exception {
                    return feeService.processPayment(reg, amount);
                }

                @Override
                protected void done() {
                    btnPay.setEnabled(true);
                    btnClear.setEnabled(true);
                    progressBar.setVisible(false);

                    try {
                        FeePaymentDTO result = get();
                        lblStatus.setText("Payment successful! Reference: " + result.getTransactionReference());
                        lblStatus.setForeground(new Color(0, 128, 0));

                        JOptionPane.showMessageDialog(FeeWindow.this,
                                "Payment of ₹" + String.format("%.2f", result.getAmount()) + " Received Successfully!\n"
                                        + "Transaction Ref: " + result.getTransactionReference() + "\n"
                                        + "Date: " + result.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                                "Payment Confirmation", JOptionPane.INFORMATION_MESSAGE);

                    } catch (Exception ex) {
                        lblStatus.setText("Payment failed: " + ex.getMessage());
                        lblStatus.setForeground(Color.RED);
                        JOptionPane.showMessageDialog(FeeWindow.this,
                                "Transaction Error: " + ex.getMessage(),
                                "Gateway Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();

        } catch (IllegalArgumentException valEx) {
            lblStatus.setText("Validation error: " + valEx.getMessage());
            lblStatus.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, valEx.getMessage(), "Input Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleViewReceipt() {
        String reg = txtRegNo.getText();
        if (reg == null || reg.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Register Number to view receipt.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        FeePaymentDTO receipt = feeService.getLatestReceipt(reg);
        if (receipt == null) {
            JOptionPane.showMessageDialog(this, "No payment records found for Register Number: " + reg.trim(), "Record Not Found", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String receiptDetails = "========================================\n"
                + "       SIMATS UNIVERSITY FEE RECEIPT     \n"
                + "========================================\n"
                + "Student Reg No : " + receipt.getRegisterNumber() + "\n"
                + "Fee Amount Paid: ₹" + String.format("%.2f", receipt.getAmount()) + "\n"
                + "Transaction ID : " + receipt.getTransactionReference() + "\n"
                + "Payment Status : " + (receipt.isSuccessful() ? "SUCCESSFUL" : "FAILED") + "\n"
                + "Date & Time    : " + receipt.getTimestamp().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss")) + "\n"
                + "========================================\n";

        JTextArea area = new JTextArea(receiptDetails);
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Official Fee Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    public JTextField getTxtRegNo() { return txtRegNo; }
    public JTextField getTxtAmount() { return txtAmount; }
    public JButton getBtnPay() { return btnPay; }
}
