package uct;

import java.util.ArrayList;
import java.util.Map;

import driver.Centralized;

import recomendation.SegmentProcess;

/* This class is used for simulations and provide neigbours of current state is set after simulation
 * 



*/
public class SegmentSimulator1 implements Simulator1 {
	public static final double simulationSpeed = 1;
	private MDPState1 mdpState;
	private int timeSlot;
	private boolean reward;
	private ArrayList<MDPState1> neighbourMdpState;
	public int numActions;
	
	
	public SegmentSimulator1(MDPState1 mdpState)
	{
	this.mdpState = mdpState;
	this.timeSlot = DemoUCT1.timeSlot;
	ArrayList<SegmentProcess> neighbourList = DemoUCT1.roadNetwork.neighborSegment(mdpState.segment);
	if(neighbourList.isEmpty())
	{
		this.numActions=0;
		//System.out.println("no neighbours found for current segment ");
	}
	else{
	neighbourMdpState = new ArrayList<MDPState1>();
	for(SegmentProcess segment : neighbourList)
	{
		if(DemoUCT1.phase=="Training" || DemoUCT1.phase=="Verma")
		{
		
			if(segment.visited==true)
				this.neighbourMdpState.add(new MDPState1(segment)); 
			else{
				double[] freqArray=new double[Centralized.noOfTimeslots];
				for(int n=0;n<DemoUCT1.noOfTimeslot;n++){
				freqArray[n]=DemoUCT1.segmentFreqMap.get(segment).get(n);
				}
				this.neighbourMdpState.add(new MDPState1(segment,freqArray));
	
				}
		
		}
	else 
		if(DemoUCT1.phase=="Testing")
		{
			this.neighbourMdpState.add(new MDPState1(segment));
		}
	
	}
	this.numActions=getNumActions();
	}
}
		

public void populateNeighbor()
{
	ArrayList<SegmentProcess> neighbourList = DemoUCT1.roadNetwork.neighborSegment(mdpState.segment);
	if(neighbourList.isEmpty())
	{
		this.numActions=0;
		//System.out.println("no neighbours found for current segment ");
	}
	else{
	neighbourMdpState = new ArrayList<MDPState1>();
	
	for(SegmentProcess segment : neighbourList)
	{
		if(DemoUCT1.phase=="Training" || DemoUCT1.phase=="Verma")
		{
		
			if(segment.visited==true)
				this.neighbourMdpState.add(new MDPState1(segment)); 
			else{
				double[] freqArray=new double[Centralized.noOfTimeslots];
				for(int n=0;n<DemoUCT1.noOfTimeslot;n++){
					freqArray[n]=DemoUCT1.segmentFreqMap.get(segment).get(n);
					}
				this.neighbourMdpState.add(new MDPState1(segment,freqArray));
	
				}
		
		}
	else 
		if(DemoUCT1.phase=="Testing")
		{
			this.neighbourMdpState.add(new MDPState1(segment));
		}
	
	}
	this.numActions=getNumActions();
	}
}		
		

	//@Override
	public MDPState1 getState() {
		return this.mdpState;
		
	}
	
	
	public ArrayList<MDPState1> getNeighbourState()
	{
		
		return this.neighbourMdpState;
		
		
	}
	
	public void setState(State1 state) {
		//System.out.print("Setting to new state: ");
		state.printState();
		//check this line if error comes in states
		this.mdpState = (MDPState1) state;
	}

	
	public void takeAction(int a) {
		//double freqOfSuccess=neighbourMdpState.get(a).segment.freqSuccess[this.timeSlot];
		double freqOfSuccess=neighbourMdpState.get(a).freqSuccess[this.timeSlot];
		//System.out.println("freqOfSuccess"+" " +freqOfSuccess);
		if(freqOfSuccess>0)
		{
			--neighbourMdpState.get(a).freqSuccess[this.timeSlot];
			this.reward = true;
			neighbourMdpState.get(a).setPassengerFound(true);	
		}
		else
		{
			this.reward = false;
			neighbourMdpState.get(a).setPassengerFound(false);
		}
		this.setState(neighbourMdpState.get(a));
		//System.out.println(this.getState().segment.segmentLength);
		this.populateNeighbor();
		/* updating to next state */
		
		}

	
	
	public void implementAction(int a)
	{
		this.setState(neighbourMdpState.get(a));
		this.populateNeighbor();
		//this.getState().freqSuccess[this.timeSlot]=this.getState().segment.freqSuccess[this.timeSlot];
		
	}
	
	
	//@Override
	public int getNumActions()
		{
		return neighbourMdpState.size();
		}

	//@Override
	public double getDiscountFactor() {
		return 0.0;
	}
//
	//@Override
	public double getReward(){
		return 0.0;
		}
	 

	//@Override
	public void initEpisode() {
		
		
	}
	
	
	

	

	

}
