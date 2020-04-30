package driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.math.*;

import preprocessing.SelectiveTestingData;

import TaxiRecommendation.Main;

/***
 * @author nandani
 * 
 * This file contains various parameters used for testing.
 * Main driver class for both baseline and uct algorithm
 * 
 */




public class CentralizedCrossValidation {

	/**
	 *	Defining all the parameters 
	 */
	
	/**
	 * dataCategory can be either {"Normal","Top","last"}
	 * Normal- x% of total drivers data used for training ,rest all the drivers data used for testing 
	 * Top - top x% drivers(based on how many trips a driver has) data used for training,rest all drivers data used for testing
	 * last - last x % drivers data used for training,rest all drivers data used for testing
	 */
	public static String dataCategory;
	
	
	/**
	 * testDataType={"AllSegments","Top5Neighbours","Bottom5Neighbours"} 
	 * 
	 */

	public static String testDataType;/*,"Top10Segments","Bottom10Segments","Bottom20Segments"*/
	
	/***
	 * dataType ={10,25,50,75,90}
	 * Percentage of data used for training UCT algorithm. 
	 * 
	 */
	
	public static int dataType[]; //25,50,75,90};
	
	/**
	 * distance = {1000,2000,3000,5000} in metres
	 * Distance threshold after which we stop the recommendation from a particular segment in both UCT and baseline.
	 * 
	 */
	public static int distance[];
	
	//String x[]={"0.25","0.5","1.0","1.5","2.0","5.0","10.0","15.0","20.0"};
	
	
	/**
	 * How many times recommendation should be made from a particular segment
	 * 
	 */	
	public static int noOfTimesRecommendationSegment;//,10,15,20,25,30,35,40,45,50};

	
	/**
	 * Represents the current phase of the system.There are 2 phases,
	 * "Training" and "Testing" which is set according to the operation we 
	 * are performing
	 */
	
	
	public static String phase="";
	
	
	/**
	 *  Trajectories represents given a segment how many times 
	 * 	UCT tree will be updated for that particular segment
	*/
	
	public static int trajectories;
	
	
	/**
	costPerMetre is the cost of travelling on that segment per metre
	**/
	public static double costPerMetre; //{0.125,0.25,0.5,1.0,3.0,5.0,10.0};
	
	
	public static double ucb_ScalerMatrix=1.41421356237;/*{0.25,0.5,1.0,1.41421356237,2.0,3.0,5.0,10.0};*/
	
	
	
	/**	
	 * C value
	 *//*
	public static double ucb_scaler =1.41421356237;
	*/
	
	/**
	 * Testing C value
	 */
	public static double  testingUcb_scaler = 1.41421356237;/*{0.25,0.5,1.0,1.41421356237,2.0,3.0,5.0,10.0,15.0,20.0};*/
	
	

	/**
	 * Testing Cost value
	 */
	public static double testingCost;
	
	
	
	public static String experimentsType;/*"UCT_TESTING","UCT_TRAINING"*///*"BASELINE"*/};
	
	
	
	public static String INPUT_FOLDER_PATH= "/home/nandani/Documents/sem-3/taxi-project2/";
	
	/**
	 * 
	 * No of timeslots in which data is getting divided
	 */
	public static int noOfTimeslots=4;
	
	
	/**
	 * Current timeslot of the day
	 * 
	 */
	public static int timeslot;
	
	
	/**
	 * Various timeslots range
	 */
	public static String[] timeslotRange=new String[]{"0-7","8-11","12-16","17-23"};
	
	/**
	 * 
	 * percentage of testing data to be used(top x% segments out of non-zero frequency segments and their 2-hop neighbour)
	 */
	public static int testDataPercentage;

	public static int noOfTimesCrossValidation = 10;
	
	
	
	//public static String PREDICTION_SEGMENTS_PATH="TestingData/SegmentWithNeighbours.csv";
	
	//public static String PREDICTION_SEGMENTS_PATH="TestingData/Normal75/CompleteSegments.csv";
	
	
	
