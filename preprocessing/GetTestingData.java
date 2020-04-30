package preprocessing;


/**
 * This file takes as input CompleteSegments.csv(Here Train10 or Test10) depending upon the data category and store general testing data in a file 
 * given by "testData" in the format "Segment start id Segment end id,customer frequency[timeslot1],customer frequency[timeslot2],customer frequency[timeslot3],customer frequency[timeslot4]
 * 
 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import recomendation.Location;
//import driver.CentralizedCopy;

import TaxiRecommendation.SegmentProcess;
import TaxiRecommendation.Main;

public class GetTestingData {

	/**
	 * @param args
	 */
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
	
	/***
	 * dataType ={10,25,50,75,90}
	 * Percentage of data used for training UCT algorithm. 
	 * 
	 */
	
	public static int[] dataType={10};
	
	
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
	//public static int timeslot=2;
	
	
	public static Map<Long, ArrayList<SegmentProcess>> segmentProcessIdMap;
	
	public static String[] timeslotRange=new String[]{"0-7","8-11","12-16","17-23"};
	
	public static int noOfTimesCrossValidation = 10;
	
	
	public static void populateSegmentProcessIdMap(String filename)
	{

		segmentProcessIdMap= new HashMap<Long, ArrayList<SegmentProcess>>();
		String[] tr=timeslotRange;
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
				double[] distanceTravelled = new double[noOfTimeslots];
				double[] freqSuccess = new double[noOfTimeslots];
				double[] freqFailure = new double[noOfTimeslots];
				for (int i = 0; i < subString.length; i++) {
					
					for(int s=0;s<tr.length;s++)
					{
						
						if(i>=Integer.parseInt(tr[s].split("-")[0]) && i<=Integer.parseInt(tr[s].split("-")[1]))
						{
							//System.out.println(Integer.parseInt(tr[s].split("-")[0]) +","+Integer.parseInt(tr[s].split("-")[1]));
							distanceTravelled[s] += Double.parseDouble(subString[i]);
							freqSuccess[s] += Double.parseDouble(subString2[i]);
							freqFailure[s] += Double.parseDouble(subString3[i]);
							break;
						}
					}
				

				}
				for(int n=0;n<tr.length;n++)
				{
				//System.out.println(Integer.parseInt(tr[n].split("-")[1]) - Integer.parseInt(tr[n].split("-")[0])+1);
				distanceTravelled[n] /= Integer.parseInt(tr[n].split("-")[1]) - Integer.parseInt(tr[n].split("-")[0])+1;
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


	

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
		for(int y=0;y<dataType.length;y++)
		{
		
		
			for(int c=1;c<=noOfTimesCrossValidation;c++)
			{
			
		String TRAIN_INPUT_SEGMENT_FILE=INPUT_FOLDER_PATH+"Datasets/SFExperimentData/"+dataCategory+dataType[y]+"/CompleteSegmentDataTrain_"+c+".csv";
		String TEST_INPUT_SEGMENT_FILE=INPUT_FOLDER_PATH+"Datasets/SFExperimentData/"+dataCategory+dataType[y]+"/CompleteSegmentDataTest_"+c+".csv";

			PrintStream testData=new PrintStream(new File(INPUT_FOLDER_PATH+"Bandit/TestingData/"+dataCategory+dataType[y]+"/NewCompleteSegments_"+c+".csv"));
			
			populateSegmentProcessIdMap(TEST_INPUT_SEGMENT_FILE);
			double[] frequency_count=new double[noOfTimeslots];
			for(Entry<Long, ArrayList<SegmentProcess>> entry:segmentProcessIdMap.entrySet())
			{
				ArrayList<SegmentProcess> process=entry.getValue();
			
				for(SegmentProcess segmentProcess1:process)
				{
					double[] frequencyOfSegment =new double[noOfTimeslots];
					frequencyOfSegment=segmentProcess1.freqSuccess;
					testData.print(segmentProcess1.toString());
					for(int k=0;k<frequencyOfSegment.length;k++)
					{
					frequency_count[k]=frequency_count[k]+frequencyOfSegment[k];
					testData.print(","+segmentProcess1.freqSuccess[k]);
					}
					testData.println();
				

				}

			}
			System.out.println("Processing "+dataCategory+" "+dataType[y]);

		}
		}
}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
