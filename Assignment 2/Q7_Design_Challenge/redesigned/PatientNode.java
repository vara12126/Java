package q7_design.redesigned;

import q6_triage.Patient;

/**
 * Doubly-linked node wrapping a Patient record.
 * Enables O(1) constant-time unlinking from any severity bucket
 * and O(1) splicing into an elevated priority bucket.
 * 
 * Student: Devi Vara Prasad (Reg: 192365032)
 */
public class PatientNode {
    public Patient patient;
    public int currentBucket; // 1 to 5
    public PatientNode prev;
    public PatientNode next;

    public PatientNode(Patient patient, int currentBucket) {
        this.patient = patient;
        this.currentBucket = currentBucket;
        this.prev = null;
        this.next = null;
    }
}
