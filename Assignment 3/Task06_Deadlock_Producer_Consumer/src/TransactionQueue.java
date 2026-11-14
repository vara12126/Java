package task06_deadlock_queue;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Thread-Safe Bounded Buffer using wait() and notifyAll().
 * 
 * Demonstrates:
 * 1. Synchronized monitor condition synchronization.
 * 2. Guarded suspension using 'while' loop instead of 'if' (guards against Spurious Wakeups).
 * 3. Prevention of Lost Wakeups by using notifyAll() instead of notify().
 */
public class TransactionQueue<T> {

    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;

    public TransactionQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
    }

    /**
     * Enqueues an item. Blocks if buffer is full.
     */
    public synchronized void enqueue(T item) throws InterruptedException {
        // ALWAYS use while loop to re-check condition upon waking up (Spurious Wakeup safety)
        while (queue.size() == capacity) {
            System.out.println("  [BUFFER FULL: " + queue.size() + "/" + capacity + "] Producer [" 
                    + Thread.currentThread().getName() + "] WAITING on monitor...");
            wait();
        }

        queue.add(item);
        System.out.println("  [ENQUEUE] Produced item -> Buffer count: " + queue.size() + "/" + capacity);

        // Notify waiting consumers that an item is available
        notifyAll();
    }

    /**
     * Dequeues an item. Blocks if buffer is empty.
     */
    public synchronized T dequeue() throws InterruptedException {
        while (queue.isEmpty()) {
            System.out.println("  [BUFFER EMPTY: 0/" + capacity + "] Consumer [" 
                    + Thread.currentThread().getName() + "] WAITING on monitor...");
            wait();
        }

        T item = queue.poll();
        System.out.println("  [DEQUEUE] Consumed item -> Remaining: " + queue.size() + "/" + capacity);

        // Notify waiting producers that a slot is freed
        notifyAll();
        return item;
    }

    public synchronized int size() {
        return queue.size();
    }

    public int getCapacity() {
        return capacity;
    }
}
