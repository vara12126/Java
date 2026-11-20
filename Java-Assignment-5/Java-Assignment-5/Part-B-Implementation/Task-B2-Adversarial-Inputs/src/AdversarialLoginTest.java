import java.sql.*;
import java.util.Properties;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AdversarialLoginTest {

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

    /**
     * Vulnerable login implementation using dynamic SQL string concatenation.
     */
    public static boolean unsafeLogin(Connection con, String id, String pwd) throws SQLException {
        String sql = "SELECT * FROM users WHERE id='" + id + "' AND pwd='" + pwd + "';";
        System.out.println("  [DEBUG SQL (Unsafe)]: " + sql);
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                System.out.println("  -> Logged in as: " + rs.getString("full_name") + " (Role: " + rs.getString("role") + ")");
                return true;
            }
        }
        return false;
    }

    /**
     * Secure login implementation using PreparedStatement with parameter placeholders.
     */
    public static boolean safeLogin(Connection con, String id, String pwd) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ? AND pwd = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, pwd);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("  -> Logged in as: " + rs.getString("full_name") + " (Role: " + rs.getString("role") + ")");
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   TASK B2: ADVERSARIAL INPUT & SQL INJECTION SECURITY DEMONSTRATION");
        System.out.println("================================================================================");

        try (Connection con = getConnection()) {
            // Setup DB tables and test accounts
            try (Statement s = con.createStatement()) {
                s.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                        "id VARCHAR(50) PRIMARY KEY, " +
                        "pwd VARCHAR(100) NOT NULL, " +
                        "full_name VARCHAR(100) NOT NULL, " +
                        "role VARCHAR(30) NOT NULL)");
                s.executeUpdate("DELETE FROM users");
                s.executeUpdate("INSERT INTO users VALUES ('admin', 'Secret@123', 'Campus Administrator', 'ADMIN')");
                s.executeUpdate("INSERT INTO users VALUES ('192311094', 'Dhanush@2026', 'Kattiripalli Dhanush', 'STUDENT')");
                s.executeUpdate("INSERT INTO users VALUES ('faculty01', 'Prof@Pass99', 'Dr. S. Raman', 'FACULTY')");
            }
            System.out.println("Database initialized with 3 test accounts (admin, 192311094, faculty01).\n");

            // Test Case 1: Legitimate Login
            System.out.println("--- Test Case 1: Legitimate User Login (id: 'admin', pwd: 'Secret@123') ---");
            boolean u1 = unsafeLogin(con, "admin", "Secret@123");
            boolean s1 = safeLogin(con, "admin", "Secret@123");
            System.out.println("Result -> Unsafe: " + (u1 ? "SUCCESS (Expected)" : "FAILED") + 
                               " | Safe: " + (s1 ? "SUCCESS (Expected)" : "FAILED") + "\n");

            // Test Case 2: Invalid Password
            System.out.println("--- Test Case 2: Invalid Password Login (id: 'admin', pwd: 'WrongPassword') ---");
            boolean u2 = unsafeLogin(con, "admin", "WrongPassword");
            boolean s2 = safeLogin(con, "admin", "WrongPassword");
            System.out.println("Result -> Unsafe: " + (u2 ? "SUCCESS" : "REJECTED (Expected)") + 
                               " | Safe: " + (s2 ? "SUCCESS" : "REJECTED (Expected)") + "\n");

            // Test Case 3: Adversarial Input - Boolean Tautology Injection
            System.out.println("--- Test Case 3: Adversarial SQL Injection - Boolean Tautology ---");
            String payload1_id = "admin";
            String payload1_pwd = "' OR '1'='1";
            System.out.println("Attacker Input: id = \"" + payload1_id + "\", pwd = \"" + payload1_pwd + "\"");
            boolean u3 = unsafeLogin(con, payload1_id, payload1_pwd);
            boolean s3 = safeLogin(con, payload1_id, payload1_pwd);
            System.out.println("Result -> Unsafe Login: " + (u3 ? "VULNERABLE (Bypassed without password!)" : "REJECTED") +
                               " | Safe PreparedStatement: " + (s3 ? "ACCEPTED" : "SECURE (Rejected literal)"));
            System.out.println("Interpretation: The unsafe query evaluates '1'='1' as TRUE, granting unauthorized access.");
            System.out.println("PreparedStatement treats the payload as raw password characters, safely rejecting it.\n");

            // Test Case 4: Adversarial Input - Comment Truncation Injection
            System.out.println("--- Test Case 4: Adversarial SQL Injection - Comment Truncation ---");
            String payload2_id = "admin' -- ";
            String payload2_pwd = "arbitrary_garbage_password";
            System.out.println("Attacker Input: id = \"" + payload2_id + "\", pwd = \"" + payload2_pwd + "\"");
            boolean u4 = unsafeLogin(con, payload2_id, payload2_pwd);
            boolean s4 = safeLogin(con, payload2_id, payload2_pwd);
            System.out.println("Result -> Unsafe Login: " + (u4 ? "VULNERABLE (Password check truncated!)" : "REJECTED") +
                               " | Safe PreparedStatement: " + (s4 ? "ACCEPTED" : "SECURE (Rejected literal)"));
            System.out.println("Interpretation: The SQL comment '-- ' comment-out the password clause entirely in dynamic SQL.");
            System.out.println("PreparedStatement treats '-- ' as part of the user ID string, finding no match.\n");

            // Test Case 5: Universal Tautology Bypass
            System.out.println("--- Test Case 5: Universal Database Dump Injection ---");
            String payload3_id = "' OR 1=1 -- ";
            String payload3_pwd = "dummy";
            System.out.println("Attacker Input: id = \"" + payload3_id + "\", pwd = \"" + payload3_pwd + "\"");
            boolean u5 = unsafeLogin(con, payload3_id, payload3_pwd);
            boolean s5 = safeLogin(con, payload3_id, payload3_pwd);
            System.out.println("Result -> Unsafe Login: " + (u5 ? "VULNERABLE (Logged in as first user!)" : "REJECTED") +
                               " | Safe PreparedStatement: " + (s5 ? "ACCEPTED" : "SECURE (Rejected literal)") + "\n");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
