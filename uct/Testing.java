package uct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import recomendation.Main;
import recomendation.RoadNetwork;
import recomendation.SegmentProcess;

import uct.Utils1.Maximizer1;

public class Testing {
	
	/**
	 * timeSlot is the number of slot in a day 
	 */
	 
	public static int timeSlot = 2;
	
	
	/**
	 * C Value constant used in UCB formula
	 * 
	 */
	public double ucb_scaler = 1;
	
	/**
	 *  Trajectories represents given a segment how many times 
	 *  UCT tree will be updated for that particular segment
	*/
	public static int trajectories = 500;
	
	
	
	
	
	
	
	Testing()
	{}
	
	
	/**
	 * 
	 * @param startOfSegment
	 * @param endOfSegment
	 * Main method used for testing
	 * 
	 * 
	 */

	public void test(long startOfSegment,long endOfSegment,int distance/*,boolean VermaTesting*/)
	{
		Random rand2 = new Random();
		SegmentProcess startSegment=DemoUCT1.roadNetwork.currRoadSegment(startOfSegment, endOfSegment);
		MDPState1 mdpState1 = new MDPState1(startSegment);
		SegmentSimulator1 simPlan1 = new SegmentSimulator1(mdpState1);
		if(simPlan1.numActions!=0)
		{	
			UCT1 planner = new UCT1(simPlan1, trajectories, distance,rand2,DemoUCT1.roadNetwork);
			mdpState1.printState();
			//if(VermaTesting==false)
			planner.planForTesting(mdpState1,mdpState1.segmentLength,mdpState1.segment);
			//else
				//planner.planForTestingV(mdpState1,mdpState1.segmentLength,mdpState1.segment);
		
		}
		else
		{
			//System.out.println("segment passed is the leaf segment,no further segments starting from current segment");							
			//DemoUCT1.out1.println("No recommendation,no further actions");
			/**
			 * uncomment this line later
			 * 
			 */
			DemoUCT1.out1.println(distance+1000);
		}

				
		
		
	}
	
	
	
}
