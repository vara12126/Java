/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 12: Day-4 Design Challenge
 * 
 * Delivery Van entity governed by DeliveryVanTimeWindowRule.
 */
public class DeliveryVan extends Vehicle {

    public DeliveryVan(String registrationNumber) {
        super(registrationNumber, "DeliveryVan", new DeliveryVanTimeWindowRule());
    }

    public DeliveryVan(String registrationNumber, DeliveryVanTimeWindowRule customRule) {
        super(registrationNumber, "DeliveryVan", customRule);
    }

    @Override
    public double getBaseHourlyRate() {
        return 30.0; // Standard car rate base
    }
}
