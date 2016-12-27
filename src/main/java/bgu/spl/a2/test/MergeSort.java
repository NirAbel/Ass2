/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.test;

import bgu.spl.a2.Task;
import bgu.spl.a2.WorkStealingThreadPool;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class MergeSort extends Task<int[]> {

    private final int[] array;

    public MergeSort(int[] array) {
        this.array = array;
    }

    @Override
    protected void start() {
//    System.out.println("Array length is " +array.length);
        if (array.length == 1) {
            complete(array);
        } else {
            List<MergeSort> tasks = new ArrayList<>();

            int[] array1 = new int[(array.length + 1) / 2];
            int[] array2 = new int[array.length / 2];
            for (int i = 0; i < array.length; i++)
                if (i < array1.length)
                    array1[i] = array[i];
                else
                    array2[i - array1.length] = array[i];

            MergeSort newTask1 = new MergeSort(array1);
            spawn(newTask1);
            tasks.add(newTask1);
            MergeSort newTask2 = new MergeSort(array2);
            spawn(newTask2);
            tasks.add(newTask2);

            whenResolved(tasks, () -> {
                int[] left = tasks.get(0).getResult().get();
                int[] right = tasks.get(1).getResult().get();

                int[] newArray = new int[left.length + right.length];

                int i = 0, j = 0, k = 0;

                while (i < left.length && j < right.length) {
                    if (left[i] < right[j]) {
                        newArray[k] = left[i];
                        i++;
                    } else {
                        newArray[k] = right[j];
                        j++;
                    }
                    k++;
                }
                while (i < left.length) {
                    newArray[k] = left[i];
                    i++;
                    k++;
                }
                while (j < right.length) {
                    newArray[k] = right[j];
                    j++;
                    k++;
                }
                complete(newArray);
            });
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int j = 0; j <= 100000; j++) {
            WorkStealingThreadPool pool = new WorkStealingThreadPool(4);
            int n = 7; //you may check on different number of elements if you like
            //int[] array = new Random().ints(n).toArray();
            int[]array = new int[n];
            for(int k=0;k<n;k++){
                array[k]=7-k;
            }

            MergeSort task = new MergeSort(array);

            CountDownLatch l = new CountDownLatch(1);
            pool.start();
            pool.submit(task);
            task.getResult().whenResolved(() -> {
                //warning - a large print!! - you can remove this line if you wish
                //System.out.println(Arrays.toString(task.getResult().get()));
                /******* debug ***********/
                boolean ans = true;
                int length = task.getResult().get().length;
                for (int i = 1; i < length; i++) {
                    if (task.getResult().get()[i] < task.getResult().get()[i - 1]) {
                        ans = false;
                        break;
                    }
                }
                System.out.println();
                System.out.println(ans);
                /*************************/
                l.countDown();
            });

            l.await();
            pool.shutdown();
        }
    }

}
