package q2_list;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Implementation of a Music Streaming "Up Next" Queue using java.util.ArrayDeque.
 * 
 * Performance & Architecture:
 * ArrayDeque is implemented as a resizable, circular array buffer with head and tail pointers.
 * - offerFirst() (Play Next / Priority Insert): O(1) amortized.
 * - offerLast() (Enqueue to back of queue):     O(1) amortized.
 * - pollFirst() (Play current track):          O(1).
 * - peekFirst() (Preview upcoming track):      O(1).
 * 
 * Unlike LinkedList, ArrayDeque creates ZERO Node wrapper objects, eliminating pointer overhead,
 * saving memory, and taking full advantage of modern CPU L1/L2 cache line prefetching.
 */
public class UpNextQueue {
    private final Deque<Track> queue;

    public UpNextQueue() {
        // Initial power-of-two circular buffer capacity
        this.queue = new ArrayDeque<>(16);
    }

    /**
     * Standard enqueue: adds a song to the end of the Up Next queue.
     */
    public void enqueueTrack(Track track) {
        queue.offerLast(track);
    }

    /**
     * VIP/User Priority Insert: forces a song to be played immediately NEXT.
     */
    public void prioritizeNext(Track track) {
        queue.offerFirst(track);
    }

    /**
     * Dequeues and returns the next track to be played.
     */
    public Track playNextTrack() {
        return queue.pollFirst();
    }

    /**
     * Inspects the next song in line without dequeuing.
     */
    public Track peekNext() {
        return queue.peekFirst();
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void displayQueue() {
        System.out.println("=== UP NEXT QUEUE (Current Tracks: " + queue.size() + ") ===");
        int rank = 1;
        for (Track t : queue) {
            System.out.printf("  %02d. %s%n", rank++, t);
        }
        System.out.println("==================================================");
    }
}
