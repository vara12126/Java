import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseInspector {

    private static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/assignment5_testdb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true";
        String user = "root";
        String pass = "Dhanush7184";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver error: " + e.getMessage());
        }
        return DriverManager.getConnection(url, user, pass);
    }

    public static String inspect() throws SQLException, IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("================================================================================\n");
        sb.append("           DATABASE METADATA RUNTIME INSPECTION (DatabaseMetaData)              \n");
        sb.append("================================================================================\n");

        try (Connection con = getConnection()) {
            DatabaseMetaData meta = con.getMetaData();

            sb.append("\n--- [1] DATABASE PRODUCT INFORMATION ---\n");
            sb.append(String.format("   Database Product Name:      %s\n", meta.getDatabaseProductName()));
            sb.append(String.format("   Database Product Version:   %s\n", meta.getDatabaseProductVersion()));
            sb.append(String.format("   Database Major Version:     %d\n", meta.getDatabaseMajorVersion()));
            sb.append(String.format("   Database Minor Version:     %d\n", meta.getDatabaseMinorVersion()));

            sb.append("\n--- [2] JDBC DRIVER SPECIFICATION ---\n");
            sb.append(String.format("   JDBC Driver Name:           %s\n", meta.getDriverName()));
            sb.append(String.format("   JDBC Driver Version:        %s\n", meta.getDriverVersion()));
            sb.append(String.format("   JDBC Driver Major Version:  %d\n", meta.getDriverMajorVersion()));
            sb.append(String.format("   JDBC Driver Minor Version:  %d\n", meta.getDriverMinorVersion()));
            sb.append(String.format("   JDBC Specification Compl.:  %d.%d\n", meta.getJDBCMajorVersion(), meta.getJDBCMinorVersion()));

            sb.append("\n--- [3] CONNECTION & TRANSACTION CAPABILITIES ---\n");
            sb.append(String.format("   Connection URL:             %s\n", meta.getURL()));
            sb.append(String.format("   Connected User:             %s\n", meta.getUserName()));
            sb.append(String.format("   Supports Transactions:      %s\n", meta.supportsTransactions() ? "YES" : "NO"));
            sb.append(String.format("   Supports Batch Updates:     %s\n", meta.supportsBatchUpdates() ? "YES" : "NO"));
            sb.append(String.format("   Supports Savepoints:        %s\n", meta.supportsSavepoints() ? "YES" : "NO"));
            sb.append(String.format("   Max Connections Allowed:    %d\n", meta.getMaxConnections()));

            sb.append("\n--- [4] DATABASE TABLES PRESENT IN SCHEMA 'assignment5_testdb' ---\n");
            List<String> tableNames = new ArrayList<>();
            try (ResultSet rs = meta.getTables("assignment5_testdb", null, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    String tableType = rs.getString("TABLE_TYPE");
                    tableNames.add(tableName);
                    sb.append(String.format("   [TABLE] %-20s | Type: %s\n", tableName, tableType));
                }
            }

            sb.append("\n--- [5] COLUMN DEFINITIONS & CONSTRAINTS PER TABLE ---\n");
            for (String tbl : tableNames) {
                sb.append("\n   Table: ").append(tbl).append("\n");
                try (ResultSet colRs = meta.getColumns("assignment5_testdb", null, tbl, "%")) {
                    while (colRs.next()) {
                        String colName = colRs.getString("COLUMN_NAME");
                        String typeName = colRs.getString("TYPE_NAME");
                        int colSize = colRs.getInt("COLUMN_SIZE");
                        int nullable = colRs.getInt("NULLABLE");
                        sb.append(String.format("      - Column: %-15s | Type: %-12s | Size: %-5d | Nullable: %s\n",
                                colName, typeName, colSize, (nullable == DatabaseMetaData.columnNullable ? "YES" : "NO")));
                    }
                }
            }

            sb.append("\n================================================================================\n");
            sb.append("   METADATA UTILITY EXPLANATION: WHY IS THIS VITAL DURING SERVER MIGRATIONS?    \n");
            sb.append("================================================================================\n");
            sb.append("1. Dynamic Capability Verification: Verifies whether the production database engine\n");
            sb.append("   supports ACID transactions, batch updates, and specific SQL ANSI dialects before executing.\n");
            sb.append("2. Schema Drift Detection: Automatically inspects column names, types, and constraints to ensure\n");
            sb.append("   the target server matches the application's Object-Relational schema expectations.\n");
            sb.append("3. Version Compatibility: Warns developers if deprecated JDBC features or engine version\n");
            sb.append("   mismatches exist between staging and production environments without manual DBA queries.\n");
        }

        Path outPath = Paths.get("Java-Assignment-5/Part-B-Implementation/Task-B4-Final-Decision/screenshots/metadata_inspection.txt");
        Files.createDirectories(outPath.getParent());
        Files.writeString(outPath, sb.toString());

        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        System.out.println(inspect());
    }
}
