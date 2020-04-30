package uct;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import recomendation.SegmentProcess;

public class MDPState1 implements State1{

	/**
	 * @param args
	 */
	public SegmentProcess segment;
	public boolean passengerFound;
	public double cost;
	public double[] freqSuccess;
	public double segmentLength;
	//public boolean visited=false;
	
	
	public MDPState1(SegmentProcess segment)
	{

	this.segment = segment;
	this.segment.visited=true;
	this.passengerFound = false;
	this.cost = segment.potentialCost;
	this.freqSuccess=segment.freqSuccess;
	this.segmentLength=segment.segmentLength;
	}
	
	public MDPState1(MDPState1 mdpState)
	{

	this.segment = mdpState.segment;
	this.segment.visited=true;
	this.passengerFound = mdpState.passengerFound;
	this.cost = mdpState.cost;
	this.segmentLength=mdpState.segmentLength;
	this.freqSuccess=mdpState.freqSuccess;
	}
	
	
	public MDPState1(SegmentProcess segment,double[] freqSuccess)
	{
		
		
		//this.segment=new SegmentProcess(segment.startLocation, segment.endLocation, segment.segmentLength, segment.distanceTravelled, freqSuccess, segment.freqFailure, segment.timeToCrossSegment);
		this.segment=segment;
		this.segment.visited=true;
		this.passengerFound=false;
		this.cost=this.segment.potentialCost;
		this.freqSuccess=freqSuccess;
		this.segment.freqSuccess=freqSuccess;
		//System.out.println(this.freqSuccess[DemoUCT1.timeSlot]);
		this.segmentLength=this.segment.segmentLength;
		
	
	}
	
	
	
	/*public void populateFreqOfSuccess()
	
	{
		Map<Long, ArrayList<SegmentProcess>> segmentIdMap= DemoUCT1.roadNetwork.segmentProcessIdMap;
		for(Entry<Long, ArrayList<SegmentProcess>> entry:segmentIdMap.entrySet())
		{
			ArrayList<SegmentProcess> process=entry.getValue();
			for(SegmentProcess segment1:process)
			{
				
				MDPState1 MDP=new MDPState1(segment1,segment1.freqSuccess);
				System.out.println(this.freqSuccess[DemoUCT1.timeSlot]);
				
			}
	
	
		}
	
	
	}*/
	
	
	
	public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if((obj == null) || (obj.getClass() != this.getClass()))
            return false;
        // object must be Pair at this point
        MDPState1 mdpState = (MDPState1)obj;
        return this.segment.equals(mdpState.segment);
    }

	//@Override
	public int hashCode()
	{
        return this.segment.hashCode();
	}

	//@Override
	public MDPState1 copy() {
		return new MDPState1(this);
		
	}
	

	public void setPassengerFound(boolean passengerFound) {
		this.passengerFound = passengerFound;
	}
	
	//@Override
	public boolean isAbsorbing() {
		return this.passengerFound;
	}

	//@Override
	public void printState() 
	{
		//System.out.println("Segment :"+segment.startLocation.id+" "+segment.endLocation.id+" ");
	}
	
	
}
