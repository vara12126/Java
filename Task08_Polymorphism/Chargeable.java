/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 08: Polymorphism
 * 
 * Chargeable interface defining fee calculation contracts.
 */
public interface Chargeable {
    // Runtime polymorphism method contract
    double calculateFee(int hours);

    // Compile-time polymorphism (Method Overloading)
    double calculateFee(int hours, double discountPercentage);
}
