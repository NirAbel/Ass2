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

import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {
	private static WorkStealingThreadPool workStealingThreadPool;
	private static Warehouse warehouse;
	private static List<Wave> waves;


	/**
	 * Begin the simulation
	 * Should not be called before attachWorkStealingThreadPool()
	 */
	public static ConcurrentLinkedQueue<Product> start() {
		ConcurrentLinkedQueue<Product> manProducts = new ConcurrentLinkedQueue<>();
		try {
			workStealingThreadPool.start();
			for (Wave currWave : waves) {
				List<Product> productWave = getTheWaveProduct(currWave);
				CountDownLatch l = new CountDownLatch(productWave.size());
				for (Product currProduct : productWave) {
					ManTask currTask = new ManTask(currProduct, warehouse);
					Simulator.workStealingThreadPool.submit(currTask);
					currTask.getResult().whenResolved(l::countDown);
				}
				l.await();
				manProducts.addAll(productWave.stream().collect(Collectors.toList()));
			}
			workStealingThreadPool.shutdown();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		for(Product p : manProducts){
			System.out.println(p.getStartId() + " " +p.getFinalId());
		}
		return manProducts;
	}

	private static List<Product> getTheWaveProduct(Wave wave)
	{
		List<Product> productList = new ArrayList<>();
		int count=0;
		for (Order order : wave.getOrders())
		{
			int i=0;
			while(i<order.getQty()){
				productList.add(new Product(order.getStartId() + i, order.getProduct()));
				count=count+1;
				i++;
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

	/**
	 * add new tool to the Warehouse
	 * @param toolType
	 * @param qty
	 */
	private static void addTool(String toolType, int qty) {
		switch (toolType){
			case "gs-driver":{
				GcdScrewDriver t1 = new GcdScrewDriver();
				warehouse.addTool(t1, qty);
				break;
			}
			case "np-hammer":{
				NextPrimeHammer t2 = new NextPrimeHammer();
				warehouse.addTool(t2, qty);
				break;
			}
			case "rs-pliers":{
				RandomSumPliers t3 = new RandomSumPliers();
				warehouse.addTool(t3, qty);
				break;
			}
			default:
				throw new NoSuchElementException("no such tools");
		}
	}

	private static void Series(Series SeriesObj) {
		//create new WorkStealingThreadPool
		WorkStealingThreadPool workStealing= new WorkStealingThreadPool(SeriesObj.getThreads());
		Simulator.attachWorkStealingThreadPool(workStealing);
		warehouse = new Warehouse();
		waves = new ArrayList<>();

		//this func is adding plan to wareHouse
		for (int i = 0; i < SeriesObj.getPlans().size(); i++) {
			addPlan(SeriesObj, i);
		}
		//this func is adding tool to wareHouse
		for (int i = 0; i < SeriesObj.getTools().size(); i++) {
			addTool(SeriesObj.getTools().get(i).getTool(), SeriesObj.getTools().get(i).getQty());
		}
		addWave(SeriesObj);
	}

	/**
	 * this func is adding another Wave to Wave List
	 * @param SeriesObj
	 */
	private static void addWave(Series SeriesObj) {
		Wave wave;
		int sizeOfSeriesObj = SeriesObj.getWaves().size();
		for (int i = 0; i < sizeOfSeriesObj; i++) {
			wave = new Wave(SeriesObj.getWaves().get(i));
			waves.add(wave);
		}
	}

	/**
	 * this func is adding plan to wareHouse
	 * @param obj
	 * @param i
	 */
	private static void addPlan(Series obj, int i) {
		ManufactoringPlan plan;
		plan = new ManufactoringPlan(obj.getPlans().get(i).getProduct(),obj.getPlans().get(i).getParts(), obj.getPlans().get(i).getTools());
		warehouse.addPlan(plan);
	}

	public static void main(String[] args) throws InterruptedException {
		try
		{
//			String jsonFile = args[0];
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\win10\\IdeaProjects\\Ass2\\ax1.json"));
			Series obj = gson.fromJson(br, Series.class);
			Series(obj);
     		ConcurrentLinkedQueue<Product> simulationResult;
			simulationResult = Simulator.start();
			//System.out.println(simulationResult.size());
			FileOutputStream file = new FileOutputStream("result.ser");
			ObjectOutputStream oos = new ObjectOutputStream(file);
			oos.writeObject(simulationResult);
			oos.close();
			br.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}