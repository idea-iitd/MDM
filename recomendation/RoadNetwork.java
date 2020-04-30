package recomendation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import driver.Centralized;

import recomendation.Location;

public class RoadNetwork 
{
	public Map<Long, ArrayList<SegmentProcess>> segmentProcessIdMap = new HashMap<Long, ArrayList<SegmentProcess>>();
	private String INPUT_SEGMENT_FILE = "";
	private double minCost = Integer.MAX_VALUE;
	private double maxCost = Integer.MIN_VALUE;
	
//	public RoadNetwork() 
//	{
//		segmentProcessIdMap = new HashMap<Long, ArrayList<SegmentProcess>>();
//	}
	
	public RoadNetwork(String file)
	{
		this.INPUT_SEGMENT_FILE=file;
	}
	
	
	
	
	
	/**
	 * function to calculate size of road Network
	 * @return  size of road Network
	 */
	public int sizeOfRoadNetwork()
	{
		return segmentProcessIdMap.size();
	}
	
	/**
	 * Function to get List of segment having same start location
	 * @param start Long startID
	 * @return List of road segment 
	 */
	public ArrayList<SegmentProcess> getSegmentProcessFromStartId(long start)
	{
		return segmentProcessIdMap.get(start);
	}
	
	
	
	
	/**
	 * Function to adjacent road segment 
	 * @param start startid of segment
	 * @param end   endid of segment
	 * @return  return the segment 
	 */
	public SegmentProcess currRoadSegment(long start, long end) 
	{
		ArrayList<SegmentProcess> segmentList = segmentProcessIdMap.get(start);
		SegmentProcess segmentProcess = null;
		for (SegmentProcess temp : segmentList)
		{
			if(temp.endLocation.id == end)
			{
				segmentProcess = temp;
				break;
			}
		}
		return segmentProcess;
	}
	
	/**
	 * Function to return neighbor Road Segment
	 * @param segment  source segment
	 * @return All adjacent road segment except itself 
	 */
	
	public ArrayList<SegmentProcess> neighborSegment(SegmentProcess segment)
	{
		ArrayList<SegmentProcess> segmentList = segmentProcessIdMap.get(segment.endLocation.id);
		ArrayList<SegmentProcess> list = new ArrayList<SegmentProcess>();
		if(segmentList==null){
			//System.out.println("segment passed is the leaf segment,no further segments starting from current segment");
			return list;}
		else
		for(SegmentProcess segmentTemp:segmentList)
		{
			if(!(segmentTemp.endLocation.id.toString().equals(segment.startLocation.id.toString())))
			{
				//System.out.println("freq"+segmentTemp.freqSuccess[2]);
				list.add(segmentTemp);
				
			}
			
		}
		return list;
		
	}
	
	/**
	 * Function to find normalize cost based on maximum and minimum cost of the road network
	 * @param cost
	 * @return normalized Cost
	 */
	public double normailizedCostOfRoadNetwork(double cost)
	{
		return (cost - minCost)/(maxCost - minCost);
	}
	
	/**
	 * Function to print adjacent road segment with start and end point of a given road segment
	 * @param segment 
	 */
	public void printAdjacentSegment(SegmentProcess segment)
	{
		System.out.println("Adjacent segment list for :" + segment.startLocation.id+" "+segment.endLocation.id);
		ArrayList<SegmentProcess> segmentList = neighborSegment(segment);
		for(SegmentProcess iterSegment:segmentList)
			System.out.println(iterSegment.startLocation.id+" "+iterSegment.endLocation.id);
	}
	
