package q4_map;

import java.util.Objects;

/**
 * Domain model representing a pharmaceutical medicine in the hospital pharmacy inventory.
 * 
 * Student: Devi Vara Prasad (Reg: 192365032)
 */
public class Medicine implements Comparable<Medicine> {
    private final String medicineCode;
    private final String name;
    private int stockQuantity;
    private final double unitPrice;
    private final String manufacturer;
    private final String category;

    public Medicine(String medicineCode, String name, int stockQuantity, double unitPrice, String manufacturer, String category) {
        this.medicineCode = medicineCode;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.unitPrice = unitPrice;
        this.manufacturer = manufacturer;
        this.category = category;
    }

    public String getMedicineCode() { return medicineCode; }
    public String getName() { return name; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public double getUnitPrice() { return unitPrice; }
    public String getManufacturer() { return manufacturer; }
    public String getCategory() { return category; }

    @Override
    public int compareTo(Medicine o) {
        return this.medicineCode.compareTo(o.medicineCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medicine)) return false;
        Medicine medicine = (Medicine) o;
        return Objects.equals(medicineCode, medicine.medicineCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(medicineCode);
    }

    @Override
    public String toString() {
        return String.format("[%s] %-20s | Qty: %5d | Price: $%-6.2f | %s",
                medicineCode, name, stockQuantity, unitPrice, manufacturer);
    }
}
