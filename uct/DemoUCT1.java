
package uct;
import java.io.BufferedReader;
import driver.Centralized;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import recomendation.Main;
import recomendation.RoadNetwork;
import recomendation.SegmentProcess;
import uct.UCTNodes1.UCTNodeStore1;
import uct.UCTNodes1.UCTStateNode1;




public class DemoUCT1  {
	
	/**
	 * Object of RoadNetwork class
	 */
	public static RoadNetwork roadNetwork;
	
	/**
	 * timeSlot is the number of slot in a day 
	 */
	 
	public static int timeSlot = Centralized.timeslot;
	
	/**
	 * No of timeslots in which day is divided
	 */
	
	public static int noOfTimeslot = Centralized.noOfTimeslots;
	
	
	
	//public static String trainingDataType="75";
	
	/**	
	 * This file contains data about all road segments, as required by the recommender for training.
	 * <p>
	 * <dl>
	 * <dt>file format:
	 * <dd>&lt;start location ID&gt;,&lt;start location latitude&gt;,&lt;start location longitude&gt;;
	 * 		&lt;end location ID&gt;,&lt;end location latitude&gt;,&lt;end location longitude&gt;;
	 * 		&lt;segment length in metres&gt;;&lt;frequencies of success separated by commas&gt;;
	 * 		&lt;frequencies of failure separated by commas;&gt;&lt;time required to cross the segment in seconds&gt;
	 * </dl>
	 * TODO:
	 * example: 
	 */
	//public static String TRAIN_INPUT_SEGMENT_FILE = "/home/nandani/Documents/sem-3/taxi project2/Sa_dataset/ExperimentData/"+trainingDataType+"/CompleteSegmentDataTrain.csv";
	
	
	/**
	 * This file contains data about all road segments, as required by the recommender for testing.
	 * <p>
	 * <dl>
	 * <dt>file format:
	 * <dd>&lt;start location ID&gt;,&lt;start location latitude&gt;,&lt;start location longitude&gt;;
	 * 		&lt;end location ID&gt;,&lt;end location latitude&gt;,&lt;end location longitude&gt;;
	 * 		&lt;segment length in metres&gt;;&lt;frequencies of success separated by commas&gt;;
	 * 		&lt;frequencies of failure separated by commas;&gt;&lt;time required to cross the segment in seconds&gt;
	 * </dl>
	 * TODO:
	 * example: 
	 */
	
	//public static String TEST_INPUT_SEGMENT_FILE= "/home/nandani/Documents/sem-3/taxi project2/Sa_dataset/ExperimentData/"+trainingDataType+"/CompleteSegmentDataTest.csv";
	
	
	//Sorted data based on frequency used for testing
	public static ArrayList<String> sortedTestData=new ArrayList<String>();
	
	
	public static PrintStream out1;
	
	

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
	
	public static int trajectories=Centralized.trajectories;
	
	
	/**
	costPerMetre is the cost of travelling on that segment per metre
	**/
	public static double costPerMetre=Centralized.costPerMetre;
	
	
	/**
	 * C value
	 */
	public static double ucb_scaler=Centralized.ucb_ScalerMatrix;
	
	//NEWLY ADDED
	/**
	 * Testing C value
	 */
	public static double testingUcb_scaler=Centralized.testingUcb_scaler;
	
	

	//NEWLY ADDED
	/**
	 * Testing Cost value
	 */
	public static double testingCost=Centralized.testingCost;
	
	
	/**
	 * dataCategory can be either {"Normal","Top","last"}
	 * Normal- x% of total drivers data used for training ,rest all the drivers data used for testing 
	 * Top - top x% drivers(based on how many trips a driver has) data used for training,rest all drivers data used for testing
	 * last - last x % drivers data used for training,rest all drivers data used for testing
	 */
	public static String dataCategory="Normal";
	
	
	
	
	/** 
	 * distanceThreshold represents distance in metres upto which we will recommend.
	 * After 5kms we will start from the starting segment again 
	*/
	
