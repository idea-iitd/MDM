package recomendation;
import java.util.ArrayList;

import driver.Centralized;


public class Main {
	/**
	 * noOftimeSlot is the number of slots in a day 
	 */
	public static int noOfTimeSlot = Centralized.noOfTimeslots;
	
	/**
	 * timeSlot is the slot in which recommendation needs to be given
	 */
	public static int timeSlot = Centralized.timeslot;
	
	/**
	 * depth is the number of road segment till which we will recommend to a Taxi driver 
	 */
	public static int depth = 5;
//	/**
//	 * To get the startSegment
//	 */
	public SegmentProcess startSegment;
	// startId and endId for segment
//	public static long start = 330665612l;
//    public static long end = 65325804l;
	
	// Path 2
//	public static long  start = 65339290l;
//	public static long end = 2819241004l;
	
	// Path 3
//	public static long  start = 65360287l;
//	public static long end = 2089587799l;
	
	//path 4
	public static long  start = 65339305l;
	public static long end = 259138847l;
	
	// Path 5
//	public static long  start = 65352339l;
//	public static long end = 65352344l;
	
	// Path 6
//	public static long start = 65325369l;
//	public static long end = 65325371l;
	
	
	// Path 6
//	public static long start = 258758038l;
//	public static long end = 65327138l;
	 
	/*Path 7
	public static long start = 258475645 ;
	public static long end = 258468617;*/


	// Path 8  65325061			65334473
	// public static long  start = 65325061l;
	//public static long end = 65334473l;
	
	
	/*public static long  start = 65336708 ;
	public static long  end = 65354081 ;*/


	/**
	 * 
	 * @param route  ArrayList of segment 
	 * @param timeSlot  time at which routes to be recommended
	 * @param index index of the segment in the given route
	 * @return
	 */
	public static double findAllNetProfitPerRoute(ArrayList<SegmentProcess> route, int timeSlot, int index)
	{
		
			double netProfitperSegment = 0.0;
			
			int routeSize = route.size();
			double failureProduct = 1.0;
			double potentialCost = route.get(0).potentialCost;
			double potentialEarning = route.get(0).potentialEarning[timeSlot];
			double pickUpProbabilty = route.get(0).findPickupProbability(timeSlot);
			netProfitperSegment = potentialEarning*pickUpProbabilty - potentialCost*(1 - pickUpProbabilty);
			for(int j = 1 ; j <= index && j < routeSize ; ++j)
			{
				potentialCost = route.get(j).potentialCost;
				potentialEarning = route.get(j).potentialEarning[timeSlot];
				pickUpProbabilty = route.get(j).findPickupProbability(timeSlot);
				double currentSegmentNetProfit = potentialEarning*pickUpProbabilty - potentialCost*(1 - pickUpProbabilty);
				failureProduct *= (1 - route.get(j - 1).findPickupProbability(timeSlot));
				netProfitperSegment = currentSegmentNetProfit + ( failureProduct * netProfitperSegment);
				
				
			}
		return netProfitperSegment;
	}

	
	// old main2 class
//	public  ArrayList<SegmentProcess> main2(int timeSlot) {
//		RoadNetwork roadNetwork = new RoadNetwork();
//		roadNetwork.populateSegmentProcessIdMap(noOfTimeSlot);
//		SegmentProcess segment = roadNetwork.currRoadSegment(start, end);
//		startSegment = segment;
//		ArrayList<SegmentProcess> neighbourSegment = roadNetwork.neighborSegment(segment);
//		return neighbourSegment;
//	}
	
	public  RoadNetwork main2(int timeSlot,String file) {
		RoadNetwork roadNetwork = new RoadNetwork(file);
		roadNetwork.populateSegmentProcessIdMap(noOfTimeSlot);
		/*SegmentProcess segment = roadNetwork.currRoadSegment(start, end);
		startSegment = segment;*/
		return roadNetwork;
	}
	
	/*public  RoadNetwork main3(int timeSlot,Long start,Long end) {
		RoadNetwork roadNetwork = new RoadNetwork();
		roadNetwork.populateSegmentProcessIdMap(noOfTimeSlot);
		SegmentProcess segment = roadNetwork.currRoadSegment(start, end);
		startSegment = segment;
		return roadNetwork;
	}*/
		
}
