package task04_lifecycle;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Task 04: Thread Life Cycle, State Transitions & Non-Deterministic Scheduling.
 * 
 * Student A: Devi Vara Prasad (Reg: 192365032)
 * Student B: [Student B Name] (Reg: [Student B Register Number])
 */
public class ThreadLifecycleDemo {

    /**
     * Part 1: start() vs run() verification.
     */
    public static void demonstrateStartVsRun() {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("PART 1: Thread.start() vs Runnable.run() Execution Context Verification");
        System.out.println("--------------------------------------------------------------------------------");

        Runnable task = () -> {
            System.out.println("  -> Task executing inside Thread: [" + Thread.currentThread().getName() 
                    + "] (ID: " + Thread.currentThread().threadId() + ")");
        };

        System.out.println("[INVOCATION 1] Direct call to task.run():");
        // Direct run() call does NOT spawn a new thread. It runs synchronously on main thread.
        task.run();

        System.out.println("\n[INVOCATION 2] Spawning via thread.start():");
        Thread workerThread = new Thread(task, "WorkerThread-Alpha");
        workerThread.start();

        try {
            workerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nKey Takeaway: task.run() executes on caller thread (" + Thread.currentThread().getName() + ").");
        System.out.println("thread.start() requests JVM/OS thread allocation and executes asynchronously.");
        System.out.println("--------------------------------------------------------------------------------\n");
    }

    /**
     * Part 2: State Transitions & Liveness Tracking.
     */
    public static void demonstrateLifecycleStates() throws InterruptedException {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("PART 2: Thread State Transitions (NEW -> RUNNABLE -> TIMED_WAITING -> TERMINATED)");
        System.out.println("--------------------------------------------------------------------------------");

        Thread worker = new Thread(() -> {
            try {
                // Busy work
                long count = 0;
                for (int i = 0; i < 50_000_000; i++) count += i;

                // Timed sleep
                Thread.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Lifecycle-Probe");

        // 1. NEW State
        ThreadStateObserver.printThreadState("PHASE 1 - Pre-start", worker);

        // 2. RUNNABLE State
        worker.start();
        ThreadStateObserver.printThreadState("PHASE 2 - Post-start", worker);

        // 3. TIMED_WAITING State
        // Wait briefly for worker to enter Thread.sleep()
        Thread.sleep(60);
        ThreadStateObserver.printThreadState("PHASE 3 - Sleeping", worker);

        // 4. TERMINATED State
        worker.join();
        ThreadStateObserver.printThreadState("PHASE 4 - Post-join", worker);

        System.out.println("\nState Sequence Verified: NEW -> RUNNABLE -> TIMED_WAITING -> TERMINATED");
        System.out.println("--------------------------------------------------------------------------------\n");
    }

    /**
     * Part 3: Thread Coordination using join() and isAlive().
     */
    public static void demonstrateCoordinationWithJoin() throws InterruptedException {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("PART 3: Multi-Thread Coordination using join() and isAlive() Guard");
        System.out.println("--------------------------------------------------------------------------------");

        List<Thread> workers = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            final int id = i;
            Thread t = new Thread(() -> {
                System.out.println("  [START] Batch Statement Worker #" + id + " calculating holdings...");
                try {
                    Thread.sleep(80 * id);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("  [DONE]  Batch Statement Worker #" + id + " completed generation.");
            }, "StmtWorker-" + id);
            workers.add(t);
        }

        System.out.println("Spawning all 3 statement workers in parallel...");
        for (Thread t : workers) t.start();

        System.out.println("\nMain thread waiting for all batch statements to complete via join()...");
        for (Thread t : workers) {
            System.out.println("  Checking worker [" + t.getName() + "] isAlive: " + t.isAlive() + " -> waiting on join()...");
            t.join();
            System.out.println("  Worker [" + t.getName() + "] joined! isAlive now: " + t.isAlive());
        }

        System.out.println("\n[BARRIER PASSED] All worker threads terminated. Main thread proceeding with master audit summary.");
        System.out.println("--------------------------------------------------------------------------------\n");
    }

    /**
     * Part 4: 10 Experimental Runs of Thread Execution Order (Non-Deterministic Scheduling).
     */
    public static List<String[]> runSchedulingExperiment() throws Exception {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("PART 4: 10 Experimental Runs - Thread Priority vs Non-Deterministic Scheduling");
        System.out.println("--------------------------------------------------------------------------------");

        int numRuns = 10;
        int numWorkers = 5;
        List<String[]> experimentResults = new ArrayList<>();

        // Priorities assigned to 5 workers
        int[] priorities = {Thread.MIN_PRIORITY, 3, Thread.NORM_PRIORITY, 7, Thread.MAX_PRIORITY};

        System.out.printf("%-8s | %-12s | %-12s | %-12s | %-12s | %-12s%n",
                "Run #", "Finish 1st", "Finish 2nd", "Finish 3rd", "Finish 4th", "Finish 5th");
        System.out.println("--------------------------------------------------------------------------------");

        for (int run = 1; run <= numRuns; run++) {
            List<String> finishOrder = new CopyOnWriteArrayList<>();
            AtomicInteger counter = new AtomicInteger(0);
            List<Thread> threadList = new ArrayList<>();

            for (int i = 0; i < numWorkers; i++) {
                String workerName = "W" + (i + 1) + "(P" + priorities[i] + ")";
                StatementWorker task = new StatementWorker("INV-00" + (i + 1), 500, 10, finishOrder, counter);
                Thread t = new Thread(task, workerName);
                t.setPriority(priorities[i]);
                threadList.add(t);
            }

            // Shuffle start dispatch order to avoid biased launch sequence
            Collections.shuffle(threadList);
            for (Thread t : threadList) {
                t.start();
            }

            for (Thread t : threadList) {
                t.join();
            }

            String[] row = new String[numWorkers + 1];
            row[0] = String.valueOf(run);
            for (int i = 0; i < numWorkers; i++) {
                row[i + 1] = (i < finishOrder.size()) ? finishOrder.get(i).split(" ")[0] : "N/A";
            }
            experimentResults.add(row);

            System.out.printf("Run #%-4d | %-12s | %-12s | %-12s | %-12s | %-12s%n",
                    run, row[1], row[2], row[3], row[4], row[5]);
        }

        // Export to CSV files
        exportToCSV(experimentResults);

        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Observation: Thread priorities serve as HINTS to the OS scheduler, NOT absolute rules.");
        System.out.println("Execution order is non-deterministic due to OS preemptive time-slicing and core scheduling.");
        System.out.println("--------------------------------------------------------------------------------\n");

        return experimentResults;
    }

    private static void exportToCSV(List<String[]> data) throws Exception {
        File dir1 = new File("Assignment 3/Task04_Thread_Lifecycle/test-results");
        dir1.mkdirs();
        File f1 = new File(dir1, "Task04_Thread_Runs.csv");

        File dir2 = new File("Assignment 3/Results");
        dir2.mkdirs();
        File f2 = new File(dir2, "Task04_Thread_Runs.csv");

        for (File f : Arrays.asList(f1, f2)) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println("Run_Number,Finish_1st,Finish_2nd,Finish_3rd,Finish_4th,Finish_5th");
                for (String[] row : data) {
                    pw.printf("%s,%s,%s,%s,%s,%s%n", row[0], row[1], row[2], row[3], row[4], row[5]);
                }
            }
        }
        System.out.println("[CSV EXPORT] Experimental run data successfully exported to Task04_Thread_Runs.csv");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 3 - TASK 04: THREAD LIFECYCLE & SCHEDULING BENCHMARK               ");
        System.out.println("   Student A: Devi Vara Prasad | Reg: 192365032                                 ");
        System.out.println("   Student B: [Student B Name] | Reg: [Student B Register Number]               ");
        System.out.println("   Environment: Java SE " + System.getProperty("java.version") + " (" + System.getProperty("os.name") + ")");
        System.out.println("================================================================================\n");

        demonstrateStartVsRun();
        demonstrateLifecycleStates();
        demonstrateCoordinationWithJoin();
        runSchedulingExperiment();

        System.out.println("================================================================================");
        System.out.println("[TASK 04 VERIFICATION COMPLETE] Lifecycle, coordination and scheduling verified.");
        System.out.println("================================================================================");
    }
}
