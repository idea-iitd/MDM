package preprocessing;

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
import java.util.TreeMap;

import TaxiRecommendation.Main;
import TaxiRecommendation.SegmentProcess;

public class findLowFreqSegments {
	
	public static String TRAIN_INPUT_SEGMENT_FILE = "/home/nandani/Documents/sem-3/taxi-project2/Datasets/NYLatestData/Normal/Normal75Train.csv";
	public static Map<Long, ArrayList<SegmentProcess>> segmentProcessMapForTrain;
	public static Map<Long, ArrayList<SegmentProcess>> segmentMap=new HashMap<>();
	public static String LOW_FREQUENCY_SEGMENTS_FILE="/home/nandani/Documents/sem-3/taxi-project2/Datasets/NYLatestData/LowFreqSegments.csv";
	public static Map<String,Long> lowFreqSegmentsMap=new HashMap<>();
	public static PrintStream out1;
	public int count;
	public String initialSegment="";
	public static String dataType="Normal";
	public static int[] dataSplit={75};
	public static String[] dataPhase={"Train","Test"};
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		out1=new PrintStream(new File(LOW_FREQUENCY_SEGMENTS_FILE));
		
		Main.populateSegmentProcessIdMap(TRAIN_INPUT_SEGMENT_FILE);

		segmentProcessMapForTrain=Main.segmentProcessIdMap;
		findLowFreqSegments obj=new findLowFreqSegments();
		
		for(Entry<Long, ArrayList<SegmentProcess>> entry:segmentProcessMapForTrain.entrySet())
		{

			segmentMap.put(entry.getKey(),entry.getValue());
		}

		System.out.println(segmentProcessMapForTrain.size());
		int countnoneighbour=0;
		int countreturnvalue=0;

		for(Entry<Long, ArrayList<SegmentProcess>> entry1:segmentMap.entrySet())
		{
			ArrayList<SegmentProcess> process=entry1.getValue();
			int removeSegmentOrNot=0;
			int i=0;
			int returnValue=0;
			for(SegmentProcess segmentProcess1:process)
			{
			obj.count=0;
			i++;
			returnValue=obj.checkSegmentNeighbourhood(segmentProcess1);
			ArrayList<SegmentProcess> neighbourlist1=segmentProcessMapForTrain.get(segmentProcess1.endLocation.id);
			if(returnValue==0)
			{
				out1.println("returnvalue"+","+segmentProcess1+","+segmentProcess1.freqSuccess[2]);
				lowFreqSegmentsMap.put(segmentProcess1.toString(), 0l);
				removeSegmentOrNot++;
				countreturnvalue++;

			}
			
			else if(neighbourlist1==null)
			{
				out1.println("no neighbour"+","+segmentProcess1+","+segmentProcess1.freqSuccess[2]);
				lowFreqSegmentsMap.put(segmentProcess1.toString(), 0l);
				removeSegmentOrNot++;
				countnoneighbour++;
				
			}

			}	
		if(i==removeSegmentOrNot && returnValue==0)
		{
			segmentProcessMapForTrain.remove(entry1.getKey());
		}
		


		}
		
		System.out.println(segmentProcessMapForTrain.size());	
		System.out.println("returnvaluecount "+countreturnvalue);
		System.out.println("noneighbourcount "+countnoneighbour);
	obj.removeLowFreqSegmentsFromData();


		
		

	}
	
	
	public int checkSegmentNeighbourhood(SegmentProcess segmentProcess1)
	{


		if(count==0)
		{
			initialSegment =segmentProcess1.toString();
		}	


		System.out.println(segmentProcess1.freqSuccess[0]+","+ segmentProcess1.freqSuccess[1]+","+ segmentProcess1.freqSuccess[2]);
		if(segmentProcess1.freqSuccess[2]==0)
		{
			if(count!=2)
			{
				int highFrequencySegment=0;
				ArrayList<SegmentProcess> neighbourlist1=segmentProcessMapForTrain.get(segmentProcess1.endLocation.id);


				count++;
				System.out.println("count"+count);
				if(neighbourlist1!=null)
				{

					
					for(int k=0;k<neighbourlist1.size();k++)
					{
					System.out.println(neighbourlist1.get(k));
					highFrequencySegment=checkSegmentNeighbourhood(neighbourlist1.get(k));
					count--;
					}


				}
				
				return highFrequencySegment ;
			}
			else
			{
				System.out.println(initialSegment);
				return 0;
			}




		}
		else
			return 1;

	}
	
	
	public void removeLowFreqSegmentsFromData() throws IOException
	{

		for(int i=0;i<dataSplit.length;i++)
		{
			for(int j=0;j<dataPhase.length;j++)
			{
				FileInputStream in =new FileInputStream("/home/nandani/Documents/sem-3/taxi-project2/Datasets/NYLatestData/"+dataType+"/"+dataType+Integer.toString(dataSplit[i])+dataPhase[j]+".csv");
				BufferedReader br=new BufferedReader(new InputStreamReader(in));
				PrintStream out1=new PrintStream("/home/nandani/Documents/sem-3/taxi-project2/Datasets/NYLatestData/Processed"+dataType+"/"+dataType+Integer.toString(dataSplit[i])+dataPhase[j]+".csv");
				String str="";
				while((str=br.readLine())!=null)
				{
					String s1[]=str.split(";");
					String s11[]=s1[0].split(",");
					String s12[]=s1[1].split(",");
					String startId=s11[0];
					String endId=s12[0];
					String segment=startId+" "+endId;
					if(lowFreqSegmentsMap.containsKey(segment))
					{}
					else
						out1.println(str);
					
					
					
					
				}
			}
		}

	}
	
	
	
	
	
	

}
