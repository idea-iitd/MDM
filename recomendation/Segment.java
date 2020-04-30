package recomendation;
public class Segment
	{
	/**
	 * timeSlot is the number of slot in a day 
	 */
	public static int timeSlot = 24;
	/**
	 * Is the mileage of the taxi in metres per litre.
	 */
	public static double MILEAGE = 5000;
	
	/**
	 * Is the Company fare to be paid for taking a Taxi to drive
	 */
	public static double COMPANY_FARE = 0.0012;
	/**
	 * Is the cost of gas in $ per litre.
	 */
	public static double GAS = 0.97;
	
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
	 * Is the frequency of a customer being found whenever a taxi passed through 
	 * this segment in a particular duration of time of the day.
	 * <br>0 - 12:00am to 5:59am
	 * <br>1 - 6:00am to 11:59am
	 * <br>2 - 12:00pm to 5:59pm
	 * <br>3 - 6:00pm to 11:59pm
	 */
	public double[] freqSuccess = new double[timeSlot];
	
	/**
	 * Is the frequency of a customer NOT being found whenever a taxi passed through 
	 * this segment in a particular duration of time of the day.
	 * <br>0 - 12:00am to 5:59am
	 * <br>1 - 6:00am to 11:59am
	 * <br>2 - 12:00pm to 5:59pm
	 * <br>3 - 6:00pm to 11:59pm
	 */
	public double[] freqFailure = new double[timeSlot];
	
	/**
	 * Is the length of the distance in metres traveled from this segment.
	 */
	public double[] distanceTravelled = new double[timeSlot];

	/**
	 * Is the time (in seconds) required to cross the segment from start location to end location.
	 */
	public long timeToCrossSegment;

	/**
	 * Creates a new instance given the start location, end location, segment length, and taxi speed
	 * @param startLocation The start location
	 * @param endLocation The end location
	 * @param taxiSpeed The speed of the taxi in kmph
	 * @param segmentLength The length of the road segment in metres
	 */
	public Segment(Location startLocation, Location endLocation, double segmentLength, double taxiSpeed)
		{
		this.startLocation = startLocation;
		this.endLocation = endLocation;
		this.segmentLength = segmentLength;
		
		for(int i = 0 ; i < timeSlot ; ++i)
			{
			this.distanceTravelled[i] = 0.0;
			this.freqSuccess[i] = 0;
			this.freqFailure[i] = Double.MIN_VALUE;
			}
		
		this.setTimeToCrossSegment(taxiSpeed);
		}

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
	public Segment(Location startLocation, Location endLocation, double segmentLength, double[] distanceTravelled,
			double[] freqSuccess, double[] freqFailure, long timeToCrossSegment)
		{
		this.startLocation = startLocation;
		this.endLocation = endLocation;
		this.segmentLength = segmentLength;
		this.distanceTravelled = distanceTravelled;
		this.freqSuccess = freqSuccess;
		this.freqFailure = freqFailure;
		this.timeToCrossSegment = timeToCrossSegment;
		}

	/**
	 * Checks if the passed location lies in epsilon neighbourhood of this segment.
	 * @param location The location to be tested
	 * @param width The width of the epsilon neighbourhood around this segment
	 * @return {@code true} if the passed location lies in the neighbourhood, {@code false} otherwise.
	 */
	public boolean isInBuffer(Location location, double width)
		{
		double a1 = startLocation.longitude;
		double a2 = endLocation.longitude;
		double b1 = startLocation.latitude;
		double b2 = endLocation.latitude;
		
		double a2MinusA1 = a2 - a1;
		double b2MinusB1 = b2 - b1;
		double rootSumOfSquares = Math.sqrt(b2MinusB1 * b2MinusB1 + a2MinusA1 * a2MinusA1);
		double c = width * b2MinusB1 / rootSumOfSquares;
		double cTimesSlope = -1 * width * a2MinusA1 / rootSumOfSquares;
		
//		outputPoints(a1, b1, a2, b2, c, cTimesSlope, location);
		
		double p = b2MinusB1 * (location.longitude - a1 - c) - a2MinusA1 * (location.latitude - b1 - cTimesSlope);
//		System.out.println("p = " + p);
		double q = b2MinusB1 * (location.longitude - a1 + c) - a2MinusA1 * (location.latitude - b1 + cTimesSlope);
//		System.out.println("q = " + q);
		double r = a2MinusA1 * (location.longitude - a1 + c) + b2MinusB1 * (location.latitude - b1 + cTimesSlope);
//		System.out.println("r = " + r);
		double s = a2MinusA1 * (location.longitude - a2 + c) + b2MinusB1 * (location.latitude - b2 + cTimesSlope);
//		System.out.println("s = " + s);
		
		double pTimesQ = p * q;
		double rTimesS = r * s;
		if(pTimesQ <= 0 && rTimesS <= 0)
			return true;
		return false;
		}

	/**
	 * This function estimates the time (in seconds) required to cross this segment using the length 
	 * of the segment and the speed of the taxi passed as a parameter.
	 * @param taxiSpeed The speed of the taxi in kmph
	 */
	public void setTimeToCrossSegment(double taxiSpeed)
		{
		//convert the speed from kmph to metres per second
		taxiSpeed *= 5.0 / 18.0;
		this.timeToCrossSegment = (long) (this.segmentLength / taxiSpeed);
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
	
	public double calculateCost()
		{
		return this.segmentLength * GAS / MILEAGE + this.timeToCrossSegment * COMPANY_FARE;
		}
	}
