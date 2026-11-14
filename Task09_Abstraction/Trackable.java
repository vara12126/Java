/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 09: Abstraction
 * 
 * Demonstrates multiple interface implementation.
 */
public interface Trackable {
    String getTrackingTag();

    default void logMovement(String location) {
        System.out.println("[RFID Scanner] " + getTrackingTag() + " detected at " + location);
    }
}