	//public static int distanceThreshold=5000;
	
	
	/**
	 * The name of the file that store all the values learnt using training phase.
	 *//*
	
	//String pathOfLearntParameters="\\C:\\Users\\Nandani garg\\Desktop\\taxi project2\\Results\\LearntParametersSanF.txt";
	///home/nandani/Documents/sem-3/taxi project2/taxi_results/UCT/LearntParameters/Normal10/1000m
	//String pathOfLearntParameters="/home/nandani/Documents/sem-3/taxi project2/taxi_results/UCT/LearntParameters/Normal"+trainingDataType+"/"+distanceThreshold+"m"+"/C"+ucb_scaler+"/cost_"+costPerMetre+"_traj_"+trajectories+".txt";
	public static String pathOfLearntParameters=" ";*/
	/**
	 *  countOfTimestep is used here to keep count of going from starting segment
	 *  to neighboring segment and then returning back to starting segment again
	 */
	
	public static int countOfTimestep=11;
	/**
	*	This map stores initial values of "freqSuccess" for each of the segments present in DemoUCT1.roadNetwork.segmentProcessIdMap
	*   @key SegmentProcess-segment
	*   @Value Double-freqSuccess
	**/
	
	public static Map<SegmentProcess,ArrayList<Double>> segmentFreqMap;
	
	/**
	*   This map stores all the parameters learnt using training phase
	*  	@Key String- SegmentProcess in string form .eg- "65325369l 65352344l"
	*   @Value ArrayList<SegmentUCB> - Consists learnt parameters(Q value(double) and saCount(int)) of all neighbours of the key    
	*      
	**/
	public static Map<String,ArrayList<SegmentUCB>> segmentUCBValuesList=new HashMap<String,ArrayList<SegmentUCB>>();
	
	
	/**
	*   This map stores all the parameters learnt using training phase for Verma work
	*  	@Key String- SegmentProcess in string form .eg- "65325369l 65352344l"
	*   @Value ArrayList<SegmentUCB> - Consists learnt parameters(Q value(double) and saCount(int)) of all neighbours of the key    
	*      
	**/
	public static Map<String,SegmentUCB[]> VermaValuesList=new HashMap<String,SegmentUCB[]>();
	
	
	/**
	*   This map stores all the parameters learnt using testing phase
	*  	@Key String- SegmentProcess in string form .eg- "65325369l 65352344l"
	*   @Value ArrayList<SegmentUCB> - Consists learnt parameters(Q value(double) and saCount(int)) of all neighbours of the key    
	*      
	**/
		
	
	public static Map<String,ArrayList<SegmentUCB>> testSegmentUCBValuesList=new HashMap<String,ArrayList<SegmentUCB>>();
	
	
	/**
	 *	This map stores all the segments sCount value
	 * 	@Key SegmentProcess
	 * 	@Value sCount
	 * 
	 */
	
	public static Map<SegmentProcess,Integer> segmentSCountList=new HashMap<SegmentProcess, Integer>();



	//For removing verma experiment, remove last 2 arguments
	public static void main(String[] args,String phase1,String TRAIN_INPUT_SEGMENT_FILE,String TEST_INPUT_SEGMENT_FILE,PrintStream out1,ArrayList<String> predictionSegments,String pathOfLearntParameters,int distance,int noOfRecommendations/*,String VermaInput,boolean VermaTesting*/) throws Exception