	public static void main(String[] args) {
		
		
		//populating all the arguments
			dataCategory=args[0];
			System.out.print("dataCategory==>");
			System.out.println(dataCategory);
			testDataType = args[1];
			String[] dataList = args[2].split(",");
			//populating different train or test data
			dataType=new int[dataList.length];
			for(int s=0;s<dataList.length;s++)
			{
				dataType[s] = Integer.parseInt(dataList[s]);
			}
			String[] distanceList = args[3].split(",");
			//populating distance parameter
			distance=new int[distanceList.length];
			for(int s=0;s<distanceList.length;s++)
			{
				distance[s] = Integer.parseInt(distanceList[s]);
			}
			noOfTimesRecommendationSegment=Integer.parseInt(args[4]);
			costPerMetre = Double.parseDouble(args[5]);
			testingCost = Double.parseDouble(args[6]);
			experimentsType = args[7];
			timeslot = Integer.parseInt(args[8]);
			System.out.print("timeslot==>");
			System.out.println(timeslot);
			System.out.print("traincost==>");
			System.out.println(costPerMetre);
			testDataPercentage = Integer.parseInt(args[9]);
			trajectories = Integer.parseInt(args[10]);
			
		
		
		
		
		
		try {
				
		// Trigger

		for(int y=0;y<dataType.length;y++)
		{
			
			for(int c=1;c<=noOfTimesCrossValidation;c++)
			{
			ArrayList<String> predictionSegments=new ArrayList<>();
			
			
			String PREDICTION_SEGMENTS_PATH=INPUT_FOLDER_PATH+"Bandit/TestingData/"+dataCategory+dataType[y]+"/NewCompleteSegments_"+c+".csv";

			FileInputStream f1=new FileInputStream(PREDICTION_SEGMENTS_PATH);
			BufferedReader br=new BufferedReader(new InputStreamReader(f1));
			String s1=" ";
			
			while((s1=br.readLine())!=null)
			{
				String segments[]=s1.split(","); 
				predictionSegments.add(segments[0].trim());
			}
			
			
			System.out.print("dataType==>");
			System.out.println(dataType[y]);

			//implementing cross-valdation

			
			
			
			for(int m=0;m<distance.length;m++)
			{	
				
				
				String TRAIN_INPUT_SEGMENT_FILE=INPUT_FOLDER_PATH+"Datasets/SFExperimentData/"+dataCategory+dataType[y]+"/CompleteSegmentDataTrain_"+c+".csv";
				String TEST_INPUT_SEGMENT_FILE=INPUT_FOLDER_PATH+"Datasets/SFExperimentData/"+dataCategory+dataType[y]+"/CompleteSegmentDataTest_"+c+".csv";
				//String TEST_INPUT_SEGMENT_FILE="/home/nandani/Documents/sem-3/taxiNYC/NYExperimentData/ManhattanData/Normal/output/Test10.csv";
				
				//SelectiveTestingData obj = new SelectiveTestingData();
				//predictionSegments = obj.choosetestData(PREDICTION_SEGMENTS_PATH,timeslot,testDataPercentage,TEST_INPUT_SEGMENT_FILE,testDataType);
				System.out.println(predictionSegments.size());
				
			
				
				 
			        PrintStream UCTOutputFile=null;
				String pathOfLearntParameters=INPUT_FOLDER_PATH+"Bandit/LearntParameters/N"+dataCategory+dataType[y]+"/t"+timeslot+"/"+distance[m]+"m"+"/C"+ucb_ScalerMatrix+"/cost"+costPerMetre+"/C"+c+"MNewLearntParameters2"+"-"+trajectories+".csv";

				//String pathOfLearntParameters=INPUT_FOLDER_PATH+"Bandit/LearntParameters/N"+dataCategory+dataType[y]+"/t"+timeslot+"/"+distance[m]+"m"+"/C"+ucb_ScalerMatrix+"/cost"+costPerMetre+"/MNewLearntParameters2.csv";
				//String path= "UCT/N"+dataCategory+dataType[y]+"/t"+timeslot+"/"+distance[m]+"m"+"/C"+ucb_ScalerMatrix+"/cost"+costPerMetre+"/"+Centralized.testDataType+"/"+Centralized.noOfTimesRecommendationSegment+"/TestC"+testingUcb_scaler+"/TestCost"+testingCost+"/MOutput.csv";
				//System.out.println(path);
				

					try {	

						


						
						if(experimentsType.equals("UCT_TRAINING"))
						{
							phase="Training";
							uct.DemoUCT1.main(null,phase,TRAIN_INPUT_SEGMENT_FILE,TEST_INPUT_SEGMENT_FILE,UCTOutputFile,predictionSegments,pathOfLearntParameters,distance[m],noOfTimesRecommendationSegment);
							UCTOutputFile.close();

						}
						
						if(experimentsType.equals("UCT_TESTING"))
						{

							phase="Testing";
							//UCTOutputFile=new PrintStream(new File(INPUT_FOLDER_PATH+"Bandit/UCT/N"+dataCategory+dataType[y]+"/t"+timeslot+"/"+distance[m]+"m"+"/C"+ucb_ScalerMatrix+"/cost"+costPerMetre+"/"+Centralized.testDataType+"/"+Centralized.noOfTimesRecommendationSegment+"/TestC"+testingUcb_scaler+"/TestCost"+testingCost+"/FOutput.csv"));
							UCTOutputFile=new PrintStream(new File(INPUT_FOLDER_PATH+"Bandit/UCT/N"+dataCategory+dataType[y]+"/t"+timeslot+"/"+distance[m]+"m"+"/C"+ucb_ScalerMatrix+"/cost"+costPerMetre+"/"+Centralized.testDataType+"/"+Centralized.noOfTimesRecommendationSegment+"/TestC"+testingUcb_scaler+"/TestCost"+testingCost+"/C"+c+"FOutput"+"-"+trajectories+".csv")); 
							uct.DemoUCT1.main(null,phase,TRAIN_INPUT_SEGMENT_FILE,TEST_INPUT_SEGMENT_FILE,UCTOutputFile,predictionSegments,pathOfLearntParameters,distance[m],noOfTimesRecommendationSegment);
							UCTOutputFile.close();

						}
						
						
						if(experimentsType.equals("BASELINE"))
						{
							
							PrintStream BaseOutputFile=new PrintStream(new File(INPUT_FOLDER_PATH+"Bandit/Baseline/N"+dataCategory+dataType[y]+"/t"+timeslot+"/"+distance[m]+"m/"+testDataType+"/"+noOfTimesRecommendationSegment+"/C"+c+"FOutput.csv"));
							TaxiRecommendation.Main.main(null,TRAIN_INPUT_SEGMENT_FILE,TEST_INPUT_SEGMENT_FILE,BaseOutputFile,predictionSegments,distance[m],noOfTimesRecommendationSegment);
							BaseOutputFile.close();
						} 
		
					}catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


					
				
				} //c loop
			
			
		}		//m loop
	}		//y loop
		
		} //END OF OUTER TRY	
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
}






}
