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
    private final int[] arr;
    public MergeSort(int[] array) {
        this.arr = array;
    }

    @Override
    protected void start() {
        if (arr.length == 1) {
            complete(arr);
        } else {
            int size1=(arr.length + 1) / 2;
            int size2=arr.length / 2;
            int[] arr1 = new int[size1];
            int[] arr2 = new int[size2];
            List<MergeSort> tasks = new ArrayList<>();
            int count=0;
            while(count<arr.length){
                if (count < arr1.length)
                    arr1[count] = arr[count];
                else
                    arr2[count - arr1.length] = arr[count];
                count++;
            }
            MergeSort newTask1 = new MergeSort(arr1);
            spawn(newTask1);
            tasks.add(newTask1);
            MergeSort newTask2 = new MergeSort(arr2);
            spawn(newTask2);
            tasks.add(newTask2);

            whenResolved(tasks, () -> {
                int[] left = tasks.get(0).getResult().get();
                int[] right = tasks.get(1).getResult().get();

                int[] newArr = new int[left.length + right.length];

                int i = 0, j = 0, k = 0;
                int leftLength=left.length;
                int rightLength=right.length;
                while (i < leftLength && j < rightLength) {
                    if (left[i] < right[j]) {
                        newArr[k] = left[i];
                        i++;
                    } else {
                        newArr[k] = right[j];
                        j++;
                    }
                    k++;
                }
                while (i <leftLength) {
                    newArr[k] = left[i];
                    i++;
                    k++;
                }
                while (j <rightLength) {
                    newArr[k] = right[j];
                    j++;
                    k++;
                }
                complete(newArr);
            });
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int j = 0; j <1000; j++) {
            int n = 100000;
            int[] arr = new Random().ints(n).toArray();
            WorkStealingThreadPool pool = new WorkStealingThreadPool(4);
            MergeSort task = new MergeSort(arr);
            CountDownLatch l = new CountDownLatch(1);
            pool.start();
            pool.submit(task);
            task.getResult().whenResolved(() -> {
                boolean ans = true;
                int length = task.getResult().get().length;
                int count=1;
                while(count<length){
                    if (task.getResult().get()[count] < task.getResult().get()[count - 1]) {
                        ans = false;
                        break;
                    }
                    count++;
                }
                System.out.println(ans);
                l.countDown();
            });

            l.await();
            pool.shutdown();
        }
    }
}