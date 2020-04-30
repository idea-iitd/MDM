package recomendation;

public class SegmentSimulator implements Runnable
	{
	public static final double simulationSpeed = 1;
	private SegmentProcess segment;
	private int timeSlot;
	
	public SegmentSimulator(SegmentProcess segment, int timeSlot)
		{
		this.segment = segment;
		this.timeSlot = timeSlot;
		}
	
	public void run()
		{
		double pickupProbability = this.segment.findPickupProbability(this.timeSlot);
		long maxDelay = (long) (this.segment.timeToCrossSegment / simulationSpeed);
		
		/* path is being simulated.. */
		double randomNumber = Math.random();
		long delay;
		if(randomNumber < pickupProbability)
			delay = (long) (Math.random() * (double) maxDelay);
		else
			delay = maxDelay;
		try
			{
			Thread.sleep(delay);
			}
			catch(InterruptedException e)
				{
				e.printStackTrace();
				}
		
		/* updating appropriate frequencies */
		if(randomNumber < pickupProbability)
			++this.segment.freqSuccess[this.timeSlot];
		else
			++this.segment.freqFailure[this.timeSlot];
		
		
		}
	
//	private double minProfit;
//	private double maxProfit;
//	private double scaleSimulationSpeed;
//    private BlockingQueue<ArrayList> singleQueue; 
//    private Integer timeStep;
//	
//	@Override
//	public void run()
//		{
//		//calculate actual cost of the route
////		double cost = 0;
////		//TODO: update cost
////		for(int i = 0 ; i < segmentIndex ; ++i)
////			{
////			cost += route.get(i).calculatePotentialCost();
////			}
////			cost += route.get(segmentIndex).calculateCost();
//		double profit = 0.0;
//		profit = Main.findAllNetProfitPerRoute(route, timeSlot, 1);
//		
//		
//		
////		System.out.println("min cost = " + minProfit);
////		System.out.println("max cost = " + maxProfit);
//		
////		System.out.println("cost = " + profit);
//		
//		double normalizedProfit = (profit - minProfit) / (maxProfit - minProfit);
////		if( normalizedProfit < 0 )
////		   System.out.println("STUPID    XXXXXXXXXXXXXXXXXXXXXXXXXXX");
//		
////		System.out.println("normalized cost = " + normalizedProfit);
//		ArrayList value = new ArrayList();
//		value.add(timeStep);
//		value.add(normalizedProfit);
//		value.add(profit);
//		
//		
//		try {
//                   synchronized (singleQueue) {
//                	   // Since cost incurred is loss for Taxi Driver so we are making it negative to bring the notion of loss 
//                     singleQueue.put(value);
//                   
//                  
//                   }
//                   
//                } catch (InterruptedException ex) {
//                     System.out.println(" problem in SImulatePath");
//                }
//		
////		System.out.println( " XXX");
//		}
	
	

	
	
	}