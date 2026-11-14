package task06_deadlock_queue;

/**
 * Thread-Safe transfer mechanism using Deterministic Global Lock Ordering.
 * 
 * Breaks the "Circular Wait" condition of the 4 Coffman Conditions:
 * 1. Mutual Exclusion (Required for state safety)
 * 2. Hold and Wait (Acquiring multiple locks)
 * 3. No Preemption (Java synchronized monitors cannot be preempted)
 * 4. Circular Wait -> BROKEN via consistent alphanumeric lock ordering!
 */
public class SafeAccountTransfer {

    public static boolean executeSafeTransfer(Account from, Account to, double amount) {
        if (from == to || from.getId().equals(to.getId())) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }

        // Establish strict global ordering based on immutable Account ID
        Account firstLock;
        Account secondLock;

        if (from.getId().compareTo(to.getId()) < 0) {
            firstLock = from;
            secondLock = to;
        } else {
            firstLock = to;
            secondLock = from;
        }

        System.out.println("  [" + Thread.currentThread().getName() + "] DETERMINISTIC ORDER: Locking " 
                + firstLock.getId() + " first, then " + secondLock.getId());

        synchronized (firstLock) {
            System.out.println("  [" + Thread.currentThread().getName() + "] Acquired 1st lock: " + firstLock.getId());
            synchronized (secondLock) {
                System.out.println("  [" + Thread.currentThread().getName() + "] Acquired 2nd lock: " + secondLock.getId());

                from.debit(amount);
                to.credit(amount);

                System.out.println("  [" + Thread.currentThread().getName() + "] Safe Transfer $" + amount 
                        + " (" + from.getId() + " -> " + to.getId() + ") COMPLETED.");
                return true;
            }
        }
    }
}