	{
		String str;
		DemoUCT1.phase=phase1;
		DemoUCT1.out1=out1;
		//System.out.println("************* STARTING UCT "+phase+"*************************************");
		//System.out.println(noOfRecommendations);
		/*
		*//***
		 * dataType ={10,25,50,75,90}
		 * Percentage of data used for training UCT algorithm. 
		 * 
		 *//*
		
		int dataType[]=Centralized.dataType;
		
		*//**
		 * distance = {1000,2000,3000,5000} in metres
		 * Distance threshold after which we stop the recommendation from a particular segment in both UCT and baseline.
		 * 
		 *//*
		int distance[]=Centralized.distance;
		
		
		ArrayList<Long> testingTime=new ArrayList<Long>();
		FileInputStream f1=new FileInputStream("TestingData/"+dataCategory+dataType[0]+"/Top1Neighbours.csv");
		BufferedReader br=new BufferedReader(new InputStreamReader(f1));
		while((str=br.readLine())!=null)
		{
			String s[]=str.split(",");
			sortedTestData.add(s[0]);
			//count++;



		}
		
		
	
		 
		for(int y=0;y<dataType.length;y++)
		{

			for(int m=0;m<distance.length;m++)
			{
				out1=new PrintStream(new File("UCT/"+dataCategory+dataType[y]+"/"+distance[m]+"m"+"/C"+ucb_scaler+"/cost"+costPerMetre+"/"+Centralized.testDataType+"/"+Centralized.noOfTimesRecommendationSegment+"/Output.csv"));
				System.out.println(m+y);
		
				String TRAIN_INPUT_SEGMENT_FILE="/home/nandani/Documents/sem-3/taxiNYC/NYExperimentData/"+dataCategory+"/"+dataType[y]+"/Train"+dataType[y]+".csv";
				String TEST_INPUT_SEGMENT_FILE="/home/nandani/Documents/sem-3/taxiNYC/NYExperimentData/"+dataCategory+"/"+dataType[y]+"/Test"+dataType[y]+".csv";
				pathOfLearntParameters="/home/nandani/Documents/sem-3/taxiNYC/Bandit/LearntParameters/"+dataCategory+dataType[y]+"/"+distance[m]+"m"+"/C"+ucb_scaler+"/cost"+costPerMetre+"/LearntParameters.csv";
				
				
				
				//out1=new PrintStream(new File("_UCT/Normal"+trainingDataType+"/"+distanceThreshold+"m"+"/C"+ucb_scaler+"/cost"+x[y]+"/"+m+".csv"));
				//out1=new PrintStream(new File("_UCT/Normal"+trainingDataType+"/"+distanceThreshold+"m"+"/C"+x[y]+"/cost"+costPerMetre+"/"+m+".csv"));
				

				
				
			*/
				
				DemoUCT1 demoUCT1Obj=new DemoUCT1();
				// Entire logic for training

				if(phase=="Training")
				{
					
					
					//long startTime = System.currentTimeMillis();
					Random rand2 = new Random();
					recomendation.Main startMain = new Main();
					roadNetwork = startMain.main2(timeSlot,TRAIN_INPUT_SEGMENT_FILE);
					//new lines added
					Map<Long, ArrayList<SegmentProcess>> segmentIdMap= DemoUCT1.roadNetwork.segmentProcessIdMap;
					//System.out.println(segmentIdMap.size());
					segmentFreqMap=new HashMap<SegmentProcess,ArrayList<Double>>();
					

					for(Entry<Long, ArrayList<SegmentProcess>> entry:segmentIdMap.entrySet())
					{
						ArrayList<SegmentProcess> process=entry.getValue();
						Long startValue=entry.getKey();


						for(SegmentProcess segment1:process)
						{

							ArrayList<Double> frequencies=new ArrayList<Double>();
							for(int n=0;n<noOfTimeslot;n++){
							frequencies.add(segment1.freqSuccess[n]);
							}
							segmentFreqMap.put(segment1, frequencies);

						}
					}
					

					demoUCT1Obj.train(rand2,segmentIdMap,distance);
					demoUCT1Obj.writeLearntValuesIntoFile(segmentUCBValuesList,pathOfLearntParameters);

					//long stopTime = System.currentTimeMillis();
					//long elapsedTime = stopTime - startTime;
					//System.out.println("Time used for training :"+ elapsedTime+" ms");	
					
					

				}         //end of training phase


				
				
				
				//// Entire logic for testing
				else
					if(phase=="Testing")
					{
						
						demoUCT1Obj.readLearntValues(pathOfLearntParameters);
						recomendation.Main startMain = new Main();
						roadNetwork = startMain.main2(timeSlot,TEST_INPUT_SEGMENT_FILE);
						//System.out.println(roadNetwork.segmentProcessIdMap.size());
						Map<Long, ArrayList<SegmentProcess>> segmentIdMap= DemoUCT1.roadNetwork.segmentProcessIdMap;
						ArrayList<SegmentProcess> testData=new ArrayList<SegmentProcess>();
						
						for(Entry<Long, ArrayList<SegmentProcess>> entry:segmentIdMap.entrySet())
						{
							int j=0;
							Long start=entry.getKey();
							ArrayList<SegmentProcess> process=entry.getValue();			
							for(SegmentProcess segment1:process)
							{
								
								
								
								Long end=process.get(j).endLocation.id;
								j++;
								segmentSCountList.put(segment1,trajectories+1);
								if(start.toString().equals(end.toString())==false)
									testData.add(segment1);
								

							}
						}



						
						Testing testObj =new Testing();


						//Reading test data from file CompleteTestDataSortedTimeslot2.csv

						//int count=0;
						
						
						
						
							/*for(int l=0;l<1;l++)
							{

								for(int i=0;i<sortedTestData.size();i++)
								{
								//System.out.println("Recommendation started for segment :" +sortedTestData.get(i));
								//System.out.println("----------------------------------------------------------------"+"\n");
									String segmentId=sortedTestData.get(i);
									String[] segmentIdPartition=segmentId.split(" ");
									Long startId=Long.parseLong(segmentIdPartition[0]);
									Long endId=Long.parseLong(segmentIdPartition[1]);	

									testObj.test(startId,endId,distance[m]);
									System.out.println();

								}

							}*/

					//}


					
					long startTime=System.nanoTime();
					for(int noOfTimesRecommendationSegment=1;noOfTimesRecommendationSegment<=noOfRecommendations;noOfTimesRecommendationSegment=noOfTimesRecommendationSegment+1)

						{
						
							for(int i=0;i<predictionSegments.size();i++)
							{
							/*	System.out.println("Recommendation started for segment :" +predictionSegments.get(i));
								System.out.println("----------------------------------------------------------------"+"\n");*/
								//System.out.println("PreTesting"+(testData.get(i).startLocation.id)+","+(testData.get(i).endLocation.id));
								
								String segmentId=predictionSegments.get(i);
								String[] segmentIdPartition=segmentId.split(" ");
								long startId=Long.parseLong(segmentIdPartition[0]);
								long endId=Long.parseLong(segmentIdPartition[1]);
								
								
								
								if((startId)!=(endId))
								{
									
									testObj.test(startId,endId,distance/*,VermaTesting*/);
								}
								//System.out.println();
							}
						}
					long stopTime = System.nanoTime();
					long elapsedTime = stopTime - startTime;
					System.out.println(elapsedTime);	
						
						

					}		//end of if

                //Uncomment this code if you want to run Verma testing
					/*else {
						if (phase.equals("Verma")) {


							Random rand2 = new Random();
							recomendation.Main startMain = new Main();
							roadNetwork = startMain.main2(timeSlot, TRAIN_INPUT_SEGMENT_FILE);
							//new lines added
							Map<Long, ArrayList<SegmentProcess>> segmentIdMap = DemoUCT1.roadNetwork.segmentProcessIdMap;
							//System.out.println(segmentIdMap.size());
							segmentFreqMap = new HashMap<SegmentProcess, ArrayList<Double>>();

							for (Entry<Long, ArrayList<SegmentProcess>> entry : segmentIdMap.entrySet()) {
								ArrayList<SegmentProcess> process = entry.getValue();
								Long startValue = entry.getKey();


								for (SegmentProcess segment1 : process) {

									ArrayList<Double> frequencies = new ArrayList<Double>();
									for (int n = 0; n < noOfTimeslot; n++) {
										frequencies.add(segment1.freqSuccess[n]);
									}
									segmentFreqMap.put(segment1, frequencies);

								}
							}


							BufferedReader br = new BufferedReader(new FileReader(VermaInput));
							String string = "";
							List<List<Long>> trajectoryID = new ArrayList<List<Long>>();
							while ((string = br.readLine()) != null) {
								String[] array = string.split(",");
								List<Long> group = new ArrayList<Long>();
								for (int i = 0; i < array.length; i++)
									group.add(Long.parseLong(array[i]));
								trajectoryID.add(group);

							}


							demoUCT1Obj.trainVerma(rand2, trajectoryID, distance, segmentIdMap);
							demoUCT1Obj.writeVermaLearntValuesIntoFile(VermaValuesList, pathOfLearntParameters);

							//long stopTime = System.currentTimeMillis();
							//long elapsedTime = stopTime - startTime;
							//System.out.println("Time used for training :"+ elapsedTime+" ms");	

						}
						
							
					}*/

	}
	
	
	
	
	/**
	 * @param rand2- Random number
	 * @param segmentIdMap - Roadnetwork map obtained from DemoUCT1.roadNetwork.segmentProcessIdMap
	 * @throws FileNotFoundException
	 * 
	 * This is the main method for learning the parameters.All planning is done in this method.Generates map segmentUCBValuesList
	 * which stores all the learnt parameters 
	 */
		
