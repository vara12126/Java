package task03_resources;

import java.sql.SQLException;

/**
 * Custom AutoCloseable resource simulating an active SQL transaction session with connection pool.
 */
public class DatabaseSession implements AutoCloseable {
    private final String sessionUrl;
    private boolean closed;
    private final boolean failOnClose;

    public DatabaseSession(String sessionUrl, boolean failOnClose) {
        this.sessionUrl = sessionUrl;
        this.closed = false;
        this.failOnClose = failOnClose;
        System.out.println("[RESOURCE OPENED] DatabaseSession: Connection to '" + sessionUrl + "' established.");
    }

    public void commitAuditBatch() throws SQLException {
        if (closed) throw new IllegalStateException("Attempted commit on closed session.");
        System.out.println("  -> Database batch transaction committed successfully.");
    }

    @Override
    public void close() throws Exception {
        if (!closed) {
            closed = true;
            System.out.println("[RESOURCE CLOSING] Closing DatabaseSession: '" + sessionUrl + "'...");
            if (failOnClose) {
                throw new Exception("Secondary Fault: Database connection pool timed out while returning connection for '" + sessionUrl + "'.");
            }
            System.out.println("[RESOURCE CLOSED] DatabaseSession: '" + sessionUrl + "' cleanly terminated.");
        }
    }
}
