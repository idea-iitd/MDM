package preprocessing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import driver.Centralized;

import TaxiRecommendation.Main;
import TaxiRecommendation.SegmentProcess;
import preprocessing.SegmentFrequency;

public class SelectiveTestingData {

	/**
	 * @param args
	 */
	//contains top x% segments having non-zero frequency 
	public ArrayList<String> sortedTestData=new ArrayList<String>();
	
	//contains top x% segments and its frequency in that particular timeslot for segments having non-zero frequency 
	public Map<String,Float> topXSegments=new HashMap<String,Float>();
	
	public ArrayList<SegmentProcess> completeNeighbourList=new ArrayList<SegmentProcess>();
	public static String INPUT_FOLDER_PATH= "/home/nandani/Documents/sem-3/taxi-project2/";
	
	
	
	public ArrayList<String> choosetestData(String data,int timeslot,int dataPercentage,String testdata,String testDataType)
	{
		
		ArrayList<String> sortedData=new ArrayList<>();
			sortedData=getData(data, timeslot, dataPercentage,testDataType);
			if(testDataType.equals("topxneighbours") || testDataType.equals("bottomxneighbours"))
				sortedData=getNeighbours(testdata, timeslot);
		return sortedData;
	}
	
	
	
	
	
	
	
	//returns top dataPercentage segments having non-zero frequency 
	
	public ArrayList<String> getData(String data,int timeslot,int dataPercentage,String testDataType)
	{
		try {
		
		FileInputStream f1=new FileInputStream(data);
		BufferedReader br=new BufferedReader(new InputStreamReader(f1));
		String s1=" ";
		ArrayList<SegmentFrequency> segmentInfo=new ArrayList<>();
		int count=0;
		while((s1=br.readLine())!=null)
		{
			String segments[]=s1.split(","); 
			
			if((Float.parseFloat(segments[timeslot+1]))>0)
			{
				SegmentFrequency seg = new SegmentFrequency(segments[0],Float.parseFloat(segments[timeslot+1]),testDataType);
				segmentInfo.add(seg);
				count++;
				
			}
			
		}
		
		//Collections.sort(segmentInfo,new SegmentFrequency());
		
		//sorting the segments based on frequency in a particular timeslot
		Collections.sort(segmentInfo);
		
		//top dataPercentage segments to choose from segmentInfo
		int topDataSize=(int)((dataPercentage/100.0)*segmentInfo.size());
		//System.out.println(topDataSize);
		
		for(int i=0;i< topDataSize;i++)
		{
			sortedTestData.add(segmentInfo.get(i).segmentid);
			
			topXSegments.put(segmentInfo.get(i).segmentid,segmentInfo.get(i).frequency);
		}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

			return sortedTestData;
	}
	
	public ArrayList<String> getNeighbours(String testdata,int timeslot)
	{
		
		Main.populateSegmentProcessIdMap(testdata);

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
				if(segmentProcessTemp1.endLocation.id.toString().equals(endId.toString()))
					//if(segmentProcessTemp1.endLocation.id==end)
				{
					
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
				double[] freq2=new double[Centralized.noOfTimeslots];
				double freq=completeNeighbourList.get(i).freqSuccess[timeslot];
				float f1 = (float) freq; 
				topXSegments.put(completeNeighbourList.get(i).toString(),f1);
				
				
			}
		}
		
		


		for(Entry<Long, ArrayList<SegmentProcess>> entry:segmentProcessMapForTest.entrySet())
		{
			ArrayList<SegmentProcess> process=entry.getValue();			
			for(SegmentProcess segmentProcess1:process)
			{
				if(topXSegments.containsKey(segmentProcess1.toString()))
				{
					sortedTestData.add(segmentProcess1.toString());
					//System.out.println(segmentProcess1.toString());
				}
				
			}
		
		
		
		}

		
		
		return sortedTestData;
		
		
	}
		
		

	

}
