/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 09: Abstraction
 * 
 * Interface demonstrating:
 * 1. Abstract method.
 * 2. Default method (Java 8+).
 * 3. Static interface method.
 */
public interface Chargeable {

    // 1. Abstract method contract
    double calculateFee(int hours);

    // 2. Default method providing fallback implementation
    default double calculateDiscountedFee(int hours, double discountPercent) {
        if (discountPercent < 0.0 || discountPercent > 100.0) {
            throw new IllegalArgumentException("Invalid discount percentage: " + discountPercent);
        }
        double baseFee = calculateFee(hours);
        return baseFee - (baseFee * (discountPercent / 100.0));
    }

    // 3. Static utility method in interface
    static String getCurrencySymbol() {
        return "Rs";
    }

    static void printRateCardHeader() {
        System.out.println("=== CAMPUS SMART PARKING OFFICIAL RATE CARD (" + getCurrencySymbol() + ") ===");
    }
}
