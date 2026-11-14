/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 12: Day-4 Design Challenge
 * 
 * Contract for Campus and Visitor Parking Passes.
 */
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface Pass {
    String getPassId();
    String getPassType();
    boolean isValid(LocalDateTime timestamp);
    BigDecimal applyDiscount(BigDecimal grossFee);
}
