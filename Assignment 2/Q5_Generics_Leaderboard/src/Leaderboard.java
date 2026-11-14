package q5_leaderboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Highly reusable generic Leaderboard component capable of ranking any competition entity T.
 * 
 * Uses bounded wildcards (PECS - Producer Extends, Consumer Super):
 * ranking(Comparator<? super T> comparator) accepts a comparator defined for T or any supertype of T.
 * 
 * Implements defensive programming: ranking() produces a brand-new sorted list without mutating
 * the original registered items collection.
 * 
 * Student: Devi Vara Prasad (Reg: 192365032)
 */
public class Leaderboard<T> {
    private final List<T> items;

    public Leaderboard() {
        this.items = new ArrayList<>();
    }

    public void add(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add null item to leaderboard.");
        }
        items.add(item);
    }

    public void addAll(List<? extends T> newItems) {
        if (newItems != null) {
            items.addAll(newItems);
        }
    }

    /**
     * Produces a ranked snapshot of the leaderboard using the supplied Comparator.
     * 
     * DEFENSIVE PROGRAMMING GUARANTEE:
     * Does NOT mutate the underlying registered items list.
     */
    public List<T> ranking(Comparator<? super T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("Comparator cannot be null.");
        }
        // Create defensive copy to preserve original registration sequence
        List<T> rankedCopy = new ArrayList<>(this.items);
        rankedCopy.sort(comparator);
        return Collections.unmodifiableList(rankedCopy);
    }

    public List<T> getRawItems() {
        return Collections.unmodifiableList(this.items);
    }

    public int size() {
        return items.size();
    }
}
