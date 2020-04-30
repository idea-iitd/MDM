                                                                     
                                                                     
                                                                     
                                             
package recomendation;

import ilo.main;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import tools.Sampler;


public class SimulatePath implements Runnable
	{
	private ArrayList<SegmentProcess> route;
	private int timeSlot;
	private double minProfit;
	private double maxProfit;
	private double scaleSimulationSpeed;
    private BlockingQueue<ArrayList> singleQueue; 
    private Integer timeStep;
	
	public SimulatePath(ArrayList<SegmentProcess> route, BlockingQueue<ArrayList> singleQueue ,  int timeSlot, double minProfit, double maxProfit, double scaleSimulationSpeed , Integer timeStep)
		{
		this.route = route;
		this.timeSlot = timeSlot;
		this.minProfit = minProfit;
		this.maxProfit = maxProfit;
		this.scaleSimulationSpeed = scaleSimulationSpeed;
        this.singleQueue = singleQueue;
        this.timeStep = timeStep;
                
		}
	
	//@Override
	public void run()
		{
		SegmentProcess segmentProcess = route.get(0);
		double pickupProbability = segmentProcess.findPickupProbability(timeSlot);
		long delay = (long) (segmentProcess.timeToCrossSegment / scaleSimulationSpeed);
//		int segmentIndex;
//				//is the index of the segment in the route where the passenger was found, route.size if no passenger found
//		
//		for(segmentIndex = 0 ; segmentIndex < route.size() ; ++segmentIndex)
//			{
//			SegmentProcess segment = route.get(segmentIndex);
//			long delay = (long) (segment.timeToCrossSegment / scaleSimulationSpeed);
//			//System.out.println( "Delay  " + delay);
//			
			try
				{
				Thread.sleep(delay);
				}
				catch(InterruptedException e)
					{
					e.printStackTrace();
					}
//			double pickupProbability = segment.findPickupProbability(timeSlot);
			Random rng = Sampler.getRandom();
			
			if(rng.nextDouble() < pickupProbability)
				{
				segmentProcess.freqSuccess[timeSlot] += 1;
//				break;
				}
			else
				segmentProcess.freqFailure[timeSlot] += 1;
//			}
		
		//TODO: future work: instead of passing loss, pass the segment where the passenger was found
		
		//calculate actual cost of the route
//		double cost = 0;
//		//TODO: update cost
//		for(int i = 0 ; i < segmentIndex ; ++i)
//			{
//			cost += route.get(i).calculatePotentialCost();
//			}
//			cost += route.get(segmentIndex).calculateCost();
		double profit = 0.0;
		profit = Main.findAllNetProfitPerRoute(route, timeSlot, 1);
		
		
		
//		System.out.println("min cost = " + minProfit);
//		System.out.println("max cost = " + maxProfit);
		
//		System.out.println("cost = " + profit);
		
		double normalizedProfit = (profit - minProfit) / (maxProfit - minProfit);
//		if( normalizedProfit < 0 )
//		   System.out.println("STUPID    XXXXXXXXXXXXXXXXXXXXXXXXXXX");
		
//		System.out.println("normalized cost = " + normalizedProfit);
		ArrayList value = new ArrayList();
		value.add(timeStep);
		value.add(normalizedProfit);
		value.add(profit);
		
		
		try {
                   synchronized (singleQueue) {
                	   // Since cost incurred is loss for Taxi Driver so we are making it negative to bring the notion of loss 
                     singleQueue.put(value);
                   
                  
                   }
                   
                } catch (InterruptedException ex) {
                     System.out.println(" problem in SImulatePath");
                }
		
//		System.out.println( " XXX");
		}
	
	
	}

