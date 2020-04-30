package recomendation;

import java.util.ArrayList;

public class PoissonParameterCalculator
	{
	/**
	 * This function returns the lambda parameter (in persons per minute) for a given route in a given time slot.
	 * @param route The route for which the lambda parameter is to be found
	 * @param timeSlot The time of the day
	 * @return The lambda parameter
	 */
	public static double calculateLambda(ArrayList<SegmentProcess> route, int timeSlot)
		{
		double freqSuccessInRoute = 0.0;
		double timeSpentInRoute = 0.0;
		for(SegmentProcess segment : route)
			{
			freqSuccessInRoute += segment.freqSuccess[timeSlot];
			double timeSpentInSegment = (segment.freqSuccess[timeSlot] + segment.freqFailure[timeSlot]) * segment.timeToCrossSegment;
//			System.out.println("freqSuccess: " + segment.freqSuccess[timeSlot]);
//			System.out.println("freqFailure: " + segment.freqFailure[timeSlot]);
//			System.out.println("time in segment: " + timeSpentInSegment);
			timeSpentInRoute += timeSpentInSegment;
//			System.out.println("time in route: " + timeSpentInRoute);
			}
		
//		System.out.println("freqSuccessInRoute = " + freqSuccessInRoute);
//		System.out.println("timeSpentInRoute = " + timeSpentInRoute);
		
		double totalTimeSpent = 0.0;
		switch (timeSlot) {
		case 0:
			totalTimeSpent = 60 * 5 * 30;  // time in min hours days  
			break;
		case 1: 
			totalTimeSpent = 60 * 11 * 30; 
			break;
		case 2:
			totalTimeSpent = 60 * 8 * 30;
			break;

		}
//		double lambda = (freqSuccessInRoute / timeSpentInRoute * 60.0) / totalTimeSpent;
		double lambda = (freqSuccessInRoute / totalTimeSpent);
		// to test
//		lambda = .0000119581;
		
		return lambda;
		}

//	public static void main(String[] args)
//		{
//		ArrayList<SegmentProcess> route = new ArrayList<SegmentProcess>();
//		double fS1[] = {3, 4, 5};
//		double fF1[] = {2, 8, 1};
//		route.add(new SegmentProcess(null, null, 0, fS1, fF1, 2000));
//		
//		double fS2[] = {5, 6, 4};
//		double fF2[] = {3, 7, 7};
//		route.add(new SegmentProcess(null, null, 0, fS2, fF2, 4000));
//
//		double fS3[] = {1, 6, 3};
//		double fF3[] = {5, 4, 0};
//		route.add(new SegmentProcess(null, null, 0, fS3, fF3, 2500));
//		
//		System.out.println("lambda = " + calculateLambda(route, 0));
//		}
	}