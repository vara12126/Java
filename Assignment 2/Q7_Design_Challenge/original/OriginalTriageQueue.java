package q7_design.original;

import q6_triage.Patient;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Original Architecture: Standard java.util.PriorityQueue-based Triage System.
 * 
 * Bottleneck Diagnosis:
 * 1. Arbitrary patient lookup by ID requires O(N) linear search through the binary heap.
 * 2. Updating priority or promoting patients requires queue.remove(p) (O(N) search)
 *    followed by queue.offer(p) (O(log N) sift-up).
 * 3. Applying an aging sweep across N waiting patients results in catastrophic O(N^2)
 *    execution time and massive CPU thrashing during hospital surge conditions.
 * 
 * Student: Devi Vara Prasad (Reg: 192365032)
 */
public class OriginalTriageQueue {
    private final PriorityQueue<Patient> queue;

    public OriginalTriageQueue() {
        Comparator<Patient> comp = Comparator
                .comparingInt(Patient::getSeverity)
                .thenComparingLong(Patient::getArrivalOrder);
        this.queue = new PriorityQueue<>(comp);
    }

    public void admit(Patient patient) {
        queue.offer(patient);
    }

    public Patient triageNext() {
        return queue.poll();
    }

    /**
     * BOTTLENECK 1: Linear O(N) search through heap array.
     */
    public Patient lookup(String patientName) {
        for (Patient p : queue) {
            if (p.getName().equals(patientName)) {
                return p;
            }
        }
        return null;
    }

    /**
     * BOTTLENECK 2: Priority update requires O(N) search + remove and O(log N) re-insertion.
     */
    public boolean updatePriority(String patientName, int newSeverity) {
        Patient target = lookup(patientName);
        if (target != null) {
            // Must remove from heap to re-bubble with new priority
            queue.remove(target); // O(N) linear search in heap array
            target.setSeverity(newSeverity);
            queue.offer(target);  // O(log N) sift-up
            return true;
        }
        return false;
    }

    /**
     * BOTTLENECK 3: Aging sweep requires evaluating all N patients.
     * Every promoted patient triggers O(N) remove + O(log N) re-insert,
     * causing catastrophic O(N^2) complexity.
     */
    public int applyAgingSweep(int waitThresholdMins) {
        int promotedCount = 0;
        // In PriorityQueue, modifying in-place invalidates heap invariants.
        // Therefore, we must drain or remove and re-insert.
        Patient[] snapshot = queue.toArray(new Patient[0]);
        for (Patient p : snapshot) {
            if (p.getWaitTimeMinutes() >= waitThresholdMins && p.getSeverity() > 1) {
                queue.remove(p); // O(N)
                p.setSeverity(p.getSeverity() - 1);
                queue.offer(p);  // O(log N)
                promotedCount++;
            }
        }
        return promotedCount;
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
