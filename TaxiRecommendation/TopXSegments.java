package TaxiRecommendation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class TopXSegments {


	//Sorted data based on frequency used for testing
	public static ArrayList<String> sortedTestData=new ArrayList<String>();
	public static String dataCategory="Top";
	public static Map<Long, ArrayList<SegmentProcess>> segmentProcessIdMap;
	public static int totalNoOfSegments=7963;
	public static int x=(int)(0.05*totalNoOfSegments);
	public static Map<String,double[]> topXSegments=new HashMap<String,double[]>();
	public static ArrayList<SegmentProcess> completeNeighbourList=new ArrayList<SegmentProcess>();




	public static void main(String[] args) throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
		
		int dataType[]={75};
		int distance[]={1000};
		

		String str;
		int count=0;
		double total_Frequency=0;
		FileInputStream f1=new FileInputStream("TestingData/"+dataCategory+dataType[0]+"/DecreaseSortedTestData.csv");
		BufferedReader br=new BufferedReader(new InputStreamReader(f1));
		while((str=br.readLine())!=null && count<x)
		{
			String s[]=str.split(",");
			sortedTestData.add(s[0]);
			//System.out.println(s[0]);
			double[] freq=new double[3];
			freq[0]=Double.parseDouble(s[1]);
			freq[1]=Double.parseDouble(s[2]);
			freq[2]=Double.parseDouble(s[3]);
			topXSegments.put(s[0],freq);
			total_Frequency=total_Frequency+Float.parseFloat(s[3]);
			count++;



		}


		
		
		
		PrintStream out1=new PrintStream(new File("TestingData/"+dataCategory+dataType[0]+"/Top5Neighbours.csv"));
		PrintStream out2=new PrintStream(new File("TestingData/"+dataCategory+dataType[0]+"/Bottom5Neighbours.csv"));

		for(int y=0;y<dataType.length;y++)
		{


			for(int m=0;m<distance.length;m++)
			{
			
				String TRAIN_INPUT_SEGMENT_FILE = "/home/nandani/Documents/sem-3/taxi project2/Sa_dataset/ExperimentData/"+dataCategory+dataType[y]+"/CompleteSegmentDataTrain.csv";
				String TEST_INPUT_SEGMENT_FILE= "/home/nandani/Documents/sem-3/taxi project2/Sa_dataset/ExperimentData/"+dataCategory+dataType[y]+"/CompleteSegmentDataTest.csv";

				Main.populateSegmentProcessIdMap(TEST_INPUT_SEGMENT_FILE);

				Map<Long, ArrayList<SegmentProcess>> segmentProcessMapForTest=Main.segmentProcessIdMap;



				for(int t=0;t<sortedTestData.size();t++)
				{

					String segmentId=sortedTestData.get(t);
					String[] segmentIdPartition=segmentId.split(" ");
					Long startId=Long.parseLong(segmentIdPartition[0]);
					Long endId=Long.parseLong(segmentIdPartition[1]);

					ArrayList<SegmentProcess> segmentProcessList = segmentProcessMapForTest.get(startId);
					ArrayList<SegmentProcess> secondNeighbourList=new ArrayList<SegmentProcess>();
					ArrayList<SegmentProcess> firstNeighbourList = new ArrayList<SegmentProcess>();
					
					SegmentProcess segmentProcess = null;
					for (SegmentProcess segmentProcessTemp1 : segmentProcessList) {
						//System.out.println(segmentProcessTemp1.endLocation.id+" "+ end);
						if(segmentProcessTemp1.endLocation.id.toString().equals(endId.toString()))
							//if(segmentProcessTemp1.endLocation.id==end)
						{
							//System.out.println("nandu");
							segmentProcess = segmentProcessTemp1;
							firstNeighbourList=segmentProcessMapForTest.get(segmentProcess.endLocation.id);
							if(firstNeighbourList!=null)
							{
								for(SegmentProcess segmentTemp:firstNeighbourList)
								{
									
									completeNeighbourList.add(segmentTemp);
									secondNeighbourList=segmentProcessMapForTest.get(segmentTemp.endLocation.id);
									if(secondNeighbourList!=null)
									{
										for(SegmentProcess segment2:secondNeighbourList)
											completeNeighbourList.add(segment2);
									}
									
									
									
									

								}

								
							}

						}

					} //end of for loop     


				}          //end of outer for loop

				
				for(int i=0;i<completeNeighbourList.size();i++)
				{
					
					if(topXSegments.containsKey(completeNeighbourList.get(i))==false)
					{
						double[] freq2=new double[3];
						freq2[0]=completeNeighbourList.get(i).freqSuccess[0];
						freq2[1]=completeNeighbourList.get(i).freqSuccess[1];
						freq2[2]=completeNeighbourList.get(i).freqSuccess[2];
						topXSegments.put(completeNeighbourList.get(i).toString(),freq2);
						
						
					}
				}
				
				


				for(Entry<Long, ArrayList<SegmentProcess>> entry:segmentProcessMapForTest.entrySet())
				{
					ArrayList<SegmentProcess> process=entry.getValue();			
					for(SegmentProcess segmentProcess1:process)
					{
						if(topXSegments.containsKey(segmentProcess1.toString()))
							out1.println(segmentProcess1.toString()+","+segmentProcess1.freqSuccess[0]+","+segmentProcess1.freqSuccess[1]+","+segmentProcess1.freqSuccess[2]);
						else
							out2.println(segmentProcess1.toString()+","+segmentProcess1.freqSuccess[0]+","+segmentProcess1.freqSuccess[1]+","+segmentProcess1.freqSuccess[2]);
						
						
					}
				
				
				
				}


			}   //end of m

		}       //end of y

	}
	
}
