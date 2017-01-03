package bgu.spl.a2;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * this class represents a single work stealing processor, it is
 * {@link Runnable} so it is suitable to be executed by threads.
 * <p>
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class Processor implements Runnable {

    private final WorkStealingThreadPool pool;
    private final int id;
    private final LinkedBlockingDeque<Task> tasksQueues;

    /**
     * constructor for this class
     * <p>
     * IMPORTANT:
     * 1) this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     * <p>
     * 2) you may not add other constructors to this class
     * nor you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param id   - the processor id (every processor need to have its own unique
     *             id inside its thread pool)
     * @param pool - the thread pool which owns this processor
     */
    /*package*/ Processor(int id, WorkStealingThreadPool pool) {
        this.id = id;
        this.pool = pool;
        this.tasksQueues = new LinkedBlockingDeque<>();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Task t1;
            try {
                if (tasksQueues.isEmpty()) {
                    stealTheTasks();
                } else {
                    t1 = tasksQueues.pollFirst();
                    if (t1 != null)
                        t1.handle(this);
            }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**steals half of the tasks of another processor (if available)
     *
     * @throws InterruptedException
     */
    private void stealTheTasks() throws InterruptedException {
        int numberOfProccesorToStealFrom;
        Processor proccesorToStealFrom;
        int currVersionMonitorNumber = pool.getVersionMonitor().getVersion();
        boolean isFound = false;
        boolean checkAgain = true;//boolean to check if the versionMonitor number changed
        while (checkAgain && !isFound) {//checks if we found proccesor to steal from or if the versionMonitor number changed
            checkAgain = false;
            if (id == (pool.getProcessors().length) - 1)//checks if its the last processor in the array
                numberOfProccesorToStealFrom = 0;
            else
                numberOfProccesorToStealFrom = id + 1;
            while (!isFound && numberOfProccesorToStealFrom != id) {
                currVersionMonitorNumber = pool.getVersionMonitor().getVersion();
                proccesorToStealFrom = pool.getProcessors()[numberOfProccesorToStealFrom];
                if (proccesorToStealFrom.tasksQueues.size() >= 1) {
                    isFound = true;
                    final int numberOfTasksToSteal = (proccesorToStealFrom.tasksQueues.size()) / 2;
                    for (int i = 0; i < numberOfTasksToSteal && proccesorToStealFrom.tasksQueues.size() > 1; i++) {
                        Task t = proccesorToStealFrom.tasksQueues.pollLast();
                        if (t != null) {
                            this.tasksQueues.addLast(t);
                        }
                    }
                } else {
                    if (numberOfProccesorToStealFrom == (pool.getProcessors().length) - 1) {
                        numberOfProccesorToStealFrom = 0;
                    } else
                        numberOfProccesorToStealFrom = numberOfProccesorToStealFrom + 1;
                }
                if (currVersionMonitorNumber != pool.getVersionMonitor().getVersion())
                    checkAgain = true;
            }
        }
        if (this.tasksQueues.size() == 0) {
            pool.getVersionMonitor().await(currVersionMonitorNumber);
        }
    }

    /**adds a task to the proccesor
     *
     * @param task - the given task to add
     */
    void addTask(Task<?> task) {
        tasksQueues.addFirst(task);
        pool.getVersionMonitor().inc();
    }
}
