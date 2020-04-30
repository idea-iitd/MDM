package TaxiRecommendation;

import recomendation.Location;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import driver.Centralized;

public class Main {
	/**
	 * timeSlot is the number of slot in a day 
	 */
	public static int noOfTimeSlot = Centralized.noOfTimeslots;
	
	/**
	 * timeSlot is the slot in which recommendation needs to be given
	 */
	public static int timeSlot = Centralized.timeslot;
	

	static long  start = recomendation.Main.start;
	static long end = recomendation.Main.end;
	
	//public static String trainingDataType="10";
	
	
	
	
	public static PrintStream out1;
	
	

	/**
	 * dataCategory can be either {"Normal","Top","last"}
	 * Normal- x% of total drivers data used for training ,rest all the drivers data used for testing 
	 * Top - top x% drivers(based on how many trips a driver has) data used for training,rest all drivers data used for testing
	 * last - last x % drivers data used for training,rest all drivers data used for testing
	 */
	public static String dataCategory=Centralized.dataCategory;
	

	/**
	 * testDataType={"AllSegments","Top5Neighbours","Bottom5Neighbours"} 
	 * 
	 */

	//public static String testDataType=Centralized.testDataType;
	
	
	
	/**
	 * How many times recommendation should be made from a particular segment
	 * 
	 */	
	//public static int noOfTimesRecommendationSegment=1;
	
	
	
	
	
			
	public static int M = 5;
	public static int K = 10;
	public static Map<Long, ArrayList<SegmentProcess>> segmentProcessIdMap;
	//public static String TRAIN_INPUT_SEGMENT_FILE = "/home/nandani/Documents/sem-3/taxi project2/Sa_dataset/ExperimentData/"+trainingDataType+"/CompleteSegmentDataTrain.csv";
	//public static String TEST_INPUT_SEGMENT_FILE = "/home/nandani/Documents/sem-3/taxi project2/Sa_dataset/ExperimentData/"+trainingDataType+"/CompleteSegmentDataTest.csv";
	
	public static Map<Long, ArrayList<SegmentProcess>> segmentProcessMapForTrain=new HashMap<Long, ArrayList<SegmentProcess>>();
	
	
	public static boolean noRouteFound=false;
	
	
	
	/**
	 * This hashmap stores for each segment used in testing its topk route  of length 5 recommended by the algorithm
	 * @key starting segment
	 * @value Arraylist of topK routes recommended by the algorithm 
	 */
	
	public static Map<SegmentProcess,RouteProfitMap[]> segmentTopKRoutes=new HashMap<SegmentProcess,RouteProfitMap[]>(); 
	
	/**
	 * This hashmap stores every segment present in testing dataset and its frequency
	 * @key SegmentProcess-Segment
	 * @Value double[] -freqSucess[timeslot]
	 */
	
	public static Map<String,double[]> segmentFrequency =new HashMap<String,double[]>();
	
