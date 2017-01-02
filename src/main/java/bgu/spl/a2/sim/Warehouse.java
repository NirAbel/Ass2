package bgu.spl.a2.sim;

import bgu.spl.a2.sim.tools.GcdScrewDriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;
import bgu.spl.a2.sim.tools.Tool;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.Deferred;
import java.util.List;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * A class representing the warehouse in your simulation
 * 
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 */
public class Warehouse {
    List<ManufactoringPlan> plans;
    private ConcurrentLinkedDeque <GcdScrewDriver> gsDrivers;
    private ConcurrentLinkedDeque<NextPrimeHammer> npHammers;
    private ConcurrentLinkedDeque<RandomSumPliers> rsPliers;
    private ConcurrentLinkedDeque<Deferred<Tool>> gcdDeferreds;
    private ConcurrentLinkedDeque<Deferred<Tool>> npDeferreds;
    private ConcurrentLinkedDeque<Deferred<Tool>> rsDeferreds;

	/**
	* Constructor
	*/
    public Warehouse(){
        plans=new LinkedList<>();
        gsDrivers=new ConcurrentLinkedDeque<>();
        npHammers=new ConcurrentLinkedDeque<>();
        rsPliers=new ConcurrentLinkedDeque<>();
        gcdDeferreds=new ConcurrentLinkedDeque<>();
        npDeferreds=new ConcurrentLinkedDeque<>();
        rsDeferreds=new ConcurrentLinkedDeque<>();
    }

	/**
	* Tool acquisition procedure
	* Note that this procedure is non-blocking and should return immediatly
	* @param type - string describing the required tool
	* @return a deferred promise for the  requested tool
	*/
    public Deferred<Tool> acquireTool(String type){
        Deferred<Tool> toolDeferred= new Deferred<Tool>();
        switch (type) {
            case "rs-pliers":{
                Tool t = rsPliers.poll();
                if(t!=null){
                    toolDeferred.resolve(t);
                }
                else{
                    rsDeferreds.add(toolDeferred);
                }
                return toolDeferred;
            }

            case "np-hammer":{
                Tool t = npHammers.poll();
                if(t!=null){
                    toolDeferred.resolve(t);
                }
                else{
                    npDeferreds.add(toolDeferred);
                }
                return toolDeferred;
            }

            case "gs-driver":{
                Tool t = gsDrivers.poll();
                if(t!=null){
                    toolDeferred.resolve(t);
                }
                else{
                    gcdDeferreds.add(toolDeferred);
                }
                return toolDeferred;
            }
            default:
                throw new NoSuchElementException("no such tools");
        }
    }

	/**
	* Tool return procedure - releases a tool which becomes available in the warehouse upon completion.
	* @param tool - The tool to be returned
	*/
    public synchronized void releaseTool(Tool tool){
        //TODO:check if need to add locks and synchronized
        switch (tool.getType()) {
            case "rs-pliers":{
                Deferred<Tool> toolDeferred=rsDeferreds.poll();
                if(toolDeferred!=null)
                    toolDeferred.resolve(tool);
                else
                    rsPliers.addLast((RandomSumPliers)tool);
            }

            case "np-hammer":{
                Deferred<Tool> toolDeferred=npDeferreds.poll();
                if(toolDeferred!=null)
                    toolDeferred.resolve(tool);
                else
                    npHammers.addLast((NextPrimeHammer)tool);
            }

            case "gs-driver":{
                Deferred<Tool> toolDeferred=gcdDeferreds.poll();
                if(toolDeferred!=null)
                    toolDeferred.resolve(tool);
                else
                    gsDrivers.addLast((GcdScrewDriver)tool);
            }
            default:
                throw new NoSuchElementException("no such tools");
        }
    }

	
	/**
	* Getter for ManufactoringPlans
	* @param product - a string with the product name for which a ManufactoringPlan is desired
	* @return A ManufactoringPlan for product
	*/
    public ManufactoringPlan getPlan(String product){
        for(int i=0;i<plans.size();i++){
            if(plans.get(i).getProductName().equals(product))
                return plans.get(i);
        }
        return null;
    }
	
	/**
	* Store a ManufactoringPlan in the warehouse for later retrieval
	* @param plan - a ManufactoringPlan to be stored
	*/
    public void addPlan(ManufactoringPlan plan){
        plans.add(plan);
    }
    
	/**
	* Store a qty Amount of tools of type tool in the warehouse for later retrieval
	* @param tool - type of tool to be stored
	* @param qty - amount of tools of type tool to be stored
	*/
    public void addTool(Tool tool, int qty){
        switch (tool.getType()){
            case "gs-driver":
                for (int i=0;i<qty;i++){
                gsDrivers.add(new GcdScrewDriver());
            }
                break;
            case "np-hammer":
                for (int i=0;i<qty;i++) {
                    npHammers.add(new NextPrimeHammer());
                }
                break;
            case "rs-pliers":
                for (int i=0;i<qty;i++) {
                    rsPliers.add(new RandomSumPliers());
                }
                break;
            default:
                throw new NoSuchElementException("no such tools");
        }
    }

}