	public static void trainVerma(Random rand2,List<List<Long>> nodesMap,int distance,Map<Long,ArrayList<SegmentProcess>> segmentIdMap) throws FileNotFoundException
	{
		UCT1 planner=null;
		for(int k=0;k<nodesMap.size();k++)
		{
			List<Long> process = nodesMap.get(k);
			Long start = process.get(0);
			Long end = process.get(1);
			SegmentProcess startSegment = roadNetwork.currRoadSegment(start, end);
			MDPState1 mdpState1 = new MDPState1(startSegment);
			SegmentSimulator1 simPlan1 = new SegmentSimulator1(mdpState1);
			if(simPlan1.numActions!=0 && start.toString().equals(end.toString())==false)
			{	
				//System.out.println("neighbour size "+simPlan1.numActions);
				planner = new UCT1(simPlan1, trajectories,distance,rand2,roadNetwork);
				MDPState1 mdpcurrState;
				//long startTime = System.currentTimeMillis();
				mdpcurrState = simPlan1.getState();
				int a1 = planner.planAndActV(mdpcurrState,simPlan1.numActions,process);
		
		
			}	

			}
		
		}
		

		

	
	
	/**
	 * @param rand2- Random number
	 * @param segmentIdMap - Roadnetwork map obtained from DemoUCT1.roadNetwork.segmentProcessIdMap
	 * @throws FileNotFoundException
	 * 
	 * This is the main method for learning the parameters.All planning is done in this method.Generates map segmentUCBValuesList
	 * which stores all the learnt parameters 
	 */
		
