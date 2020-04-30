/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ilo;

/**
 *
 * @author sairam
 */

import learners.ThomsonSampling;
import learners.Ucb;
import engine.Engine;
import engine.Statistics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import jdk.internal.dynalink.beans.StaticClass;

import com.sun.org.apache.regexp.internal.recompile;

import opponents.TaxiBandits;
import recomendation.Feedback;
import recomendation.Location;
import recomendation.Main;
import recomendation.RoadNetwork;
import recomendation.Segment;
import recomendation.SegmentProcess;
import tools.Pair;
import tools.ReadAndWiteBanditInfoToFile;

public class main {

	/**
	 * @param args
	 */
	// TIME_SLOT 0 -> 0600 to 1759 , 1 -> 1800 to 0059 , 2 -> 0100 to 0559
	public static int TIME_SLOT = 2;	//can take values 0, 1, or 2 depending on when the recommendation is asked
	public static int MAX_ARMS = 10;
	public static double SIMULATION_SPEED = 1;
	static boolean appendTofile = false;  // for the first Time open the File for remaining segment append to the end
	

//	public static String INPUT_1000_RANDOM_SEGMENTS_FROM_FILE = "random1000Segments.txt";
	public static String INPUT_1000_RANDOM_SEGMENTS_FROM_FILE = "/home/animesh/Desktop/datamining/Bandit/random826Segments.csv";
	public static String OUTPUT_CLUSTER_OF_1000_SEGMENTS = "cluster1000Segments.txt";
	public static String OUTPUT_MEAN_TO_FILE ="accumilatedMeans.txt";
	
	
	public static ArrayList<SegmentProcess> routes = new ArrayList<SegmentProcess>();
	
       public static double[] getFreq(double a, double b, double c, double d)
		{
		double[] r = new double[4];
		r[0] = a; r[1] = b; r[2] = c; r[3] = d;
		return r;
		}
       
       
           
        
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		final int numberOfRounds = 20; // Horizon of the bandit problem.   
		final int numberOfPlays = 10; //Number of times we repeat experiment. 
		int noOfTimeSlot = 3;
		double[][] accumilatedMeans;
//		RoadNetwork roadNetwork = new RoadNetwork()
		RoadNetwork.populateSegmentProcessIdMap(noOfTimeSlot);
                
                
//        ArrayList<Segment> route1 = new ArrayList<Segment>();
//		ArrayList<Segment> route2 = new ArrayList<Segment>();
		       