	/**
	 *  function to populate the RoadNetwok segment from file CompleteSegmentData.txt
	 * @param noOfTimeSlot time Slot of the day for recommendation 
	 */
	public void populateSegmentProcessIdMap(int noOfTimeSlot)
	{
		String[] tr=Centralized.timeslotRange;
		File file = new File(INPUT_SEGMENT_FILE);
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String string;
			while((string = in.readLine()) != null)
			{
				String[] str = string.split(";");
				String[] subString = str[0].split(",");
				Location startLocation = new Location(Long.parseLong(subString[0]), Double.parseDouble(subString[1]), Double.parseDouble(subString[2]));
				subString = str[1].split(",");
				Location endLocation = new Location(Long.parseLong(subString[0]), Double.parseDouble(subString[1]), Double.parseDouble(subString[2]));
				double segmentLength = Double.parseDouble(str[2]);
				subString = str[3].split(",");
				String[] subString2 = str[4].split(",");
				String[] subString3 = str[5].split(",");
				double[] distanceTravelled = new double[noOfTimeSlot];
				double[] freqSuccess = new double[noOfTimeSlot];
				double[] freqFailure = new double[noOfTimeSlot];
				for (int i = 0; i < subString.length; i++) {

				for(int s=0;s<tr.length;s++)
					{
						
						if(i>=Integer.parseInt(tr[s].split("-")[0]) && i<=Integer.parseInt(tr[s].split("-")[1]))
						{
							
							distanceTravelled[s] += Double.parseDouble(subString[i]);
							freqSuccess[s] += Double.parseDouble(subString2[i]);
							freqFailure[s] += Double.parseDouble(subString3[i]);
							break;
						}
					}
		
				/*
					if(i>=1 && i<=5)
					{
						distanceTravelled[0] += Double.parseDouble(subString[i]);
						freqSuccess[0] += Double.parseDouble(subString2[i]);
						freqFailure[0] += Double.parseDouble(subString3[i]);
					}
					else {
						if(i>=6 && i<=16)
						{
							distanceTravelled[1] += Double.parseDouble(subString[i]);
							freqSuccess[1] += Double.parseDouble(subString2[i]);
							freqFailure[1] += Double.parseDouble(subString3[i]);
						}
						else {
							distanceTravelled[2] += Double.parseDouble(subString[i]);
							freqSuccess[2] += Double.parseDouble(subString2[i]);
							freqFailure[2] += Double.parseDouble(subString3[i]);
						}
					}
					*/
				}
				for(int n=0;n<tr.length;n++)
				{
				distanceTravelled[n] /= Integer.parseInt(tr[n].split("-")[1]) - Integer.parseInt(tr[n].split("-")[0])+1;
				
				/*distanceTravelled[0] /= 5;  // to take average distance
				distanceTravelled[1] /= 11;
				distanceTravelled[2] /= 8;*/
				}
				
				long timeToCrossSegment = Long.parseLong(str[6]);

				if(startLocation.id.toString().equals(endLocation.id.toString())==false)
				{
				SegmentProcess segmentProcess = new SegmentProcess(startLocation, endLocation, segmentLength, distanceTravelled, freqSuccess, freqFailure, timeToCrossSegment);
				if(segmentProcessIdMap.containsKey(startLocation.id) == true)
					{
						ArrayList<SegmentProcess> segmentProcessesList = segmentProcessIdMap.get(startLocation.id);
						
						segmentProcessesList.add(segmentProcess);
					}
				
				
			
				else
					{
						ArrayList<SegmentProcess> segmentProcessesList = new ArrayList<SegmentProcess>();
						segmentProcessesList.add(segmentProcess);
						segmentProcessIdMap.put(startLocation.id, segmentProcessesList);
					}
				
				double tempCost = segmentProcess.calculatePotentialCost();
				maxCost = (tempCost> maxCost)?tempCost:maxCost;
				minCost = (tempCost < minCost)?tempCost:minCost;
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * function to find minimum cost to cruise the list of neighbor
	 * @param neighbourSegment  It is the List of all neighbor or adjacent road segment
	 * @return  minCost cost to cross the list of routes
	 */
	public double findMinCost(ArrayList<SegmentProcess> neighbourSegment)
	{
		double minCost = Double.MAX_VALUE;
		for(SegmentProcess segment : neighbourSegment)
			if(minCost > segment.calculatePotentialCost())
				minCost = segment.calculatePotentialCost();
		return minCost;
	}
	
	/**
	 * Finds Minimum Net Profit from neighbor segment. This is the real profit earned by subtracting the cost till the segment we got the customer for eg. got customer
	 * at third segment then profit earned from third segment - cost incurred till second segment.
	 * @param rootToLeaves It is the List of all desired possible routes
	 * @param timeSlot It is the time slot in which we are giving recommendation
	 * @return minProfit min profit among the route List
	 */
	public double findMinProfit(ArrayList<SegmentProcess> neighbourSegment, int timeSlot)
	{
		double minProfit = Double.MAX_VALUE;
		//find minimum profit of earned from all route
		for(SegmentProcess segment : neighbourSegment)
		{
			double profit = segment.potentialEarning[timeSlot];
			if(profit < minProfit)    // check profit for first segment
				minProfit = profit;
		}
		return minProfit;
	}
	
	/**
	 * function to find maximum cost to cruise the list of list
	 * @param rootToLeaves  It is the List of all desired possible routes
	 * @return  max cost to cross the list of routes
	 */
	public double findMaxCost(ArrayList<SegmentProcess> neighbourSegment)
	{
		double maxCost = -1 * Double.MAX_VALUE;
		//find maximum cost (cost of all segments) of each route
		for(SegmentProcess segment : neighbourSegment)
		{
			double cost = segment.calculatePotentialCost();
			if(cost > maxCost)
				maxCost = cost;
		}
		return maxCost;
	}
	
	
	/**
	 * Finds Max Net Profit from routes List. This is the real profit earned by subtracting the cost till the segment we got the customer for eg. got customer
	 * at third segment then profit earned from third segment - cost incurred till second segment.
	 * @param rootToLeaves It is the List of all desired possible routes
	 * @param timeSlot It is the time slot in which we are giving recommendation
	 * @return maxProfit max profit among the route List
	 */
	public double findMaxProfit(ArrayList<SegmentProcess> neighbourSegment, int timeSlot)
	{
		double maxProfit = -1 * Double.MAX_VALUE;
		//find maximum cost (cost of all segments) of each route
		for(SegmentProcess segment : neighbourSegment)
		{
			double profit = segment.potentialEarning[timeSlot];
			if(profit > maxProfit)    // check profit for first segment
				maxProfit = profit;
		}
		return maxProfit;
	}
	
	
	public double findMaxTimeToCrossSegment(ArrayList<ArrayList<SegmentProcess>> rootToLeaves)
	{
		double maxTime= 0.0;
		//find maximum time(time of all segments) of each route
		for(ArrayList<SegmentProcess> route : rootToLeaves)
		{
			double timeForSegment = 0;
			for(SegmentProcess segment : route)
				timeForSegment += segment.timeToCrossSegment;
			if(timeForSegment > maxTime)
				maxTime = timeForSegment;
		}
		return maxTime;
	}
	
	public double findMinTimeToCrossSegment(ArrayList<ArrayList<SegmentProcess>> rootToLeaves)
	{
		double minTime= Double.MAX_VALUE;
		//find maximum time(time of all segments) of each route
		for(ArrayList<SegmentProcess> route : rootToLeaves)
		{
			double timeForSegment = 0;
			for(SegmentProcess segment : route)
				timeForSegment += segment.timeToCrossSegment;
			if(timeForSegment < minTime)
				minTime = timeForSegment;
		}
		return minTime;
	}
	
	/**
	 * Function to calculate Net profit along the route
	 * @param route Recommended route after bandit finds a customer
	 * @param timeSlot time at which routes to be recommended
	 * @return netProfit of the route
	 */
	public double NetProfitOnRoute(ArrayList<SegmentProcess> route, int timeSlot)
	{
			double netProfitperSegment = 0.0;
			double failureProduct = 1.0;
			double potentialCost = route.get(0).potentialCost;
			double potentialEarning = route.get(0).potentialEarning[timeSlot];
			double pickUpProbabilty = route.get(0).findPickupProbability(timeSlot);
			netProfitperSegment = potentialEarning*pickUpProbabilty - potentialCost*(1 - pickUpProbabilty);
			for(int j = 1 ; j < route.size() ; ++j)
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
}
