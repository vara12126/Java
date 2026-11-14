package parking.service;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Service demonstrating:
 * 1. Strict boundary and input validation.
 * 2. Wrapper classes and autoboxing / unboxing (Integer, Double, Boolean).
 */
public class ValidationService {

    private static final Pattern REGEX = Pattern.compile("^[A-Z0-9]{4,12}$");

    public static String validateAndNormalizeRegistration(String reg) {
        Objects.requireNonNull(reg, "Registration cannot be null");
        String clean = reg.trim().toUpperCase().replaceAll("\\s+", "");
        if (!REGEX.matcher(clean).matches()) {
            throw new IllegalArgumentException("Registration invalid: [" + reg + "]. Allowed: 4-12 alphanumeric characters.");
        }
        return clean;
    }

    // Demonstrates Wrapper classes and Autoboxing / Unboxing
    public static Double calculateValidatedFee(Integer durationHours, Double hourlyRate, Boolean hasDiscount) {
        // Unboxing Integer -> int, Double -> double, Boolean -> boolean
        if (durationHours == null || durationHours <= 0) {
            return 0.0; // Autoboxing 0.0 -> Double
        }
        if (hourlyRate == null || hourlyRate < 0.0) {
            throw new IllegalArgumentException("Hourly rate cannot be negative");
        }

        double baseFee = durationHours * hourlyRate; // Unboxing in arithmetic operation
        if (Boolean.TRUE.equals(hasDiscount)) {
            baseFee = baseFee * 0.8; // 20% discount
        }

        return Double.valueOf(baseFee); // Explicit wrapper valueOf
    }
}
