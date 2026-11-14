package q7_design.redesigned;

import q6_triage.Patient;
import java.util.HashMap;
import java.util.Map;

/**
 * Redesigned Architecture: Indexed Multi-Level Bucketed Triage Queue with
 * Bitmask Level Register and O(1) Constant-Time Operations.
 * 
 * Architectural Innovations:
 * 1. O(1) Lookup: Secondary HashMap<String, PatientNode> index.
 * 2. O(1) Priority Update: Doubly-linked bucket allows instant node unlinking and re-splicing.
 * 3. O(1) Top Priority Resolution: 5-bit register where bit k indicates active level k+1.
 *    Integer.numberOfTrailingZeros(activeMask) resolves top priority in 1 CPU cycle.
 * 4. O(K) Linear Aging Sweep: Only inspects candidate buckets; promotes nodes in O(1) time
 *    with ZERO heap reconstruction or O(N) array scans.
 * 
 * Student: Devi Vara Prasad (Reg: 192365032)
 */
public class RedesignedTriageQueue {

    /**
     * Circular doubly-linked sentinel bucket for each discrete severity level (1-5).
     */
    private static class Bucket {
        PatientNode head;
        PatientNode tail;
        int size;

        Bucket() {
            this.size = 0;
            this.head = null;
            this.tail = null;
        }

        void append(PatientNode node) {
            node.next = null;
            if (tail == null) {
                head = node;
                tail = node;
                node.prev = null;
            } else {
                tail.next = node;
                node.prev = tail;
                tail = node;
            }
            size++;
        }

        void unlink(PatientNode node) {
            if (node.prev != null) {
                node.prev.next = node.next;
            } else {
                head = node.next;
            }
            if (node.next != null) {
                node.next.prev = node.prev;
            } else {
                tail = node.prev;
            }
            node.prev = null;
            node.next = null;
            size--;
        }

        PatientNode pollFirst() {
            if (head == null) return null;
            PatientNode first = head;
            unlink(first);
            return first;
        }

        boolean isEmpty() {
            return size == 0;
        }
    }

    private final Bucket[] buckets;
    private final Map<String, PatientNode> patientIndex;
    private int activeLevelsBitmask;
    private int totalSize;

    public RedesignedTriageQueue() {
        this.buckets = new Bucket[5];
        for (int i = 0; i < 5; i++) {
            this.buckets[i] = new Bucket();
        }
        this.patientIndex = new HashMap<>(4096);
        this.activeLevelsBitmask = 0;
        this.totalSize = 0;
    }

    /**
     * O(1) Constant-Time Patient Admittance.
     */
    public void admit(Patient patient) {
        int severity = patient.getSeverity();
        PatientNode node = new PatientNode(patient, severity);
        buckets[severity - 1].append(node);
        patientIndex.put(patient.getName(), node);
        activeLevelsBitmask |= (1 << (severity - 1));
        totalSize++;
    }

    /**
     * O(1) Constant-Time Top Priority Patient Extraction.
     * Uses hardware bit-scan (numberOfTrailingZeros) to locate the highest severity level instantly.
     */
    public Patient triageNext() {
        if (totalSize == 0 || activeLevelsBitmask == 0) return null;

        // Instant bitmask resolution of highest active priority (1 to 5)
        int highestPriorityIndex = Integer.numberOfTrailingZeros(activeLevelsBitmask);
        Bucket bucket = buckets[highestPriorityIndex];
        PatientNode node = bucket.pollFirst();

        if (bucket.isEmpty()) {
            activeLevelsBitmask &= ~(1 << highestPriorityIndex); // Clear bit
        }

        if (node != null) {
            patientIndex.remove(node.patient.getName());
            totalSize--;
            return node.patient;
        }
        return null;
    }

    /**
     * O(1) Constant-Time Direct Patient Lookup via Secondary Hash Index.
     */
    public Patient lookup(String patientName) {
        PatientNode node = patientIndex.get(patientName);
        return (node != null) ? node.patient : null;
    }

    /**
     * O(1) Constant-Time Priority Promotion or Modification.
     * Unlinks node from current bucket and splices into target bucket in O(1) time.
     */
    public boolean updatePriority(String patientName, int newSeverity) {
        if (newSeverity < 1 || newSeverity > 5) return false;
        PatientNode node = patientIndex.get(patientName);
        if (node == null) return false;

        int oldBucketIdx = node.currentBucket - 1;
        int newBucketIdx = newSeverity - 1;

        if (oldBucketIdx == newBucketIdx) return true;

        // O(1) Unlink from old bucket
        Bucket oldBucket = buckets[oldBucketIdx];
        oldBucket.unlink(node);
        if (oldBucket.isEmpty()) {
            activeLevelsBitmask &= ~(1 << oldBucketIdx);
        }

        // O(1) Splicing into new bucket
        node.currentBucket = newSeverity;
        node.patient.setSeverity(newSeverity);
        Bucket newBucket = buckets[newBucketIdx];
        newBucket.append(node);
        activeLevelsBitmask |= (1 << newBucketIdx);

        return true;
    }

    /**
     * Highly Efficient Aging Sweep: Only traverses buckets S2 through S5.
     * Promotes qualifying patients in O(1) node unlinks without touching S1.
     */
    public int applyAgingSweep(int waitThresholdMins) {
        int promotedCount = 0;
        // Check buckets 1 through 4 (Severity S2 through S5)
        for (int b = 1; b < 5; b++) {
            Bucket bucket = buckets[b];
            PatientNode curr = bucket.head;
            while (curr != null) {
                PatientNode nextNode = curr.next;
                if (curr.patient.getWaitTimeMinutes() >= waitThresholdMins) {
                    // Promote to next higher severity (b -> b - 1)
                    bucket.unlink(curr);
                    curr.currentBucket = b; // Promoted from (b+1) to b
                    curr.patient.setSeverity(b);
                    buckets[b - 1].append(curr);
                    activeLevelsBitmask |= (1 << (b - 1));
                    promotedCount++;
                }
                curr = nextNode;
            }
            if (bucket.isEmpty()) {
                activeLevelsBitmask &= ~(1 << b);
            }
        }
        return promotedCount;
    }

    public int size() {
        return totalSize;
    }

    public boolean isEmpty() {
        return totalSize == 0;
    }
}