	public void train(Random rand2,Map<Long, ArrayList<SegmentProcess>> segmentIdMap,int distance) throws FileNotFoundException
	{
		 
				for(Entry<Long, ArrayList<SegmentProcess>> entry:segmentIdMap.entrySet())
				{
					ArrayList<SegmentProcess> process=entry.getValue();
					Long start=entry.getKey();
					int j=0;
					for(SegmentProcess segment1:process)
					{
							
							Long end=process.get(j).endLocation.id;
							j++;
							SegmentProcess startSegment=roadNetwork.currRoadSegment(start, end);
							MDPState1 mdpState1 = new MDPState1(startSegment);
							SegmentSimulator1 simPlan1 = new SegmentSimulator1(mdpState1);
							if(simPlan1.numActions!=0 && start.toString().equals(end.toString())==false)
							{	
								//System.out.println("neighbour size "+simPlan1.numActions);
								UCT1 planner = new UCT1(simPlan1, trajectories,distance,rand2,roadNetwork);
								MDPState1 mdpcurrState;
								//long startTime = System.currentTimeMillis();
								mdpcurrState = simPlan1.getState();
								int a1 = planner.planAndAct(mdpcurrState,simPlan1.numActions);
								for(Entry<Long, ArrayList<SegmentProcess>> entry2:segmentIdMap.entrySet())
								{
									ArrayList<SegmentProcess> process2=entry2.getValue();
									
									for(SegmentProcess segment2:process2)
									{
										segment2.visited=false;
									}
								}
								
								ArrayList<SegmentUCB> segmentUCBList=new ArrayList<SegmentUCB>();
								//SegmentsCount objectsCount=new SegmentsCount(start,planner.getSCount());
								ArrayList<SegmentProcess> neighbourList = roadNetwork.neighborSegment(segment1);
								int k=0;
								for(SegmentProcess segment:neighbourList)
								{
									SegmentUCB objectUCB=new SegmentUCB(planner.getQ(k),planner.getSaCount(k));
									segmentUCBList.add(objectUCB);
									k++;
								}
								segmentUCBValuesList.put(segment1.toString(), segmentUCBList);
								//System.out.println(segmentUCBValuesList.get(start).get(0).UCB);
								//System.out.println(segmentUCBValuesList.get(start).get(1).UCB);
							/*	long stopTime = System.currentTimeMillis();
								long elapsedTime = stopTime - startTime;
								System.out.println("Time in millisec for 1 segment :"+ elapsedTime+" ms");
						*/
			
							}
											//else
												//System.out.println("segment passed is the leaf segment,no further segments starting from current segment");							
					}
				}
							
	}
	
	
	
