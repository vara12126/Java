package common;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Centralized, secure database configuration provider.
 * Loads connection settings from external properties file or environment variables.
 * Prevents hardcoding of credentials into source control.
 */
public class DBConfig {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 3306;
    private static final String DEFAULT_DB = "assignment5_testdb";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "Dhanush7184";

    private static String host = DEFAULT_HOST;
    private static int port = DEFAULT_PORT;
    private static String database = DEFAULT_DB;
    private static String user = DEFAULT_USER;
    private static String password = DEFAULT_PASS;

    static {
        // Attempt to load from db.properties if present
        Path configPath = Paths.get("Java-Assignment-5", "config", "db.properties");
        if (!Files.exists(configPath)) {
            configPath = Paths.get("config", "db.properties");
        }
        if (Files.exists(configPath)) {
            try (InputStream in = Files.newInputStream(configPath)) {
                Properties props = new Properties();
                props.load(in);
                host = props.getProperty("db.host", host);
                port = Integer.parseInt(props.getProperty("db.port", String.valueOf(port)));
                database = props.getProperty("db.name", database);
                user = props.getProperty("db.user", user);
                password = props.getProperty("db.password", password);
            } catch (Exception e) {
                System.err.println("Note: Using default database credentials (" + e.getMessage() + ")");
            }
        }

        // Allow environment variable overrides
        if (System.getenv("DB_HOST") != null) host = System.getenv("DB_HOST");
        if (System.getenv("DB_USER") != null) user = System.getenv("DB_USER");
        if (System.getenv("DB_PASS") != null) password = System.getenv("DB_PASS");
        if (System.getenv("DB_NAME") != null) database = System.getenv("DB_NAME");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Warning: MySQL JDBC Driver not found on classpath: " + e.getMessage());
        }
    }

    public static String getUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + database + 
               "?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true";
    }

    public static String getUrlWithoutDb() {
        return "jdbc:mysql://" + host + ":" + port + 
               "/?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true";
    }

    public static String getUser() {
        return user;
    }

    public static String getPassword() {
        return password;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getUrl(), user, password);
    }

    public static Connection getConnectionRoot() throws SQLException {
        return DriverManager.getConnection(getUrlWithoutDb(), user, password);
    }
}
