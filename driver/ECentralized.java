package driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

import TaxiRecommendation.Main;

/***
 * @author nandani
 * 
 * This file contains various parameters used for testing.
 * Main driver class for both baseline and uct algorithm
 * 
 */




public class ECentralized {

	/**
	 *	Defining all the parameters 
	 */
	
	/**
	 * dataCategory can be either {"Normal","Top","last"}
	 * Normal- x% of total drivers data used for training ,rest all the drivers data used for testing 
	 * Top - top x% drivers(based on how many trips a driver has) data used for training,rest all drivers data used for testing
	 * last - last x % drivers data used for training,rest all drivers data used for testing
	 */
	public static String dataCategory="Top";
	
	
	/**
	 * testDataType={"AllSegments","Top5Neighbours","Bottom5Neighbours"} 
	 * 
	 */

	public static String testDataType="AllSegments";/*,"Top10Segments","Bottom10Segments","Bottom20Segments"*/
	
	/***
	 * dataType ={10,25,50,75,90}
	 * Percentage of data used for training UCT algorithm. 
	 * 
	 */
	
	public static int dataType[]={15};
	
	/**
	 * distance = {1000,2000,3000,5000} in metres
	 * Distance threshold after which we stop the recommendation from a particular segment in both UCT and baseline.
	 * 
	 */
	public static int distance[]={1000};
	
	//String x[]={"0.25","0.5","1.0","1.5","2.0","5.0","10.0","15.0","20.0"};
	
	
	/**
	 * How many times recommendation should be made from a particular segment
	 * 
	 */	
	public static int[] noOfTimesRecommendationSegment={10};//{1,5,10,15,20,25,30};
	
	
	/**
	 * Represents the current phase of the system.There are 2 phases,
	 * "Training" and "Testing" which is set according to the operation we 
	 * are performing
	 */
	
	
	public static String phase="Training";
	
	
	/**
	 *  Trajectories represents given a segment how many times 
	 * 	UCT tree will be updated for that particular segment
	*/
	
	public static int trajectories =500;
	
	
	/**
	costPerMetre is the cost of travelling on that segment per metre
	**/
	public static double costPerMetre=0.25;/*{0.25,0.5,1.0,1.5,2.0,3.0,5.0,10.0};*/
	
	
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
	public static double testingCost = 5.0;/*{0.25,0.5,1.0,1.5,2.0,5.0,10.0,15.0,20.0};*/
	
	
	
	public static String experimentsType="UCT_TESTING";/*"UCT_TESTING","UCT_TRAINING"*///*"BASELINE"*/};
	
	
	
	public static String INPUT_FOLDER_PATH= "/home/nandani/Documents/sem-3/taxi-project2/";
	
	public static String PREDICTION_SEGMENTS_PATH="TestingData/Normal75/CompleteTest.csv";
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ArrayList<String> predictionSegments=new ArrayList<>();
		
		
		try {
			
		
		
		
		// Trigger

		for(int y=0;y<dataType.length;y++)
		{
			
			//String PREDICTION_SEGMENTS_PATH="TestingData/"+ dataCategory+dataType[y]+"/Top5%Neighbours.csv";
			FileInputStream f1=new FileInputStream(PREDICTION_SEGMENTS_PATH);
			BufferedReader br=new BufferedReader(new InputStreamReader(f1));
			String s1=" ";
			
			while((s1=br.readLine())!=null)
			{
				String segments[]=s1.split(",");
				predictionSegments.add(segments[0].trim());
			}
			
			
			
	
			System.out.println(dataType[y]);

			for(int m=0;m<distance.length;m++)
			{

			
				for(int k=0	;k<noOfTimesRecommendationSegment.length;k++)
				{

			
				String TRAIN_INPUT_SEGMENT_FILE=INPUT_FOLDER_PATH+"Datasets/SFExperimentData/"+dataCategory+dataType[y]+"/CompleteSegmentDataTrain.csv";
				String TEST_INPUT_SEGMENT_FILE=INPUT_FOLDER_PATH+"Datasets/SFExperimentData/"+dataCategory+dataType[y]+"/CompleteSegmentDataTest.csv";
				//PrintStream UCTOutputFile=null;
				PrintStream UCTOutputFile=new PrintStream(new File("UCT/"+dataCategory+dataType[y]+"/"+distance[m]+"m"+"/C"+ucb_ScalerMatrix+"/cost"+costPerMetre+"/"+Centralized.testDataType+"/"+Centralized.noOfTimesRecommendationSegment[k]+"/TestC"+testingUcb_scaler+"/TestCost"+testingCost+"/Output.csv"));
				String pathOfLearntParameters="LearntParameters/"+dataCategory+dataType[y]+"/"+distance[m]+"m"+"/C"+ucb_ScalerMatrix+"/cost"+costPerMetre+"/LearntParameters.csv";
				

				System.out.println("Parameters used");
				
					try {	

						



						if(experimentsType=="UCT_TRAINING")
						{
							phase="Training";
							uct.DemoUCT1.main(null,phase,TRAIN_INPUT_SEGMENT_FILE,TEST_INPUT_SEGMENT_FILE,UCTOutputFile,predictionSegments,pathOfLearntParameters,distance[m],noOfTimesRecommendationSegment[k]);


						}

						if(experimentsType=="UCT_TESTING")
						{

							phase="Testing";
							uct.DemoUCT1.main(null,phase,TRAIN_INPUT_SEGMENT_FILE,TEST_INPUT_SEGMENT_FILE,UCTOutputFile,predictionSegments,pathOfLearntParameters,distance[m],noOfTimesRecommendationSegment[k]);


						}
						
						
						if(experimentsType=="BASELINE")
						{
							PrintStream writeTestCompleteData=new PrintStream(new File("TestingData/"+dataCategory+dataType[y]+"/CompleteSegments.csv"));
							PrintStream BaseOutputFile=new PrintStream(new File("Baseline/"+dataCategory+dataType[y]+"/"+distance[m]+"m/"+testDataType+"/"+Centralized.noOfTimesRecommendationSegment[k]+"/Output.csv"));
							TaxiRecommendation.Main.main(null,TRAIN_INPUT_SEGMENT_FILE,TEST_INPUT_SEGMENT_FILE,BaseOutputFile,writeTestCompleteData,predictionSegments,distance[m],noOfTimesRecommendationSegment[k]);
						} 
		
					}catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}



				
				
					
				
			}
			
		}		//m loop
	}		//y loop
		
		} //END OF OUTER TRY	
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
}






}