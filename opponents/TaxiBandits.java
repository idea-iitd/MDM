/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package opponents;

import ilo.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;

import com.sun.corba.se.spi.orbutil.fsm.Action;

import recomendation.Main;
import recomendation.RoadNetwork;
import recomendation.Segment;
import recomendation.SegmentProcess;
import recomendation.SimulatePath;

/**
 *
 * @author sairam
 */
public class TaxiBandits implements StochasticOpponent {
    
    private Integer[] actions; 
    private  ArrayList<ArrayList<SegmentProcess>> routes = new  ArrayList<ArrayList<SegmentProcess>>();
    
    public TaxiBandits( ArrayList<ArrayList<SegmentProcess>> routes ){
         this.routes = routes;
    }

    @Override
    public Integer[] getPossibleActions() {
         actions = new Integer[routes.size()];
         for( int i = 0; i < routes.size() ; i++ ) actions[i]=i;
         return actions;
        
    }

    public void getReward( Integer a , ArrayList<BlockingQueue<ArrayList>> queues, Integer timeStep) {
    	double minCost = RoadNetwork.findMinProfit(routes.get(0), main.TIME_SLOT);
    	double maxCost = RoadNetwork.findMaxProfit(routes.get(0), main.TIME_SLOT);
//    	System.out.println("Action : "+a);
    	//TODO: get time of day (3rd parameter in SimulatePath
        Thread t = new Thread(new SimulatePath(routes.get(a), queues.get(a), main.TIME_SLOT, minCost, maxCost, main.SIMULATION_SPEED , timeStep));
        t.start();
//        try {
//			t.join();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        System.out.println("nnnn");
    } 

    @Override
    public String getName() {
        return "TaxiBandit";
    }

    @Override
  //For reward its maxMean 
    public double getMaxMean() {
//    
//    	double[] expectedProfit = recomendation.RoadNetwork.findAllNormalizedNetProfit(routes.get(0), main.TIME_SLOT);
    	double[] expectedProfit = new double[routes.size()];
    	int i = 0;
    	for(ArrayList<SegmentProcess> segment: routes)
    		expectedProfit[i++] = segment.get(0).potentialCost;
    	double maxMean = 0.0;
    	for(double val : expectedProfit)
    		if(maxMean < val)
    			maxMean = val;
    	System.out.println("Expected Maxixum Profit"+ maxMean);
    	return maxMean;
    	
    	
    //return Main.findMinCost(routes);
    	
//        throw new UnsupportedOperationException("Not supported yet.");
    }
	//For loss its minMean  
    public double getMinMean() {
//    
//    	ArrayList<SegmentProcess> temProcesses = new ArrayList<SegmentProcess>();
//    	double[] expectedCost = new double[routes.size()];
//    	int i = 0;
//    	for(ArrayList<SegmentProcess> segment: routes)
//    		expectedCost[i++] = segment.get(0).potentialCost;
//    	double[] expectedCost = recomendation.RoadNetwork.findMaxCost(routes, main.TIME_SLOT);
    	double[] expectedCost = new double[routes.size()];
    	int i = 0;
    	for(ArrayList<SegmentProcess> segment: routes)
    		expectedCost[i++] = segment.get(0).potentialCost;
    	double minMean = Double.MAX_VALUE;
    	for(double val : expectedCost){
    	    System.out.println(val);
    		if(minMean > val)
    			minMean = val;
    	}
    	return minMean;
    	
    	
    //return Main.findMinCost(routes);
    	
//        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
