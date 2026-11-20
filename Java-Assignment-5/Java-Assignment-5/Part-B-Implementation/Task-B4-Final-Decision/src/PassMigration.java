import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

public class PassMigration {

    private static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/assignment5_testdb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true";
        String user = "root";
        String pass = "Dhanush7184";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver error: " + e.getMessage());
        }
        return DriverManager.getConnection(url, user, pass);
    }

    public static class MigrationStats {
        public int totalRead = 0;
        public int validLoaded = 0;
        public int invalidRejected = 0;
        public long durationMs = 0;
        public List<String> failureReasons = new ArrayList<>();
    }

    public static MigrationStats migrate(Path csvFile, Path reportDir) throws IOException, SQLException {
        Files.createDirectories(reportDir);
        Path failureCsv = reportDir.resolve("failed_records.csv");
        Path summaryTxt = reportDir.resolve("migration_summary.txt");

        MigrationStats stats = new MigrationStats();
        long startTime = System.currentTimeMillis();

        String sql = "INSERT INTO bus_pass (pass_id, reg_no, name, route_no, amount) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = getConnection();
             BufferedReader reader = Files.newBufferedReader(csvFile, StandardCharsets.UTF_8);
             BufferedWriter failureWriter = Files.newBufferedWriter(failureCsv, StandardCharsets.UTF_8)) {

            // Ensure table exists
            try (Statement s = con.createStatement()) {
                s.executeUpdate("CREATE TABLE IF NOT EXISTS bus_pass (" +
                        "pass_id VARCHAR(20) NOT NULL PRIMARY KEY, " +
                        "reg_no VARCHAR(20) NOT NULL, " +
                        "name VARCHAR(50) NOT NULL, " +
                        "route_no INT NOT NULL, " +
                        "amount DOUBLE NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            }

            failureWriter.write("line_number,raw_record,failure_reason\n");

            boolean oldAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                String line;
                int lineNumber = 0;
                int batchCount = 0;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    stats.totalRead++;
                    String trimmed = line.trim();

                    if (trimmed.isEmpty()) {
                        stats.invalidRejected++;
                        failureWriter.write(lineNumber + ",\"[EMPTY_LINE]\",Line is blank or empty\n");
                        continue;
                    }

                    String[] parts = trimmed.split(",");
                    if (parts.length != 5) {
                        stats.invalidRejected++;
                        failureWriter.write(lineNumber + ",\"" + trimmed.replace("\"", "\"\"") + "\",Wrong number of fields (Expected 5, Got " + parts.length + ")\n");
                        continue;
                    }

                    String passId = parts[0].trim();
                    String regNo = parts[1].trim();
                    String name = parts[2].trim();
                    String routeStr = parts[3].trim();
                    String amountStr = parts[4].trim();

                    if (passId.isEmpty() || regNo.isEmpty() || name.isEmpty()) {
                        stats.invalidRejected++;
                        failureWriter.write(lineNumber + ",\"" + trimmed.replace("\"", "\"\"") + "\",Essential text attribute is empty\n");
                        continue;
                    }

                    int route;
                    try {
                        route = Integer.parseInt(routeStr);
                        if (route <= 0) throw new IllegalArgumentException("Route must be positive");
                    } catch (Exception e) {
                        stats.invalidRejected++;
                        failureWriter.write(lineNumber + ",\"" + trimmed.replace("\"", "\"\"") + "\",Invalid route number: " + routeStr + "\n");
                        continue;
                    }

                    double amount;
                    try {
                        amount = Double.parseDouble(amountStr);
                        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
                    } catch (Exception e) {
                        stats.invalidRejected++;
                        failureWriter.write(lineNumber + ",\"" + trimmed.replace("\"", "\"\"") + "\",Invalid monetary amount: " + amountStr + "\n");
                        continue;
                    }

                    // Record is valid: add to JDBC batch
                    ps.setString(1, passId);
                    ps.setString(2, regNo);
                    ps.setString(3, name);
                    ps.setInt(4, route);
                    ps.setDouble(5, amount);
                    ps.addBatch();
                    batchCount++;
                    stats.validLoaded++;

                    if (batchCount % 1000 == 0) {
                        ps.executeBatch();
                    }
                }

                if (batchCount > 0) {
                    ps.executeBatch();
                }

                con.commit();
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(oldAutoCommit);
            }

            stats.durationMs = System.currentTimeMillis() - startTime;

            // Generate detailed migration report
            StringBuilder sb = new StringBuilder();
            sb.append("================================================================================\n");
            sb.append("                 UNIVERSITY BUS-PASS DATA MIGRATION REPORT                      \n");
            sb.append("================================================================================\n");
            sb.append(String.format("Migration Date:         %s\n", new java.util.Date()));
            sb.append(String.format("Source File:            %s\n", csvFile.toAbsolutePath()));
            sb.append(String.format("Target Database:        MySQL (assignment5_testdb.bus_pass)\n"));
            sb.append(String.format("Execution Duration:     %d ms\n", stats.durationMs));
            sb.append("--------------------------------------------------------------------------------\n");
            sb.append(String.format("Total Lines Processed:  %,d\n", stats.totalRead));
            sb.append(String.format("Valid Passes Migrated:  %,d (%.2f%%)\n", stats.validLoaded, 
                    (stats.totalRead > 0 ? (stats.validLoaded * 100.0 / stats.totalRead) : 0)));
            sb.append(String.format("Invalid Lines Rejected: %,d (%.2f%%)\n", stats.invalidRejected,
                    (stats.totalRead > 0 ? (stats.invalidRejected * 100.0 / stats.totalRead) : 0)));
            sb.append("--------------------------------------------------------------------------------\n");
            sb.append(String.format("Detailed Failure Audit: %s\n", failureCsv.toAbsolutePath()));
            sb.append("Integrity Status:       TRANSACTION COMMITTED ATOMICALLY\n");
            sb.append("================================================================================\n");

            Files.writeString(summaryTxt, sb.toString(), StandardCharsets.UTF_8);
        }

        return stats;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("================================================================================");
        System.out.println("   TASK B4.1: ENTERPRISE BUS-PASS DATA MIGRATION WITH TRANSACTION AUDIT");
        System.out.println("================================================================================");

        Path reportDir = Paths.get("Java-Assignment-5/Part-B-Implementation/Task-B4-Final-Decision/migration-report");
        Path samplePassFile = Files.createTempFile("migration_input_sample", ".csv");

        // Prepare mixed input file with valid and adversarial damaged records
        try (BufferedWriter bw = Files.newBufferedWriter(samplePassFile, StandardCharsets.UTF_8)) {
            // 20 valid records
            for (int i = 1; i <= 20; i++) {
                bw.write(String.format("MIG_P%04d,192311%03d,Student_%d,%d,%.2f\n",
                        i, i, i, (1 + i % 15), 3800.0 + i * 50));
            }
            // 5 damaged/adversarial records
            bw.write("MIG_P9001,DAMAGED_RECORD_LESS_TOKENS\n"); // Wrong field count
            bw.write("MIG_P9002,192311902,Sita,ROUTE_TEN,4200.0\n"); // Non-numeric route
            bw.write("\n"); // Blank line
            bw.write("MIG_P9003,192311903,Gita,8,-500.0\n"); // Negative amount
            bw.write("MIG_P9004,192311904,Ravi,12,FREE_STUDENT\n"); // Non-numeric amount
            // 10 more valid records
            for (int i = 21; i <= 30; i++) {
                bw.write(String.format("MIG_P%04d,192311%03d,Student_%d,%d,%.2f\n",
                        i, i, i, (1 + i % 15), 3800.0 + i * 50));
            }
        }

        System.out.println("Initiating migration from source file: " + samplePassFile);
        MigrationStats stats = migrate(samplePassFile, reportDir);

        System.out.println("\nMigration Execution Complete:");
        System.out.printf("   Total Records Read:      %d\n", stats.totalRead);
        System.out.printf("   Valid Records Loaded:    %d\n", stats.validLoaded);
        System.out.printf("   Invalid Lines Rejected:  %d\n", stats.invalidRejected);
        System.out.printf("   Duration:                %d ms\n", stats.durationMs);

        Path summaryTxt = reportDir.resolve("migration_summary.txt");
        System.out.println("\nGenerated Migration Summary:\n" + Files.readString(summaryTxt));

        Files.deleteIfExists(samplePassFile);
    }
}