	/**
	 * 
	 * @param list - segmentUCBValuesList
	 * @throws IOException
	 * Writes all the learnt parameters into file "pathOfLearntParameters"
	 */
	
	
	public void writeLearntValuesIntoFile(Map<String,ArrayList<SegmentUCB>> list,String pathOfLearntParameters) throws IOException
	{
		DecimalFormat f = new DecimalFormat("##.00");
		PrintStream out1=new PrintStream(new File(pathOfLearntParameters));
		for(Entry<String,ArrayList<SegmentUCB>> entry:list.entrySet())
		{
			String keyValue=entry.getKey();
			ArrayList<SegmentUCB> arrayList=entry.getValue();
			out1.print(keyValue+";");
			for(int i=0;i<arrayList.size();i++)
			{
				out1.print(f.format(arrayList.get(i).Q)+","+arrayList.get(i).saCount+";");
			}
		out1.println();
		}
	}
	
	
	
	/**
	 * 
	 * @param list - segmentUCBValuesList
	 * @throws IOException
	 * Writes all the learnt parameters into file "pathOfLearntParameters"
	 */
	
	
	public void writeVermaLearntValuesIntoFile(Map<String,SegmentUCB[]> list,String pathOfLearntParameters) throws IOException
	{
		DecimalFormat f = new DecimalFormat("##.00");
		PrintStream out1=new PrintStream(new File(pathOfLearntParameters));
		for(Entry<String,SegmentUCB[]> entry:list.entrySet())
		{
			String keyValue=entry.getKey();
			SegmentUCB[] arrayList=entry.getValue();
			out1.print(keyValue+";");
			for(int i=0;i<arrayList.length;i++)
			{
				if(arrayList[i]!=null)
				out1.print(f.format(arrayList[i].Q)+","+arrayList[i].saCount+";");
				else
					out1.print(0.0+","+0+";");
			}
		out1.println();
		}
	}
		
		
	
	/**
	 * @throws Exception
	 * 
	 * This method reads all the learnt values written into file "pathOfLearntParameters" to a map named testSegmentUCBValuesList
	 * 
	 */
	
	public void readLearntValues(String pathOfLearntParameters) throws Exception
	{
		FileInputStream fr1=new FileInputStream(pathOfLearntParameters);
		BufferedReader br1 = new BufferedReader(new InputStreamReader(fr1));
		String strLine1="";
		while((strLine1=br1.readLine())!=null)
		{
			String s1[]=strLine1.split(";");
			//String s2[]=s1[0].split(",");
			//SegmentsCount segmentsCountobj=new SegmentsCount(Long.parseLong(s2[0]),Integer.parseInt(s2[1]));
			ArrayList<SegmentUCB> segmentUCBList=new ArrayList<SegmentUCB>();
			for(int i=1;i<s1.length;i++)
			{
				String s3[]=s1[i].split(",");
				SegmentUCB segmentUCBObj=new SegmentUCB(Double.parseDouble(s3[0]), Integer.parseInt(s3[1]));
				segmentUCBList.add(segmentUCBObj);
			}
			testSegmentUCBValuesList.put((s1[0]), segmentUCBList);
				
		}
		
	}
				

		
	}