		// Loop to run the algorithm for 1000 random segments
		ArrayList<tools.Pair> pairs = tools.ReadAndWiteBanditInfoToFile.read1000RandomSegments(INPUT_1000_RANDOM_SEGMENTS_FROM_FILE);
		accumilatedMeans = new double[pairs.size()][numberOfRounds];
		for (int i = 0; i < pairs.size(); i++) {
			Pair pair = pairs.get(i);
			recomendation.Main.start = pair.start;
			recomendation.Main.end = pair.end;


			//TODO:
			//        routes = rootToLeaves;
//			ArrayList<ArrayList<SegmentProcess>> routes;
			System.out.println("Start Segment Id : "+recomendation.Main.start+" "+recomendation.Main.end);
			Main m = new Main();
			routes = m.main2(TIME_SLOT);  // Run only if need to change cluster 
//			routes = ReadAndWiteBanditInfoToFile.readClusteredFile(OUTPUT_CLUSTER_OF_1000_SEGMENTS, recomendation.Main.start, recomendation.Main.end);
			if(routes == null)
			{
				System.out.println("This Segment has either one routes or one cluster of routes so need for recommendation");
				continue;
			}

//			ReadAndWiteBanditInfoToFile.writeClusteredFile(OUTPUT_CLUSTER_OF_1000_SEGMENTS, routes, appendTofile);
//			appendTofile = true;


			ArrayList<ArrayList<SegmentProcess>> t = new ArrayList<ArrayList<SegmentProcess>>();
			t.add(routes);
			long delayPeriod = (long)RoadNetwork.findMaxTimeToCrossSegment(t);
			//        double time1 = Main.findMinTimeToCrossSegment(routes);
			System.out.println("Maximum time to cross route : "+delayPeriod);

			TaxiBandits tb = new TaxiBandits(t);

			//banditInfo = banditInfoReader(routes);
			//        banditInfoWriter(routes, banditInfo);






			// Use UCB() or any other algorithm in package learners.
			final Engine engine = new Engine(new Ucb(),tb);

			// Use ts() or any other algorithm in package learners.
			//		final Engine engine = new Engine(new ThomsonSampling(),tb);



			//Read the file containing the banditInfo
			String fileName = engine.getLearner().getName() + "_vs_" + engine.getStochasticOpponent().getName() + "_TIME_SLOT"+ TIME_SLOT + "_banditInfo.txt";
			ReadAndWiteBanditInfoToFile rw = new ReadAndWiteBanditInfoToFile( t , fileName );
			Double[] banditInfo = rw.banditInfoReader(t); 



			/**  Method to handle cases when when numberOfRounds * numberOfPlays is huge,
			 * avoids Java memory limitations (Java.heap) but slower due to hard drive access.
			 * e.g. numberOfRounds = 5000,  numberOfPlays = 20000.
			 **/
			/*
		// Create Data
		String s = engine.getLearner().getName() + "_vs_" + engine.getStochasticOpponent().getName()+
				"_T"+numberOfRounds + "_N"+ numberOfPlays;
		Statistics.fprintMultiplePlaysData(engine,s+".dat",numberOfRounds,numberOfPlays);
		System.out.println("Data generated");


		// Compute statistics on Data
		double[] quantiles = {0.2,0.4,0.6,0.8,0.95,0.99};
		double maxMean = engine.getStochasticOpponent().getMaxMean(); 
		Statistics.fprintStatisticsFromMultiplePlaysData(s+".dat", s+"_Statistics.dat", maxMean, numberOfRounds, numberOfPlays, quantiles);
		System.out.println("Statistics computed");

		// Plot Statistics
		Gnuplot.makeGnuplotFile(s,numberOfRounds,numberOfPlays,quantiles);
		Gnuplot.executeGnuplot(s);
			 */


			/**  Method to handle cases when when numberOfRounds * numberOfPlays is small, 
			 *   so that everything can be processed in Java memory. 
			 *   Here small means: does not cause Java.heap memory error.
			 *   e.g. numberOfRounds = 1000,  numberOfPlays = 2000.
			 **/

			// Create Data
			//String s = engine.getLearner().getName() + "_vs_" + engine.getStochasticOpponent().getName()+
			//		"_T"+numberOfRounds + "_N"+ numberOfPlays;

			String s = fileName +  "_T"+numberOfRounds + "_N"+ numberOfPlays;		

			Integer[][] optimalActions = new Integer[numberOfPlays][numberOfRounds];
			Integer[][] subOptimalActions = new Integer[numberOfPlays][numberOfRounds];


			//Set the banditinfo in the engine
			engine.setBanditInfo(banditInfo);

			ArrayList<double[][]>  retVals = engine.repeatPlays(numberOfRounds,numberOfPlays,optimalActions,subOptimalActions, delayPeriod);
			double[][] cumr =  retVals.get(0);
			double[][] actualCosts = retVals.get(1);


			//Write the bandit info with the corresponding routes to the file
			banditInfo =  engine.getLearner().getBanditInfo();
			rw.banditInfoWriter(t, banditInfo);

			//banditInfoWriter(routes, banditInfo);





			Statistics.fprintData(s+".dat",cumr,numberOfRounds,numberOfPlays);		
			System.out.println("Data generated");


			double maxMean = engine.getStochasticOpponent().getMaxMean();


			//Compute cumulative regret mean 
			Statistics.fprintCumRegMeans(s+"_CumRegMeans", maxMean, cumr, numberOfRounds, numberOfPlays);

			// Compute statistics on Data
			double[] quantiles = {0.2,0.4,0.6,0.8,0.95,0.99};


			Statistics.fprintStatistics(s+"_Statistics.dat",cumr,maxMean,numberOfRounds,numberOfPlays,quantiles);
			System.out.println("Statistics computed");
			
			Statistics.fprintMeans(s+"_Means.dat", actualCosts, numberOfRounds, numberOfPlays,accumilatedMeans , i);
			System.out.println("Means computed");



			// Plot Statistics
			Gnuplot.makeGnuplotFile(s,numberOfRounds,numberOfPlays,quantiles);
			Gnuplot.executeGnuplot(s);

		}
		tools.ReadAndWiteBanditInfoToFile.writeMeanToFile(accumilatedMeans, OUTPUT_MEAN_TO_FILE);
		
	}

}