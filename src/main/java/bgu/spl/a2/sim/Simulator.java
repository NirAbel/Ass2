/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import bgu.spl.a2.WorkStealingThreadPool;

import java.util.concurrent.ConcurrentLinkedQueue;
import bgu.spl.a2.sim.tools.GcdScrewDriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;
import bgu.spl.a2.sim.tools.Tool;
import bgu.spl.a2.Deferred;
import com.google.gson.Gson;
import java.io.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;




/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {

	static WorkStealingThreadPool poolTask;
	static Object locked;
	static Warehouse warehouse;
	static AtomicInteger complete;
	static GsonReader input;
	static LinkedList<Deferred<Product>> Products;
	/**
	* Begin the simulation
	* Should not be called before attachWorkStealingThreadPool()
	*/
	public static ConcurrentLinkedQueue<Product> start(){
		locked = new Object();
		poolTask.start();
		Products = new LinkedList<>();
		int qty = 0;
		complete = new AtomicInteger(0);
		for (GsonReader.Zerg[] wave: input.waves) {
			for (GsonReader.Zerg zerg : wave) {
				for (int i = 0; i < zerg.qty; i++) {
					BuildProductTask task = new BuildProductTask(zerg.startId + i, zerg.product);
					poolTask.submit(task);
					task.getResult().whenResolved(() -> reportFinished()); // counts how many zergs are done
					Products.add(task.getResult()); // save a deferred object for the product
					qty++;
				}
			}
			Wave(qty);
		}

		// collect the deferred to a queue of products
		ConcurrentLinkedQueue<Product> products = new ConcurrentLinkedQueue<>();
		for(Deferred<Product> dProd : Products) {
			products.add(dProd.get());
		}
		return products;
	}

	private static void Wave(int number) {
		synchronized(locked) {
			try {
				while(complete.get() < number)
					locked.wait();
			} catch (InterruptedException e){
				System.exit(1);
			}
		}
	}
	
	/**
	* attach a WorkStealingThreadPool to the Simulator, this WorkStealingThreadPool will be used to run the simulation
	* @param myWorkStealingThreadPool - the WorkStealingThreadPool which will be used by the simulator
	*/
	public static void attachWorkStealingThreadPool(WorkStealingThreadPool myWorkStealingThreadPool){
		poolTask = myWorkStealingThreadPool;
	}
	
	public static int main(String [] args);
}
