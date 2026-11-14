package task03_library;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Campus Library Search Screen.
 * 
 * Demonstrates:
 * 1. Strategic selection of layout managers (BorderLayout + FlowLayout).
 * 2. Guaranteed resizing behavior: Results table in BorderLayout.CENTER expands
 *    to consume all available viewport surplus, while search row (NORTH) and
 *    navigation buttons (SOUTH) preserve their exact preferred heights.
 * 3. Search filtering, pagination, and book reservation.
 * 
 * Student: Devi Vara Prasad | Reg: 192365032
 */
public class LibrarySearchWindow extends JFrame {

    private final JTextField txtSearch = new JTextField(16);
    private final JComboBox<String> cmbCategory = new JComboBox<>();
    private final JButton btnSearch = new JButton("Search Catalog");
    private final JButton btnClear = new JButton("Reset");

    private final DefaultTableModel tableModel;
    private final JTable tableResults;
    private final JScrollPane scrollPane;

    private final JButton btnPrev = new JButton("< Previous");
    private final JButton btnNext = new JButton("Next >");
    private final JButton btnReserve = new JButton("Reserve Selected");
    private final JLabel lblPageInfo = new JLabel("Page 1 of 1");
    private final JLabel lblStatus = new JLabel("Enter search keywords and click Search Catalog.", JLabel.LEFT);

    private final LibraryCatalogService catalogService;
    private List<BookRecord> currentSearchResults;
    private int currentPage = 1;
    private static final int PAGE_SIZE = 5;

