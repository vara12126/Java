/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 11: Complete Smart-Parking Implementation
 */
public enum PassType {
    VISITOR("Visitor (No Pass)", 0.0),
    STUDENT_MONTHLY("Student Monthly Pass (50% Off)", 50.0),
    STAFF_MONTHLY("Staff Monthly Pass (100% Off)", 100.0),
    VISITOR_ONE_DAY("Visitor One-Day Pass (100% Off Hourly)", 100.0);

    private final String description;
    private final double discountPercentage;

    PassType(String description, double discountPercentage) {
        this.description = description;
        this.discountPercentage = discountPercentage;
    }

    public String getDescription() { return description; }
    public double getDiscountPercentage() { return discountPercentage; }
}
