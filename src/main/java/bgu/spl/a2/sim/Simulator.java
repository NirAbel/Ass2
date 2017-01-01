package bgu.spl.a2.sim;

import bgu.spl.a2.WorkStealingThreadPool;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tasks.ManTask;
import bgu.spl.a2.sim.tools.GcdScrewDriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;
import com.google.gson.Gson;

import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;


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
				for (Product product : productsInWave)
					manufacturedProducts.add(product);
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

	private static void parseData(ParseData parseDataObj) {

		//create WorkStealingThreadPool
		WorkStealingThreadPool workStealingThreadPoolTmp = new WorkStealingThreadPool(parseDataObj.getThreads());
		Simulator.attachWorkStealingThreadPool(workStealingThreadPoolTmp);

		warehouse = new Warehouse();
		waves = new ArrayList<>();
		//add tool to Warehouse
		for (int i = 0; i < parseDataObj.getTools().size(); i++) {
			addTool(parseDataObj.getTools().get(i).getTool(), parseDataObj.getTools().get(i).getQty());
		}

		//add Plan to Warehouse
		for (int i = 0; i < parseDataObj.getPlans().size(); i++) {
			addPlan(parseDataObj, i);
		}

		//add Wave to Wave List
		Wave wave;
		int size = parseDataObj.getWaves().size();
		for (int i = 0; i < size; i++) {
			wave = new Wave(parseDataObj.getWaves().get(i));
			waves.add(wave);
		}
	}

	//add plan to warehouse
	private static void addPlan(ParseData obj, int i) {
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
			String jasonFileLocation = args[0];
			Gson gson = new Gson();

			BufferedReader br = new BufferedReader(new FileReader(jasonFileLocation));
			ParseData obj = gson.fromJson(br, ParseData.class);
			parseData(obj);

			ConcurrentLinkedQueue<Product> simulationResult;
			simulationResult = Simulator.start();
			FileOutputStream fout = new FileOutputStream("result.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(simulationResult);
			oos.close();
			br.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}