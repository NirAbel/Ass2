package bgu.spl.a2;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;
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
                  // int currVersion=pool.getVersionMonitor().getVersion();
                  // boolean didSteal= stealTheTasks();
                   // if (!didSteal)
                     //   pool.getVersionMonitor().await(currVersion);
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

    private void stealTheTasks() throws InterruptedException {

        int numberOfProccesorToStealFrom;
        Processor proccesorToStealFrom;
        int currVersionMonitorNumber = pool.getVersionMonitor().getVersion();
        boolean isFound = false;
        boolean checkAgain = true;
//        if (id == (pool.getProcessors().length) - 1)//checks if its the last proccesor in the array
//            numberOfProccesorToStealFrom = 0;
//        else
//            numberOfProccesorToStealFrom = id + 1;
        while (checkAgain && !isFound) {
            checkAgain = false;
            if (id == (pool.getProcessors().length) - 1)//checks if its the last proccesor in the array
                numberOfProccesorToStealFrom = 0;
            else
                numberOfProccesorToStealFrom = id + 1;
            while (!isFound && numberOfProccesorToStealFrom != id) {
//                System.out.println("processor: "+id+ " is stealing from: "+numberOfProccesorToStealFrom);
//                System.out.println("processor: "+id+ " has "+tasksQueues.size()+ " tasks before");
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
//                System.out.println("processor: "+id+ " has "+tasksQueues.size()+ " tasks after");

            }
        }
        if (this.tasksQueues.size() == 0) {
            pool.getVersionMonitor().await(currVersionMonitorNumber);
        }
    }
//    }
//    private boolean stealTheTasks(){
//        boolean ans=false;
//        int idToSteal=(id+1)%pool.getProcessors().length;
//        while (!ans&&idToSteal!=id){
//            LinkedBlockingDeque<Task> queue2steal=pool.getProcessors()[idToSteal].tasksQueues;
//            synchronized (queue2steal){
//                if ((queue2steal.size()>=1)){
//                    ans=true;
//                    for(int i=0;i<(queue2steal.size())/2;i++) {
//                        Task tmp = queue2steal.pollLast();
//                        if (tmp != null)
//                            tasksQueues.addFirst(tmp);
//                    }
//                }
//                else{
//                    idToSteal=(idToSteal+1)%pool.getProcessors().length;
//                }
//            }
//        }
//        return ans;
//    }

    void addTask(Task<?> task) {
        tasksQueues.addFirst(task);
        pool.getVersionMonitor().inc();
    }

    WorkStealingThreadPool getPool() {
        return pool;
    }

    public LinkedBlockingDeque getTasks() {
        return tasksQueues;
    }

    public int getId() {
        return id;
    }


}
