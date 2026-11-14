/**
 * CSA09 - Java Fundamentals and Object-Oriented Programming
 * Task 11: Complete Smart-Parking Implementation
 */
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

public class ParkingTicket {
    private static int counter = 1000;

    private final String ticketId;
    private final String registrationNumber;
    private final String vehicleType;
    private final int floor;
    private final int slot;
    private final PassType passType;
    private final boolean evChargingUsed;
    private final LocalDateTime entryTime;

    private LocalDateTime exitTime;
    private long durationMinutes;
    private BigDecimal baseFee;
    private BigDecimal evChargingFee;
    private BigDecimal discountAmount;
    private BigDecimal netFeePaid;
    private boolean active;

    public ParkingTicket(String registrationNumber, String vehicleType, int floor, int slot,
                         PassType passType, boolean evChargingUsed, LocalDateTime entryTime) {
        this.ticketId = "TICK-" + (++counter);
        this.registrationNumber = registrationNumber;
        this.vehicleType = vehicleType;
        this.floor = floor;
        this.slot = slot;
        this.passType = passType != null ? passType : PassType.VISITOR;
        this.evChargingUsed = evChargingUsed;
        this.entryTime = entryTime != null ? entryTime : LocalDateTime.now();
        this.active = true;
        this.baseFee = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        this.evChargingFee = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        this.discountAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        this.netFeePaid = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public void processExit(LocalDateTime exitTime, double hourlyRate, double evChargingRatePerHour) {
        if (!active) {
            throw new IllegalStateException("Ticket " + ticketId + " has already been settled and closed.");
        }
        this.exitTime = exitTime != null ? exitTime : LocalDateTime.now();
        if (this.exitTime.isBefore(entryTime)) {
            throw new IllegalArgumentException("Exit time cannot precede entry time.");
        }

        this.durationMinutes = Duration.between(entryTime, this.exitTime).toMinutes();

        // Standard hourly slab: any fraction of an hour is rounded UP (ceiling policy)
        // Zero-minute parking within grace period (e.g. <= 1 min) can be billed as 0 or 1 hr
        long billableHours = (durationMinutes == 0) ? 0 : (long) Math.ceil((double) durationMinutes / 60.0);

        // Calculate using BigDecimal for financial precision
        BigDecimal rate = BigDecimal.valueOf(hourlyRate);
        BigDecimal hours = BigDecimal.valueOf(billableHours);
        this.baseFee = rate.multiply(hours).setScale(2, RoundingMode.HALF_UP);

        if (evChargingUsed && evChargingRatePerHour > 0) {
            BigDecimal evRate = BigDecimal.valueOf(evChargingRatePerHour);
            this.evChargingFee = evRate.multiply(hours).setScale(2, RoundingMode.HALF_UP);
        } else {
            this.evChargingFee = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal grossTotal = baseFee.add(evChargingFee);

        // Apply Pass Discounts
        double discountPercent = passType.getDiscountPercentage();
        if (discountPercent > 0.0) {
            BigDecimal discountFactor = BigDecimal.valueOf(discountPercent).divide(BigDecimal.valueOf(100.0), 4, RoundingMode.HALF_UP);
            this.discountAmount = baseFee.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
        } else {
            this.discountAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        this.netFeePaid = grossTotal.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
        if (this.netFeePaid.compareTo(BigDecimal.ZERO) < 0) {
            this.netFeePaid = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        this.active = false;
    }

    // Accessors
    public String getTicketId() { return ticketId; }
    public String getRegistrationNumber() { return registrationNumber; }
    public String getVehicleType() { return vehicleType; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public PassType getPassType() { return passType; }
    public boolean isEvChargingUsed() { return evChargingUsed; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public long getDurationMinutes() { return durationMinutes; }
    public BigDecimal getBaseFee() { return baseFee; }
    public BigDecimal getEvChargingFee() { return evChargingFee; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public BigDecimal getNetFeePaid() { return netFeePaid; }
    public boolean isActive() { return active; }

    @Override
    public String toString() {
        return String.format("Ticket[ID=%s | Reg=%s | Type=%s | Slot=F%d:S%d | Pass=%s | Active=%b | Net=Rs %s]",
                ticketId, registrationNumber, vehicleType, floor, slot, passType.name(), active, netFeePaid.toPlainString());
    }
}
