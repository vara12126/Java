package q6_triage;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Alternative Architecture: Multi-Level Discrete Priority Queue using 5 separate FIFO Queues.
 * 
 * Each severity level (S1 through S5) has its own dedicated FIFO ArrayDeque.
 * 
 * Algorithmic Advantages:
 * 1. Insert is O(1) constant time: queues[severity - 1].offer(patient).
 * 2. Extract is O(1) constant time: scan 5 fixed pointers and poll first non-empty.
 * 3. Arrival order within the same severity is GUARANTEED by FIFO deque semantics
 *    with ZERO comparator invocations or arrival counter comparisons!
 */
public class MultiLevelQueueTriage {
    @SuppressWarnings("unchecked")
    private final Queue<Patient>[] levels = new Queue[5];

    public MultiLevelQueueTriage() {
        for (int i = 0; i < 5; i++) {
            levels[i] = new ArrayDeque<>();
        }
    }

    public void addPatient(Patient patient) {
        int level = patient.getSeverity() - 1;
        levels[level].offer(patient);
    }

    public Patient treatNextPatient() {
        for (int i = 0; i < 5; i++) {
            if (!levels[i].isEmpty()) {
                return levels[i].poll();
            }
        }
        return null;
    }

    public boolean isEmpty() {
        for (int i = 0; i < 5; i++) {
            if (!levels[i].isEmpty()) return false;
        }
        return true;
    }

    public int totalWaitingPatients() {
        int count = 0;
        for (Queue<Patient> q : levels) count += q.size();
        return count;
    }
}
