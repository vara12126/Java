package task06_deadlock_queue;

/**
 * Demonstrates lock-ordering inversion deadlock in financial transfers.
 * 
 * When Thread 1 transfers A -> B and Thread 2 transfers B -> A:
 * Thread 1: locks A, attempts to lock B.
 * Thread 2: locks B, attempts to lock A.
 * -> Circular dependency results in Coffman Deadlock!
 */
public class DeadlockAccountTransfer {

    public static boolean executeUnsafeTransfer(Account from, Account to, double amount, long simulatedInterleavingMs) {
        System.out.println("  [" + Thread.currentThread().getName() + "] Attempting to acquire lock on Source: " + from.getId());
        synchronized (from) {
            System.out.println("  [" + Thread.currentThread().getName() + "] ACQUIRED lock on Source: " + from.getId());

            try {
                // Sleep to guarantee thread interleaving and trigger deadlock condition
                Thread.sleep(simulatedInterleavingMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }

            System.out.println("  [" + Thread.currentThread().getName() + "] Waiting to acquire lock on Destination: " + to.getId() + "...");
            synchronized (to) {
                System.out.println("  [" + Thread.currentThread().getName() + "] ACQUIRED lock on Destination: " + to.getId());
                from.debit(amount);
                to.credit(amount);
                System.out.println("  [" + Thread.currentThread().getName() + "] Transfer $" + amount + " from " + from.getId() + " to " + to.getId() + " SUCCESS.");
                return true;
            }
        }
    }
}
