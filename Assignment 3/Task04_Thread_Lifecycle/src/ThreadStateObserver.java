package task04_lifecycle;

/**
 * Utility to sample and display thread state transitions and liveness.
 */
public class ThreadStateObserver {

    public static void printThreadState(String tag, Thread t) {
        System.out.printf("[%s] Thread: '%-16s' | State: %-14s | isAlive: %-5s | Priority: %2d%n",
                tag, t.getName(), t.getState(), t.isAlive(), t.getPriority());
    }
}
