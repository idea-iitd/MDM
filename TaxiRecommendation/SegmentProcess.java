package TaxiRecommendation;

import java.util.ArrayList;

import recomendation.Location;


public class SegmentProcess {

	/*
	 * tells whether segment has been visited or not after timestep loop in DemoUCT1
	
	*/
	public boolean visited=false;
	
	
	
	
	/**
	 * timeSlot is the number of slots in a day ....changing it from 3 to 4
	 */
	public static int timeSlot = 4;
	
	/**
	 * Is he no of routes recommended from this segment in testing phase
	*/
	public int noOfRoutesRecommended=0;
	
	/**
	 * Is the mileage of the taxi in metres per litres.
	 */
	
	public static double MILEAGE = 5000;
	
	/**
	 * Is the Company fare to be paid for taking a Taxi to drive
	 */
	public static double COMPANY_FEE = 0.0012;
	/**
	 * Is the cost of gas in $ per litre.
	 */
	public static double GAS = 0.97;
	
	/**
	 * Is the fare charged per metres.
	 * TODO  check here fare
	 */
	public static double FARE_PER_METER = 0.00285;
	
	/**
	 * Contains information about the starting location of the segment.
	 */
	public Location startLocation;
	
	/**
	 * Contains information about the ending location of the segment.
	 */
	public Location endLocation;
	
	/**
	 * Is the length of the segment in metres.
	 */
	public double segmentLength;
	
	
	/**
	 * UCB value of each segment
	 * 
	 */
	public double UCB=0.0;
	
	
	/**
	 * Is the Potential earning of the taxi in dollar.
	 */
	public double[] potentialEarning = new double[timeSlot];
	/**
	 * Is the potential cost incurred while crossing this segment by the taxi in dollar.
	 */
	public double potentialCost;
	
	/**
	 * Is the frequency of a customer being found whenever a taxi passed through 
	 * this segment in a particular duration of time of the day.
	 * <br>0 - 06:00am to 4:59pm
	 * <br>1 - 5:00pm to 00:59am
	 * <br>2 - 01:00am to 5:59am
	 */
	public double[] freqSuccess = new double[timeSlot];
	
	/**
	 * Is the frequency of a customer NOT being found whenever a taxi passed through 
	 * this segment in a particular duration of time of the day.
	 * <br>0 - 06:00am to 4:59pm
	 * <br>1 - 5:00pm to 00:59am
	 * <br>2 - 01:00am to 5:59am
	 */
	public double[] freqFailure = new double[timeSlot];

	/**
	 * Is the length of the distance in metres traveled from this segment.
	 */
	public double[] distanceTravelled = new double[timeSlot];
	/**
	 * Is the time (in seconds) required to cross the segment from start location to end location.
	 */
	public double timeToCrossSegment;
	
	ArrayList<Double> directionVector;
	//public Double[] directionVector=new Double[8];

	

	/**
	 * Creates a new instance given the start location, end location, segment length, frequency of success, 
	 * frequency of failure, and the time required to cross the segment.
	 * @param startLocation The start location
	 * @param endLocation The end location
	 * @param segmentLength The length of the road segment in metres
	 * @param freqSuccess The frequencies of success for different intervals of time of the day
	 * @param freqFailure The frequencies of failure for different intervals of time of the day
	 * @param timeToCrossSegment The time required to cross the road segment in seconds
	 */
	
	 public SegmentProcess(Location startLocation, Location endLocation, double segmentLength, double[] distanceTravelled, double[] freqSuccess, double[] freqFailure, double timeToCrossSegment)
	 {
		 directionVector = new ArrayList<Double>();
		 this.startLocation = startLocation;
		 this.endLocation = endLocation;
		 this.segmentLength = segmentLength;
		 this.timeToCrossSegment = timeToCrossSegment;
		 this.potentialCost = this.calculatePotentialCost();
		 for(int i = 0 ; i < freqFailure.length ; ++i)
		 {
			 this.distanceTravelled[i] = distanceTravelled[i];
			 this.freqSuccess[i] = freqSuccess[i];
			 this.freqFailure[i] = freqFailure[i];
			 potentialEarning[i] = calculatePotentialEarning(i);
		 }

	 }
	
	/**
	 * TODO:
	 * @param timeRange
	 * @return
	 */
	public double findPickupProbability(int timeRange)
		{
		return this.freqSuccess[timeRange] / (this.freqSuccess[timeRange] + this.freqFailure[timeRange]);
		}
	
	public double calculatePotentialCost()
		{
		return this.segmentLength * GAS / MILEAGE + this.timeToCrossSegment * COMPANY_FEE;
		}
	
	
	public double calculatePotentialEarning(int timeRange)
		{
		return distanceTravelled[timeRange] * FARE_PER_METER;
		}
	
	public void segmentProcessPrint(int timeSlot)
		{
		System.out.println(startLocation.id+" "+endLocation.id+" "+startLocation.longitude+" "+startLocation.latitude+" "+endLocation.longitude+" "+endLocation.latitude+" "+segmentLength+" "+distanceTravelled[timeSlot]+" "+potentialEarning[timeSlot]+" "+potentialCost+" "+timeToCrossSegment);
		}
	
	public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if((obj == null) || (obj.getClass() != this.getClass()))
            return false;
        // object must be Pair at this point
        SegmentProcess segmentProcess = (SegmentProcess)obj;
        return this.startLocation.id == segmentProcess.startLocation.id && (this.endLocation.id == segmentProcess.endLocation.id);
    }

	@Override
	public int hashCode()
	{
		long hash = 7;
        hash = 31 * hash + this.startLocation.id%Integer.MAX_VALUE;
        hash = 31 * hash + this.endLocation.id%Integer.MAX_VALUE;
        return (int)hash;
	}
	

    public String toString()
    {
    	 return startLocation.id+" "+endLocation.id;
    }
    
    
   
    
    
    
	}