	//Sorted data based on frequency used for testing
	public static ArrayList<String> sortedTestData=new ArrayList<String>();
	
	
	public static void populateSegmentProcessIdMap(String filename)
	{
		
		segmentProcessIdMap= new HashMap<Long, ArrayList<SegmentProcess>>();
		String[] tr=Centralized.timeslotRange;
		File file = new File(filename);
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String string;
			while((string = in.readLine()) != null)
			{
				String[] str = string.split(";");
				String[] subString = str[0].split(",");
				Location startLocation = new Location(Long.parseLong(subString[0]), Double.parseDouble(subString[1]), Double.parseDouble(subString[2]));
				subString = str[1].split(",");
				Location endLocation = new Location(Long.parseLong(subString[0]), Double.parseDouble(subString[1]), Double.parseDouble(subString[2]));
				double segmentLength = Double.parseDouble(str[2]);
				subString = str[3].split(",");
				String[] subString2 = str[4].split(",");
				String[] subString3 = str[5].split(",");
				double[] distanceTravelled = new double[noOfTimeSlot];
				double[] freqSuccess = new double[noOfTimeSlot];
				double[] freqFailure = new double[noOfTimeSlot];
				for (int i = 0; i < subString.length; i++) {
					for(int s=0;s<tr.length;s++)
					{
						
						if(i>=Integer.parseInt(tr[s].split("-")[0]) && i<=Integer.parseInt(tr[s].split("-")[1]))
						{
							
							distanceTravelled[s] += Double.parseDouble(subString[i]);
							freqSuccess[s] += Double.parseDouble(subString2[i]);
							freqFailure[s] += Double.parseDouble(subString3[i]);
							
						}
					}
					
					
					/*if(i>=1 && i<=5)
					{
						distanceTravelled[0] += Double.parseDouble(subString[i]);
						freqSuccess[0] += Double.parseDouble(subString2[i]);
						freqFailure[0] += Double.parseDouble(subString3[i]);
					}
					else {
						if(i>=6 && i<=16)
						{
							distanceTravelled[1] += Double.parseDouble(subString[i]);
							freqSuccess[1] += Double.parseDouble(subString2[i]);
							freqFailure[1] += Double.parseDouble(subString3[i]);
						}
						else {
							distanceTravelled[2] += Double.parseDouble(subString[i]);
							freqSuccess[2] += Double.parseDouble(subString2[i]);
							freqFailure[2] += Double.parseDouble(subString3[i]);
						}
					}
					*/



					
				}
				for(int n=0;n<tr.length;n++)
				{
				distanceTravelled[n] /= Integer.parseInt(tr[n].split("-")[1]) - Integer.parseInt(tr[n].split("-")[0])+1;
				
				/*distanceTravelled[0] /= 5;  // to take average distance
				distanceTravelled[1] /= 11;
				distanceTravelled[2] /= 8;*/
				}
				long timeToCrossSegment = Long.parseLong(str[6]);

				if(startLocation.id.toString().equals(endLocation.id)==false)
				{
				
				
				SegmentProcess segmentProcess = new SegmentProcess(startLocation, endLocation, segmentLength, distanceTravelled, freqSuccess, freqFailure, timeToCrossSegment);
				if(segmentProcessIdMap.containsKey(startLocation.id) == true)
					{
						ArrayList<SegmentProcess> segmentProcessesList = segmentProcessIdMap.get(startLocation.id);
						if(segmentProcessesList.contains(segmentProcess)==false)
							segmentProcessesList.add(segmentProcess);
						
						
					}
				else
					{
						ArrayList<SegmentProcess> segmentProcessesList = new ArrayList<SegmentProcess>();
						segmentProcessesList.add(segmentProcess);
						segmentProcessIdMap.put(startLocation.id, segmentProcessesList);
					}
				
				double tempCost = segmentProcess.calculatePotentialCost();
				
				}
				
			}
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 
	 * @param rootToLeaves  all possible routes from start to end 
	 * @return routeProfitMap object consist of route and Net profit of corresponding routes 
	 */
	public static RouteProfitMap[] findAllNetProfit(ArrayList<ArrayList<SegmentProcess>> rootToLeaves)
	{
		int noOfRoutes = rootToLeaves.size();
		double[] allNetProfit = new double[noOfRoutes];
		RouteProfitMap[] routeProfitMaps = new RouteProfitMap[noOfRoutes];
		double netProfitOnROute = 0.0;

		for(int i = 0 ; i < noOfRoutes ; ++i)
		{
			double netProfitperSegment = 0.0;
			ArrayList<SegmentProcess> route = rootToLeaves.get(i);
			int routeSize = route.size();
			double failureProduct = 1.0;
			//modification done profit is calculated from the second segment not the starting one
			double potentialCost = route.get(1).potentialCost;
			double potentialEarning = route.get(1).potentialEarning[timeSlot];
			double pickUpProbabilty = route.get(1).findPickupProbability(timeSlot);
			netProfitperSegment = potentialEarning*pickUpProbabilty - potentialCost*(1 - pickUpProbabilty);
			for(int j = 2 ; j < routeSize ; ++j)
			{
				potentialCost = route.get(j).potentialCost;
				potentialEarning = route.get(j).potentialEarning[timeSlot];
				pickUpProbabilty = route.get(j).findPickupProbability(timeSlot);
				double currentSegmentNetProfit = potentialEarning*pickUpProbabilty - potentialCost*(1 - pickUpProbabilty);
				failureProduct *= (1 - route.get(j - 1).findPickupProbability(timeSlot));
				netProfitperSegment = currentSegmentNetProfit + ( failureProduct * netProfitperSegment);
				
				
			}
			netProfitOnROute = netProfitperSegment;
			routeProfitMaps[i] = new RouteProfitMap(route, netProfitOnROute);
			
		}

		return routeProfitMaps;
	}

	public static void topKRoute(RouteProfitMap[] routeProfitMap, int k) 
	{
		
		
//		PriorityQueue<RouteProfitMap> heap = new PriorityQueue<RouteProfitMap>();
//		for (int i = 0; i < routeProfitMap.length; i++) {
//			heap.add(routeProfitMap[i]);
//		}
//		for (int i = 0; i < routeProfitMap.length; i++) 
//		   System.out.println(heap.poll().netProfit);
		
//		ArrayList<RouteProfitMap> arrayList = new ArrayList<RouteProfitMap>();
//		for (int i = 0; i < routeProfitMap.length; i++) {
//			arrayList.add(routeProfitMap[i]);
//		}

//		Comparator<RouteProfitMap> descendingNetProfit = new Comparator<RouteProfitMap>()
//		{
//			@Override
//			public int compare(RouteProfitMap o1, RouteProfitMap o2)
//			{
//				// TODO Auto-generated method stub
//				if(o1.netProfit < o2.netProfit)
//					return 1;
//				else 
//					return 0;
//			}
//
//			
//		};
//		
//
//		Collections.sort(arrayList,descendingNetProfit);
		
		Arrays.sort(routeProfitMap);
		
//		RouteProfitMap[] n = new RouteProfitMap[rootToLeaves.length - 1];
//		    System.arraycopy(rootToLeaves, 0, n, 0, k );
//		    System.arraycopy(n, 0, rootToLeaves, 0, k);
//		    return routeProfitMap;
	}
	
	public static Map<SegmentProcess, ArrayList<Integer>> findOverlap(ArrayList<ArrayList<SegmentProcess>> rootToLeaves)
	{
		Map<SegmentProcess, ArrayList<Integer>> mapSegment = new HashMap<SegmentProcess, ArrayList<Integer>>();
		for (int i = 0; i < rootToLeaves.size(); i++) {
			ArrayList<SegmentProcess> route = rootToLeaves.get(i);
			for (int j = 0; j < route.size(); j++) {
				SegmentProcess segment = route.get(j);
				if(mapSegment.containsKey(segment))
				{
					ArrayList<Integer> arrayList = mapSegment.get(segment);
					arrayList.add(i);
				}
				else
				{
					ArrayList<Integer> arrayList = new ArrayList<Integer>();
					arrayList.add(i);
					mapSegment.put(segment, arrayList);
				}
			}
		}
		return mapSegment;
	}
	
	
//	public static ArrayList<ArrayList<SegmentProcess>> main2() {
	public static void main(String[] strings,String TRAIN_INPUT_SEGMENT_FILE,String TEST_INPUT_SEGMENT_FILE,PrintStream out1,ArrayList<String> predictionSegments,int distance,int noOfRecommendations) throws Exception{
		System.out.println("************* STARTING BASELINE FOR*************************************");
		System.out.println("Recommendations"+noOfRecommendations);
		String str;

		
		
		//Reading test data from file CompleteTestDataSortedTimeslot2.csv
		/*for(int i=0;i<5;i++)
		{*/
		//int count=0;
		double total_Frequency=0;
		
		/*FileInputStream f1=new FileInputStream("TestingData/"+dataCategory+dataType[0]+"/Bottom5Neighbours.csv");
		BufferedReader br=new BufferedReader(new InputStreamReader(f1));
		while((str=br.readLine())!=null && count<1000)
		{
				String s[]=str.split(",");
				sortedTestData.add(s[0]);
				total_Frequency=total_Frequency+Float.parseFloat(s[3]);
				//count++;



		}*/
		
		
		/***
		 * dataType ={10,25,50,75,90}
		 * Percentage of data used for training UCT algorithm. 
		 * 
		 */
		
	//	int dataType[]=Centralized.dataType;
		
		/**
		 * distance = {1000,2000,3000,5000} in metres
		 * Distance threshold after which we stop the recommendation from a particular segment in both UCT and baseline.
		 * 
		 */
	//	int distance[]=Centralized.distance;
		
		
		
	



		//String x[]={"0.25","0.5","1.0","1.5","2.0","5.0","10.0","15.0","20.0"};
	

/*		for(int y=0;y<dataType.length;y++)
		{


			for(int m=0;m<distance.length;m++)
			{
				
				//System.out.println(dataCategory+dataType[y]+","+distance[m]+);
				
				System.out.println(m+y);
				
				

				String TRAIN_INPUT_SEGMENT_FILE="/home/nandani/Documents/sem-3/taxiNYC/NYExperimentData/"+dataCategory+"/"+dataType[y]+"/Train"+dataType[y]+".csv";
				String TEST_INPUT_SEGMENT_FILE="/home/nandani/Documents/sem-3/taxiNYC/NYExperimentData/"+dataCategory+"/"+dataType[y]+"/Test"+dataType[y]+".csv";

				Main obj=new Main();
				PrintStream out5=new PrintStream(new File("TestingData/"+dataCategory+dataType[y]+"/CompleteSegments.csv"));
				out1=new PrintStream(new File("Baseline/"+dataCategory+dataType[y]+"/"+distance[m]+"m/"+testDataType+"/"+Centralized.noOfTimesRecommendationSegment+"/Output.csv")); */
				

				populateSegmentProcessIdMap(TRAIN_INPUT_SEGMENT_FILE);
				//populated map from training data
				segmentProcessMapForTrain=segmentProcessIdMap;

				populateSegmentProcessIdMap(TEST_INPUT_SEGMENT_FILE);

				Map<Long, ArrayList<SegmentProcess>> segmentProcessMapForTest=segmentProcessIdMap;

				//ArrayList<SegmentProcess> segments_Test_Data=new ArrayList<SegmentProcess>();



				//rTree.calculateGridDirectionVectors(segmentProcessMapForTrain);

				populateDirectionVectorsOfSegmentIDMap();



				for(Entry<Long, ArrayList<SegmentProcess>> entry:segmentProcessMapForTest.entrySet())
				{
					ArrayList<SegmentProcess> process=entry.getValue();

					for(SegmentProcess segmentProcess1:process)
					{
						double[] frequencyOfSegment =new double[noOfTimeSlot];
						frequencyOfSegment=segmentProcess1.freqSuccess;
						segmentFrequency.put(segmentProcess1.toString(),frequencyOfSegment);
	
						/**
						 * uncomment later
						 */
						//segments_Test_Data.add(segmentProcess1);


					}

				}
				
//				for(Entry<Long, ArrayList<SegmentProcess>> entry:segmentProcessMapForTrain.entrySet())
//				{
//					ArrayList<SegmentProcess> process=entry.getValue();
//
//					for(SegmentProcess segmentProcess1:process)
//					{
//						double[] frequencyOfSegment =new double[noOfTimeSlot];
//						frequencyOfSegment=segmentProcess1.freqSuccess;
//						//segmentFrequency.put(segmentProcess1.toString(),frequencyOfSegment);
//						frequency_count1=frequency_count1+frequencyOfSegment[0];
//						frequency_count2=frequency_count2+frequencyOfSegment[1];
//						frequency_count3=frequency_count3+frequencyOfSegment[2];
//						/**
//						 * uncomment later
//						 */
//						//segments_Test_Data.add(segmentProcess1);
//
//						//out5.println(segmentProcess1.toString()+","+/*segmentProcess1.startLocation.latitude+","+segmentProcess1.startLocation.longitude+","+segmentProcess1.endLocation.latitude+","+segmentProcess1.endLocation.longitude+","+*/segmentProcess1.freqSuccess[0]+","+segmentProcess1.freqSuccess[1]+","+segmentProcess1.freqSuccess[2]);
//
//					}
//
//				}
//
//
//				System.out.println("Total frequency"+","+frequency_count1+","+frequency_count2+","+frequency_count3);


				//System.out.println("Total frequency"+frequency_count);
				//System.out.println("Number of distinct start location segmentProcess "+segmentFrequency.size());

				
		
				//PrintStream out1=new PrintStream(new File("Baseline/"+dataCategory+dataType[y]+"/"+distance[m]+"m"+"/Bottom5Neighbours/"+a+".csv"));
				
			//System.out.println("Recommendation started for number "+noOfRecommendations);
		    long startTime = System.nanoTime();
			for(int noOfTimesRecommendationSegment=1;noOfTimesRecommendationSegment<=noOfRecommendations;noOfTimesRecommendationSegment=noOfTimesRecommendationSegment+1)

			{
				 
				for(int t=0;t<predictionSegments.size();t++)
				{
					
					String segmentId=predictionSegments.get(t);
					String[] segmentIdPartition=segmentId.split(" ");
					Long startId=Long.parseLong(segmentIdPartition[0]);
					Long endId=Long.parseLong(segmentIdPartition[1]);

					if(startId.toString().equals(endId.toString())==false)
					{


						ArrayList<SegmentProcess> segmentProcessList = segmentProcessMapForTrain.get(startId);
						//System.out.println(segmentProcessList);
						ArrayList<SegmentProcess> segmentNeighbourList=new ArrayList<SegmentProcess>();
						ArrayList<SegmentProcess> list = new ArrayList<SegmentProcess>();
						SegmentProcess segmentProcess = null;
						for (SegmentProcess segmentProcessTemp1 : segmentProcessList) {
							//System.out.println(segmentProcessTemp1.endLocation.id+" "+ end);
							if(segmentProcessTemp1.endLocation.id.toString().equals(endId.toString()))
								//if(segmentProcessTemp1.endLocation.id==end)
							{
								//System.out.println("nandu");
								segmentProcess = segmentProcessTemp1;
								list=segmentProcessMapForTrain.get(segmentProcess.endLocation.id);
								if(list!=null)
								{
									for(SegmentProcess segmentTemp:list)
									{
										if(!(segmentTemp.endLocation.id.toString().equals(segmentProcess.startLocation.id.toString())))
											segmentNeighbourList.add(segmentTemp);
									}
									break;
								}

							}

						} //end of for loop     

						/*System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+"\n");
						System.out.println("Recommendation started for segment :" +segmentProcess);
						System.out.println("----------------------------------------------------------------"+"\n");	*/

						//out1.print(segmentProcess.toString()+",");
						if(segmentProcess!=null && segmentNeighbourList.isEmpty()==false)
						{

							
							boolean customerFound=false;
							boolean isLengthOfRouteGreaterThan1KM=false;
							ArrayList<SegmentProcess> temporaryRoute=new ArrayList<SegmentProcess>();
							SegmentProcess segment=segmentProcess;
							ArrayList<SegmentProcess> currentSegmentNeighbourList=segmentNeighbourList;
							noRouteFound=false;
							while(customerFound==false && isLengthOfRouteGreaterThan1KM==false && currentSegmentNeighbourList!=null && noRouteFound==false)
							{

								if(segmentTopKRoutes.containsKey(segment))
								{
									//System.out.println("found in the map");
									RouteRecommended routeRecommendedObject = getCustomerFoundOrNot(segment,segment.noOfRoutesRecommended++);


									if(routeRecommendedObject!=null)
									{


										for(int i=0;i<routeRecommendedObject.routeRecommended.size();i++)
										{
											temporaryRoute.add(routeRecommendedObject.routeRecommended.get(i));
										}

										if(routeRecommendedObject.customerFound==0)         //customer is not found


										{
											if(routeRecommendedObject.currentRoute.getLengthOfRoute(temporaryRoute)>=distance)
											{	
												//System.out.println("No recommendation,length exceeds "+distanceThreshold);
												//out1.println("No recommendation,length exceeds "+distanceThreshold);
												out1.println(distance+1000);	
												isLengthOfRouteGreaterThan1KM=true;
											}

											else	//if length of route is less than 1 km
											{

												segment=temporaryRoute.get(temporaryRoute.size()-1);
												//System.out.println("segment :"+segment);
												currentSegmentNeighbourList=segmentProcessMapForTrain.get(segment.endLocation.id);



											}
										}
										else   //if customer is found
										{
											customerFound=true;
											/*System.out.println("customer found");
											System.out.print(segmentProcess+"	:	");
											System.out.print(routeRecommendedObject.currentRoute.getLengthOfRoute(temporaryRoute)+" ");*/
											out1.println(routeRecommendedObject.currentRoute.getLengthOfRoute(temporaryRoute));
											//System.out.println(temporaryRoute);



										}



									}  // end of routeRecommendedObject!=null


								}   // end of if loop ..wh

								else
								{
									//System.out.println("Not found in the map");
									getRoute(segment);

								}

							} //end of while loop

							if(currentSegmentNeighbourList==null || noRouteFound==true)
							{
								//System.out.println("No customer as well as no neighbours for recommended segment of size 1 for segment  "+ segment);
								//out1.println("No recommendation1");
								out1.println(distance+1000);	
							}	






						}//end of if


						else
						{
							//System.out.println("segment passed is the leaf segment,no further segments starting from current segment  "+segmentProcess);
							//out1.println("No recommendation,no further actions");
							out1.println(distance+1000);	
						}			
						
						//System.out.println("Time in millisec for 1 segment :"+ elapsedTime+" ms");			

						
					} //end of if that checks if segment start id is not equal to end id
					}//end of 2nd for loop


					}	//end of loop where noOfTimesRecommendationSegment is variable





		//		}   //end of for loop with m as index



			//}  //end of for loop with y as index
			long stopTime = System.nanoTime();
			long elapsedTime = stopTime - startTime;
			System.out.println(elapsedTime+" ns");	


			


		
		}
	
	public static void populateDirectionVectorsOfSegmentIDMap() throws FileNotFoundException
	{
		
		FileInputStream f1=new FileInputStream(Centralized.INPUT_FOLDER_PATH+"Bandit/DirectionVector/NewTrain.csv");
		BufferedReader br=new BufferedReader(new InputStreamReader(f1));
		String s1=" ";
		
		try {
			while((s1=br.readLine())!=null)
			{
				String[] value=s1.split(":");
				String[] segmentId=value[0].split(" ");
				String startId=segmentId[0];
				String endId=segmentId[1];
				String[] direction=value[1].split(",");
				ArrayList<SegmentProcess> process=segmentProcessMapForTrain.get(Long.parseLong(startId));
				
				for(SegmentProcess segmentProcess1:process)
					{
						String segmentEndLocationId=segmentProcess1.endLocation.id.toString();
						if(segmentEndLocationId.equals(endId) && segmentProcess1.directionVector.size()==0)
						{
						
							for(int i=0;i<direction.length;i++)
							{
								segmentProcess1.directionVector.add(Double.parseDouble(direction[i]));
							}
							//System.out.println(segmentProcess1+"	"+segmentProcess1.directionVector);
							break;
						}
						
						
					} //end of for loop
				
				
				
				
				
				
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static RouteRecommended getCustomerFoundOrNot(SegmentProcess segmentProcess,int in)
	{
		/*for(Entry<SegmentProcess,double[]> entry:segmentFrequency.entrySet())
		{
			System.out.println(entry.getKey()+"	"+entry.getValue()[2]);
			
			
		}
		*/
		
		
		int index=in;
		RouteProfitMap[] r=segmentTopKRoutes.get(segmentProcess);
		ArrayList<SegmentProcess> routeRecommended=new ArrayList<SegmentProcess>();
		int flag=0;
		int i=1;
		RouteRecommended route1=null;
		//RouteProfitMap currentRoute=new RouteProfitMap(null, 0.0);
		if(index<r.length)
		{
		RouteProfitMap currentRoute=r[index];
		
		index++;
		
		while(i<currentRoute.route.size() && flag==0)
		{
			
			double[] frequency = Main.segmentFrequency.get(currentRoute.route.get(i).toString());
			//System.out.println(currentRoute.route.get(i).toString());
			if((frequency[timeSlot]) > 0)
			{
				//
				//System.out.println("frequency "+frequency[timeSlot]);
				frequency[timeSlot]--;
				segmentFrequency.remove(currentRoute.route.get(i).toString());
				segmentFrequency.put(currentRoute.route.get(i).toString(),frequency);
				flag=1;
				routeRecommended.add(currentRoute.route.get(i));
				
			
			
			}
			else if((frequency[timeSlot]) <= 0)
			{
				
				routeRecommended.add(currentRoute.route.get(i));
			}
			
			i++;
		} //end of while loop
	
		route1=new RouteRecommended(flag,routeRecommended,currentRoute);
		
		return route1;
	
		}  //end of outer if
		
		else
		{
			
			index=0;
			segmentProcess.noOfRoutesRecommended=0;
			RouteRecommended routeReturned=getCustomerFoundOrNot(segmentProcess,index);
			return routeReturned;
		}
		
		
		
		
		
		
	}
	
	
	
	
	public static void getRoute(SegmentProcess segmentProcess) throws FileNotFoundException
	{
		
		int noOfRoutes=0;
		
		SegmentProcessTree segmentProcessTree = new SegmentProcessTree();
		segmentProcessTree = segmentProcessTree.recursionTree(segmentProcess,M, segmentProcessMapForTrain);
		ArrayList<ArrayList<SegmentProcess>> rootToLeaves = new ArrayList<ArrayList<SegmentProcess>>();
		ArrayList<SegmentProcess> tempList = new ArrayList<SegmentProcess>();
		tempList.add(segmentProcessTree.segmentProcess);
		
//		double minCost = findMinCost(rootToLeaves);
//		double maxCost = findMaxCost(rootToLeaves);
		
		SegmentProcessTree.topK(rootToLeaves,segmentProcessTree, tempList,1);
		
		// To check Repetition of segment we iterate over root to leaves
		
		ArrayList<ArrayList<SegmentProcess>> tempToremove = new ArrayList<ArrayList<SegmentProcess>>();
		for (int k = 0; k < rootToLeaves.size(); k++) {
			ArrayList<SegmentProcess> pathArrayList = rootToLeaves.get(k);
			Set<Long> set = new HashSet<Long>();
			set.add(pathArrayList.get(0).startLocation.id);
			set.add(pathArrayList.get(0).endLocation.id);
			boolean checkRepetion = false;
			for (int j = 1; j < pathArrayList.size(); j++) {
				SegmentProcess temp = pathArrayList.get(j);
				long endId = temp.endLocation.id;
				if (set.contains(endId)) {
					checkRepetion = true;
					break;
				}
				else
					set.add(endId);
			}
			
			if(checkRepetion)
				tempToremove.add(pathArrayList);
		}
		rootToLeaves.removeAll(tempToremove);
		
        //calculate the Net Profit of each route
       
        RouteProfitMap[] netProfitMap = findAllNetProfit(rootToLeaves);
        if(netProfitMap.length!=0)
        {
       
        for(RouteProfitMap d : netProfitMap)
            //System.out.println(d.netProfit);
        topKRoute(netProfitMap, netProfitMap.length);
       
        //for(RouteProfitMap d : netProfitMap)
            //System.out.println(d.netProfit);
        //System.out.println("\n Net profit of the route");
       
               
		//System.out.println("Total number of routes : "+netProfitMap.length);
		if(netProfitMap.length<K)
			noOfRoutes=netProfitMap.length;
		else
			noOfRoutes=K;
		RouteProfitMap[] topKnetProfitMap=new RouteProfitMap[noOfRoutes];
		//System.out.println("Top K routes : ");
		for(int m=0;m<noOfRoutes;m++)
		{
			topKnetProfitMap[m]=netProfitMap[m];
			/*System.out.println(netProfitMap[m].netProfit);
			System.out.println(netProfitMap[m].route);
		
			System.out.println("******************************************************************\n");*/
		}
		
		//System.out.println(rootToLeaves.size() + " routes found..");
		
		HashSet<ArrayList<SegmentProcess>> routeSet = new HashSet<ArrayList<SegmentProcess>>();
		routeSet.addAll(rootToLeaves);
		//RouteProfitMap[] newRouteProfitMap=rTree.mainRtree(topKnetProfitMap,K);
		
		//rTree.calculateGridDirectionVectors(rootToLeaves,segmentProcessMapForTrain);
		RouteProfitMap[] newRouteProfitMap=rTree.calculateRouteDirectionVectors(topKnetProfitMap);
		segmentTopKRoutes.put(segmentProcess, newRouteProfitMap);
		
		
        } //end of if
        
        else
        {
        	noRouteFound=true;
   
        }
        }
		
		
	
	
	
	
	
		
		/*ArrayList<SegmentProcess> segmentProcessList = segmentProcessIdMap.get(start);
		SegmentProcess segmentProcess = null;
		for (SegmentProcess segmentProcessTemp : segmentProcessList) {
			if(segmentProcessTemp.endLocation.id == end)
			{
				segmentProcess = segmentProcessTemp;
				segmentProcess.noOfRoutesRecommended++;
				break;
			}
		}*/
		
		




	

}
