package q2_list;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Handcrafted custom dynamic array implementation to demonstrate low-level array manipulation,
 * manual capacity doubling, and System.arraycopy memory block shifting.
 * 
 * Demonstrates the mechanical substrate underlying java.util.ArrayList.
 */
public class ManualArrayPlaylist implements Iterable<Track> {
    private Track[] data;
    private int size;
    private static final int INITIAL_CAPACITY = 10;

    public ManualArrayPlaylist() {
        this.data = new Track[INITIAL_CAPACITY];
        this.size = 0;
    }

    public ManualArrayPlaylist(int initialCapacity) {
        if (initialCapacity < 0) throw new IllegalArgumentException("Negative capacity: " + initialCapacity);
        this.data = new Track[initialCapacity];
        this.size = 0;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > data.length) {
            int newCapacity = data.length + (data.length >> 1); // 1.5x growth strategy
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            data = Arrays.copyOf(data, newCapacity);
        }
    }

    public void add(Track track) {
        ensureCapacity(size + 1);
        data[size++] = track;
    }

    public void add(int index, Track track) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        ensureCapacity(size + 1);
        // Shift trailing elements to make room
        int numMoved = size - index;
        if (numMoved > 0) {
            System.arraycopy(data, index, data, index + 1, numMoved);
        }
        data[index] = track;
        size++;
    }

    public Track remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Track removed = data[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(data, index + 1, data, index, numMoved);
        }
        data[--size] = null; // Clear reference to prevent memory leak
        return removed;
    }

    public Track get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return data[index];
    }

    public int size() {
        return size;
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            data[i] = null;
        }
        size = 0;
    }

    @Override
    public Iterator<Track> iterator() {
        return new Iterator<Track>() {
            private int cursor = 0;
            @Override
            public boolean hasNext() { return cursor < size; }
            @Override
            public Track next() {
                if (!hasNext()) throw new NoSuchElementException();
                return data[cursor++];
            }
        };
    }
}
