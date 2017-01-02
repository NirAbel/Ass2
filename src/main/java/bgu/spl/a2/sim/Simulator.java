package bgu.spl.a2.sim;

import bgu.spl.a2.WorkStealingThreadPool;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.json.Series;
import bgu.spl.a2.sim.tasks.ManTask;
import bgu.spl.a2.sim.tools.GcdScrewDriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;
import com.google.gson.Gson;

import java.io.*;

import java.util.ArrayList;
import java.util.List;
import bgu.spl.a2.sim.json.Wave;
import bgu.spl.a2.sim.json.Order;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;


/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {

	private static WorkStealingThreadPool workStealingThreadPool;
	private static List<Wave> waves;
	private static Warehouse warehouse;

	/**
	 * Begin the simulation
	 * Should not be called before attachWorkStealingThreadPool()
	 */
	public static ConcurrentLinkedQueue<Product> start() {

		ConcurrentLinkedQueue<Product> manufacturedProducts = new ConcurrentLinkedQueue<>();

		try {
			workStealingThreadPool.start();
			for (Wave currWave : waves) {
				List<Product> productsInWave = getWaveProducts(currWave);
				CountDownLatch l = new CountDownLatch(productsInWave.size());
				for (Product currProduct : productsInWave) {
					ManTask currTask = new ManTask(currProduct, warehouse);
					Simulator.workStealingThreadPool.submit(currTask);
					currTask.getResult().whenResolved(l::countDown);
				}
				l.await();
				manufacturedProducts.addAll(productsInWave.stream().collect(Collectors.toList()));
			}
			workStealingThreadPool.shutdown();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return manufacturedProducts;
	}

	private static List<Product> getWaveProducts(Wave wave)
	{
		List<Product> productList = new ArrayList<>();

		for (Order order : wave.getOrders())
		{
			for (int i = 0; i < order.getQty(); i++) {
				productList.add(new Product(order.getStartId() + i, order.getProduct()));
			}
		}

		return productList;
	}

	/**
	 * attach a WorkStealingThreadPool to the Simulator, this WorkStealingThreadPool will be used to run the simulation
	 *
	 * @param myWorkStealingThreadPool - the WorkStealingThreadPool which will be used by the simulator
	 */
	public static void attachWorkStealingThreadPool(WorkStealingThreadPool myWorkStealingThreadPool) {
		workStealingThreadPool = myWorkStealingThreadPool;
	}

	private static void addTool(String toolType, int qty) {
		if (toolType.equals("gs-driver")) {
			GcdScrewDriver tool = new GcdScrewDriver();
			warehouse.addTool(tool, qty);
		} else if (toolType.equals("np-hammer")) {
			NextPrimeHammer tool = new NextPrimeHammer();
			warehouse.addTool(tool, qty);
		} else if (toolType.equals("rs-pliers")) {
			RandomSumPliers tool = new RandomSumPliers();
			warehouse.addTool(tool, qty);
		}
	}

	private static void Series(Series SeriesObj) {

		//create new WorkStealingThreadPool
		WorkStealingThreadPool workStealingThreadPoolTmp = new WorkStealingThreadPool(SeriesObj.getThreads());
		Simulator.attachWorkStealingThreadPool(workStealingThreadPoolTmp);

		warehouse = new Warehouse();
		waves = new ArrayList<>();
		//this func is adding tool to wareHouse
		for (int i = 0; i < SeriesObj.getTools().size(); i++) {
			addTool(SeriesObj.getTools().get(i).getTool(), SeriesObj.getTools().get(i).getQty());
		}

		//this func is adding plan to wareHouse
		for (int i = 0; i < SeriesObj.getPlans().size(); i++) {
			addPlan(SeriesObj, i);
		}

		//this func is adding another Wave to Wave List
		Wave wave;
		int size = SeriesObj.getWaves().size();
		for (int i = 0; i < size; i++) {
			wave = new Wave(SeriesObj.getWaves().get(i));
			waves.add(wave);
		}
	}

	//this func is adding plan to wareHouse
	private static void addPlan(Series obj, int i) {
		ManufactoringPlan plan;
		String product = obj.getPlans().get(i).getProduct();
		String[] parts = obj.getPlans().get(i).getParts();
		String[] tools = obj.getPlans().get(i).getTools();
		plan = new ManufactoringPlan(product, parts, tools);
		warehouse.addPlan(plan);
	}

	public static void main(String[] args) {
		try
		{
			String jasonFile = args[0];
			Gson newGson = new Gson();
			Gson newGson1 = new Gson();

			BufferedReader br = new BufferedReader(new FileReader(jasonFile));
			Series obj = newGson.fromJson(br, Series.class);
			Series(obj);

			ConcurrentLinkedQueue<Product> simulationResult;
			simulationResult = Simulator.start();
			FileOutputStream stream = new FileOutputStream("result.ser");
			ObjectOutputStream oos = new ObjectOutputStream(stream);
			oos.writeObject(simulationResult);
			oos.close();
			br.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}