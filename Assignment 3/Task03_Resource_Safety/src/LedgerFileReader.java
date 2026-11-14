package task03_resources;

import java.io.IOException;

/**
 * Custom AutoCloseable resource simulating a file reader for financial transaction ledgers.
 * 
 * Student A: Devi Vara Prasad (Reg: 192365032)
 * Student B: [Student B Name] (Reg: [Student B Register Number])
 */
public class LedgerFileReader implements AutoCloseable {
    private final String filename;
    private boolean closed;
    private final boolean failOnClose;

    public LedgerFileReader(String filename, boolean failOnClose) {
        this.filename = filename;
        this.closed = false;
        this.failOnClose = failOnClose;
        System.out.println("[RESOURCE OPENED] LedgerFileReader: '" + filename + "' opened.");
    }

    public void readLedgerRecord(boolean simulateCorruptedData) throws IOException {
        if (closed) throw new IllegalStateException("Attempted read from closed reader.");
        if (simulateCorruptedData) {
            throw new IOException("Primary Fault: Ledger file '" + filename + "' contains corrupted checksum at line 402.");
        }
        System.out.println("  -> Successfully read 1,500 audit transaction records from '" + filename + "'.");
    }

    @Override
    public void close() throws Exception {
        if (!closed) {
            closed = true;
            System.out.println("[RESOURCE CLOSING] Closing LedgerFileReader: '" + filename + "'...");
            if (failOnClose) {
                throw new Exception("Secondary Fault: Disk I/O error occurred while flushing file buffers for '" + filename + "'.");
            }
            System.out.println("[RESOURCE CLOSED] LedgerFileReader: '" + filename + "' safely closed.");
        }
    }
}
