package q1_string;

/**
 * Interface representing an e-ticket document generator.
 */
public interface TicketGenerator {
    /**
     * Generates a fully rendered ticket string containing all 50 fields.
     */
    String generateTicket(ETicket ticket);

    /**
     * Name of the generator strategy for reports and benchmarks.
     */
    String getStrategyName();

    /**
     * Estimated number of intermediate heap objects created per ticket generation.
     */
    long estimateIntermediateObjectsPerTicket();
}
