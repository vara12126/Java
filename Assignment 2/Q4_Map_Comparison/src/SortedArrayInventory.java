package q4_map;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Alternative Concept Implementation: Hospital Pharmacy Inventory using a
 * sorted primitive/reference array and java.util.Arrays.binarySearch().
 * 
 * Demonstrates the classic cache-efficient, low-memory array approach and contrasts
 * its fast O(log N) lookups with its punishing O(N) insertion/deletion costs.
 */
public class SortedArrayInventory {
    private Medicine[] array;
    private int size;
    private static final Comparator<Medicine> CODE_COMPARATOR = Comparator.comparing(Medicine::getMedicineCode);

    public SortedArrayInventory(int initialCapacity) {
        this.array = new Medicine[initialCapacity];
        this.size = 0;
    }

    private void ensureCapacity(int minCap) {
        if (minCap > array.length) {
            array = Arrays.copyOf(array, array.length * 2);
        }
    }

    public void insert(Medicine med) {
        ensureCapacity(size + 1);
        int idx = Arrays.binarySearch(array, 0, size, med, CODE_COMPARATOR);
        if (idx >= 0) {
            // Update existing
            array[idx] = med;
        } else {
            int insertIdx = -(idx + 1);
            int numMoved = size - insertIdx;
            if (numMoved > 0) {
                System.arraycopy(array, insertIdx, array, insertIdx + 1, numMoved);
            }
            array[insertIdx] = med;
            size++;
        }
    }

    public Medicine search(String medicineCode) {
        Medicine key = new Medicine(medicineCode, "", 0, 0.0, "", "");
        int idx = Arrays.binarySearch(array, 0, size, key, CODE_COMPARATOR);
        return (idx >= 0) ? array[idx] : null;
    }

    public boolean updateStock(String medicineCode, int newQuantity) {
        Medicine med = search(medicineCode);
        if (med != null) {
            med.setStockQuantity(newQuantity);
            return true;
        }
        return false;
    }

    public boolean delete(String medicineCode) {
        Medicine key = new Medicine(medicineCode, "", 0, 0.0, "", "");
        int idx = Arrays.binarySearch(array, 0, size, key, CODE_COMPARATOR);
        if (idx >= 0) {
            int numMoved = size - idx - 1;
            if (numMoved > 0) {
                System.arraycopy(array, idx + 1, array, idx, numMoved);
            }
            array[--size] = null;
            return true;
        }
        return false;
    }

    public int size() {
        return size;
    }
}