    public LibrarySearchWindow(LibraryCatalogService catalogService) {
        super("SIMATS University Library – Digital Public Access Catalog");
        this.catalogService = catalogService;

        // Table Model Setup
        String[] columns = {"Accession No", "Book Title", "Author", "Subject Category", "Year", "Availability"};
        this.tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        this.tableResults = new JTable(tableModel);
        this.scrollPane = new JScrollPane(tableResults);

        initUI();
        performSearch();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(740, 500);
        setMinimumSize(new Dimension(500, 350));
        setLocationRelativeTo(null);

        // Root Container: Strict BorderLayout
        setLayout(new BorderLayout(8, 8));

        // =========================================================================
        // 1. NORTH REGION: Header Title and Search Control Row
        // =========================================================================
        JPanel pnlNorth = new JPanel(new BorderLayout());

        // Header Title Banner
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1));
        pnlHeader.setBackground(new Color(36, 54, 101));
        JLabel lblTitle = new JLabel("CAMPUS DIGITAL LIBRARY SEARCH PORTAL", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(Color.WHITE);
        JLabel lblSub = new JLabel("CO4: Layout Management & Resizing | Devi Vara Prasad (192365032)", JLabel.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(220, 235, 255));
        pnlHeader.add(lblTitle);
        pnlHeader.add(lblSub);
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        pnlNorth.add(pnlHeader, BorderLayout.NORTH);

        // Search Controls Row (FlowLayout preserves component preferred sizes)
        JPanel pnlSearchRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
        pnlSearchRow.setBackground(new Color(245, 247, 250));
        pnlSearchRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JLabel lSearch = new JLabel("Keyword:");
        lSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlSearchRow.add(lSearch);
        pnlSearchRow.add(txtSearch);

        for (String cat : catalogService.getCategories()) {
            cmbCategory.addItem(cat);
        }
        pnlSearchRow.add(new JLabel("Category:"));
        pnlSearchRow.add(cmbCategory);

        btnSearch.setBackground(new Color(30, 144, 255));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnClear.setBackground(new Color(230, 230, 230));

        pnlSearchRow.add(btnSearch);
        pnlSearchRow.add(btnClear);
        pnlNorth.add(pnlSearchRow, BorderLayout.SOUTH);

        add(pnlNorth, BorderLayout.NORTH);

        // =========================================================================
        // 2. CENTER REGION: Results JTable inside JScrollPane
        //    (Takes ALL surplus vertical and horizontal space during window resizing!)
        // =========================================================================
        tableResults.setRowHeight(24);
        tableResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableResults.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableResults.getTableHeader().setBackground(new Color(230, 235, 245));

        // Center-align column values
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableResults.getColumnModel().getColumn(0).setPreferredWidth(90);
        tableResults.getColumnModel().getColumn(1).setPreferredWidth(250);
        tableResults.getColumnModel().getColumn(2).setPreferredWidth(160);
        tableResults.getColumnModel().getColumn(3).setPreferredWidth(130);
        tableResults.getColumnModel().getColumn(4).setPreferredWidth(60);
        tableResults.getColumnModel().getColumn(5).setPreferredWidth(100);

        scrollPane.setBorder(BorderFactory.createTitledBorder("Search Results Catalog (Expands Automatically on Window Resize)"));
        add(scrollPane, BorderLayout.CENTER);

        // =========================================================================
        // 3. SOUTH REGION: Pagination Controls and Status Message
        // =========================================================================
        JPanel pnlSouth = new JPanel(new BorderLayout());

        // Button Control Row
        JPanel pnlNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 6));
        pnlNav.setBackground(new Color(245, 247, 250));
        pnlNav.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        btnReserve.setBackground(new Color(34, 139, 34));
        btnReserve.setForeground(Color.WHITE);
        btnReserve.setFocusPainted(false);

        pnlNav.add(btnPrev);
        pnlNav.add(lblPageInfo);
        pnlNav.add(btnNext);
        pnlNav.add(Box.createHorizontalStrut(20));
        pnlNav.add(btnReserve);
        pnlSouth.add(pnlNav, BorderLayout.NORTH);

        // Status Bar
        JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        pnlStatus.setBackground(new Color(235, 238, 242));
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        pnlStatus.add(lblStatus);
        pnlSouth.add(pnlStatus, BorderLayout.SOUTH);

        add(pnlSouth, BorderLayout.SOUTH);

        registerHandlers();
    }

    private void registerHandlers() {
        btnSearch.addActionListener(e -> {
            currentPage = 1;
            performSearch();
        });

        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            cmbCategory.setSelectedIndex(0);
            currentPage = 1;
            performSearch();
            lblStatus.setText("Search filters cleared.");
        });

        btnPrev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                renderCurrentPage();
            }
        });

        btnNext.addActionListener(e -> {
            int totalPages = getTotalPages();
            if (currentPage < totalPages) {
                currentPage++;
                renderCurrentPage();
            }
        });

        btnReserve.addActionListener(e -> handleReservation());
    }

    private void performSearch() {
        String query = txtSearch.getText();
        String cat = (String) cmbCategory.getSelectedItem();
        currentSearchResults = catalogService.searchBooks(query, cat);
        currentPage = 1;
        renderCurrentPage();
    }

    private int getTotalPages() {
        if (currentSearchResults == null || currentSearchResults.isEmpty()) return 1;
        return (int) Math.ceil((double) currentSearchResults.size() / PAGE_SIZE);
    }

    private void renderCurrentPage() {
        tableModel.setRowCount(0);
        int totalPages = getTotalPages();

        if (currentSearchResults == null || currentSearchResults.isEmpty()) {
            lblPageInfo.setText("Page 0 of 0");
            btnPrev.setEnabled(false);
            btnNext.setEnabled(false);
            lblStatus.setText("No books matched the search criteria.");
            return;
        }

        int start = (currentPage - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, currentSearchResults.size());

        for (int i = start; i < end; i++) {
            BookRecord b = currentSearchResults.get(i);
            tableModel.addRow(new Object[]{
                b.getAccessionNumber(),
                b.getTitle(),
                b.getAuthor(),
                b.getCategory(),
                b.getYear(),
                b.isAvailable() ? "AVAILABLE" : "RESERVED"
            });
        }

        lblPageInfo.setText(String.format("Page %d of %d (Showing %d-%d of %d)",
                currentPage, totalPages, (start + 1), end, currentSearchResults.size()));
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
        lblStatus.setText(String.format("Found %d matching books across catalog.", currentSearchResults.size()));
    }

    private void handleReservation() {
        int row = tableResults.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a book row from the table to reserve.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String accNo = (String) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 1);
        String avail = (String) tableModel.getValueAt(row, 5);

        if ("RESERVED".equals(avail)) {
            JOptionPane.showMessageDialog(this, "The book '" + title + "' is already reserved by another student!", "Reservation Conflict", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = catalogService.reserveBook(accNo);
        if (success) {
            renderCurrentPage();
            lblStatus.setText("Successfully reserved: " + title + " (" + accNo + ")");
            JOptionPane.showMessageDialog(this, "Book successfully reserved for student:\n"
                    + "Title: " + title + "\nAccession No: " + accNo + "\nPlease collect from library circulation counter within 24 hours.",
                    "Reservation Confirmed", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public JTextField getTxtSearch() { return txtSearch; }
    public JComboBox<String> getCmbCategory() { return cmbCategory; }
    public JButton getBtnSearch() { return btnSearch; }
    public JTable getTableResults() { return tableResults; }
}
