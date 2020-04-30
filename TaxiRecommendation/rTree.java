package TaxiRecommendation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

import recomendation.GpsSignal;
import recomendation.Location;

/**
 * 
 * @author animesh
 * 
 * phase 1: (data preprocessing)
 * 
 * 1) Extract locations and segments from the map and write relevant segments and locations in a file.
 * 
 * input file: locations file - format: (<location_id>,LLA(<latitude>,<longitude>,<altitude>))
 * input file: segments file - format: Segment(<start_location_id>,<end_location_id>,[<ids of locations in the segment separated
 * 											    by commas>],<segment_length>,<parent_road_class>,<parent_road_id>,<is_one_way>)
 * format of segments: <start_location_id>;<end_location_id>;<segment_length>;<ids of locations in the segment separated by commas>
 */



public class rTree {
	static int GRID_DIM =100;
	public static Grid[][]  grids = new Grid[GRID_DIM*2][GRID_DIM*2];
	public static HashSet<SegmentProcess> segmentsUsed = new HashSet<SegmentProcess>();
	static int count=0;

	
	/**
	 * The name of the folder that contains all files related to the GPS traces of taxis.
	 */
	public static String PARENT_FOLDER = /*"C:\\Users\\Nandani garg\\Desktop\\taxi project2\\cabspottingdata\\";*/ "/home/nandani/Documents/sem-3/taxi-project2/Datasets/cabspottingdata/";
	
	/**
	 * This file contains the names of all files whose GPS data is to be processed.
	 * <p>
	 * <dl>
	 * <dt>file format:
	 * <dd>&lt;cab id="&lt;file name&gt;" updates="&lt;number of lines in the file&gt;"/&gt;
	 * </dl>
	 * example: &lt;cab id="ikdagcy" updates="18109"/&gt;
	 */
	public static String GPS_CAB_METADATA = PARENT_FOLDER + "_cabs.txt";

	
	public static double getEuclideanDistance(SegmentProcess segmentProcess)
		{
		double r = 3959; // miles
		double φ1 = Math.toRadians(segmentProcess.startLocation.latitude);
		double φ2 = Math.toRadians(segmentProcess.endLocation.latitude);
		double Δφ = φ2 - φ1;
		double Δλ = Math.toRadians(segmentProcess.endLocation.longitude - segmentProcess.startLocation.longitude);

		double a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
		        Math.cos(φ1) * Math.cos(φ2) *
		        Math.sin(Δλ/2) * Math.sin(Δλ/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

		double d = r * c;
		return d;
		}
	
	
	
	/**  
	 * This function converts decimal degrees to radians
	 * @param deg degree        
	 */
	private static double deg2rad(double deg) 
	{
		return (deg * Math.PI / 180.0);
	}
	
	
	/**
	 * This function finds the distance between given two location i.e GPS traces of a Taxi.
	 * @param location start location of GPS traces
	 * @param location end location of a GPS traces 
	 * @return The distance between the given two location of the GPS traces.
	 */
	private static double getDistanceFromLatLonInKm(double latitude1,double longitude1,double latitude2,double longitude2)
	{
		double lat1 = latitude1;
		double lon1 = longitude1;
		double lat2 = latitude2;
		double lon2 = longitude2;
		
		double R = 6371000; // Radius of the earth in metres
		double dLat = deg2rad(lat2-lat1);  // deg2rad below
		double dLon = deg2rad(lon2-lon1); 
		double a = 
				Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * 
				Math.sin(dLon/2) * Math.sin(dLon/2); 
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		double d = R * c; // Distance in metres
		return d;
	}
	
	public static void calculateGridDirectionVectors(Map<Long, ArrayList<SegmentProcess>> segmentProcessIdMap) throws FileNotFoundException
	{
		// Draw Grid Structure by finding initial latitude and longitude
		PrintStream out2=new PrintStream(new File("NewGrids.txt"));
		double maxLongitude, minLongitude, maxLatitude, minLatitude;
		maxLatitude = maxLongitude = -1 * Double.MAX_VALUE;
		minLatitude = minLongitude = Double.MAX_VALUE;
		PrintStream out1=new PrintStream(new File("DirectionVector/NewTrain.csv"));
		
		
		for(Entry<Long,ArrayList<SegmentProcess>> entry:segmentProcessIdMap.entrySet())
		{
			ArrayList<SegmentProcess> arrayList=entry.getValue();
			for (SegmentProcess segmentProcess : arrayList) {
				
				if(maxLatitude < segmentProcess.endLocation.latitude)
					maxLatitude = segmentProcess.endLocation.latitude;
				if(minLatitude > segmentProcess.endLocation.latitude)
					minLatitude = segmentProcess.endLocation.latitude;
				if(maxLatitude < segmentProcess.startLocation.latitude)
					maxLatitude = segmentProcess.startLocation.latitude;
				if (minLatitude > segmentProcess.startLocation.latitude)
					minLatitude = segmentProcess.startLocation.latitude;
				if (maxLongitude < segmentProcess.endLocation.longitude)
					maxLongitude = segmentProcess.endLocation.longitude;
				if(minLongitude > segmentProcess.endLocation.longitude)
					minLongitude = segmentProcess.endLocation.longitude;
				if(maxLongitude < segmentProcess.startLocation.longitude)
					maxLongitude = segmentProcess.startLocation.longitude;
				if (minLongitude > segmentProcess.startLocation.longitude)
					minLongitude = segmentProcess.startLocation.longitude;
			}
		}
		double midlatitude=(maxLatitude+minLatitude)/2;
		double midlongitude=(maxLongitude+minLongitude)/2;
		System.out.println(maxLatitude);
		System.out.println(maxLongitude);
		System.out.println(minLatitude);
		System.out.println(minLongitude);
		System.out.println(midlatitude);
		System.out.println(midlongitude);
		System.out.println(getDistanceFromLatLonInKm(maxLatitude,maxLongitude,minLatitude,minLongitude));
		double max1 = Math.max(Math.abs(maxLatitude - midlatitude), Math.abs(midlatitude-minLatitude));
		//System.out.println("maximum Latitude = " + max1);
		double max2 = Math.max(Math.abs(maxLongitude - midlongitude), Math.abs(midlongitude-minLongitude));
		//System.out.println("maximum Longitude = " + max2);
		double max = Math.max(max1, max2);
		System.out.println("maximum = " + max);
		double interval = max/GRID_DIM;
		double initialLatitude = midlatitude + interval* GRID_DIM ;
		int noOfPointsINLatitudeDirection=0,storejCount=0;
		for (int i = 0; i < GRID_DIM*2; i++) {
			if(initialLatitude>midlatitude-max)
			{
			noOfPointsINLatitudeDirection++;
			double initialLongitude = midlongitude - interval*GRID_DIM;
			int noOfPointsInLongitudeDirection=0;
			for (int j = 0; j < GRID_DIM*2; j++) {
				if(initialLongitude<midlongitude+max)
				{
					noOfPointsInLongitudeDirection++;
					grids[i][j] = new Grid();
					grids[i][j].latitudetop = initialLatitude;
					grids[i][j].longitudeleft = initialLongitude;
					grids[i][j].latitudeBottom = initialLatitude - 2*interval;
					grids[i][j].longitudeRight = initialLongitude + 2*interval;
					//System.out.println(getDistanceFromLatLonInKm(grids[i][j].latitudetop, grids[i][j].longitudeleft , grids[i][j].latitudetop, grids[i][j].longitudeRight
						/*));*/
					initialLongitude += 2*interval;
				}
			}
			storejCount=noOfPointsInLongitudeDirection;
			System.out.println("j"+noOfPointsInLongitudeDirection);
			initialLatitude -= 2*interval;
			}
		}
		System.out.println("i"+noOfPointsINLatitudeDirection);
		
	/**DEBUG STATEMENTS START*/
		//System.out.println("maximum" + max);
		for (int i = 0; i < noOfPointsINLatitudeDirection-1; i++) {
			for (int j = 0; j < storejCount; j++) {
				out2.print(grids[i][j].latitudetop+","+grids[i][j].longitudeleft);
				out2.println();
				
				
				
			}
			//out2.println();
		}
	/**DEBUG STATEMENTS END
	 * @throws FileNotFoundException */
	
		//compute frequency of each grid
		populateFrequencyCount(grids,noOfPointsINLatitudeDirection,storejCount);
		
		//compute direction vectors of each grid
		for(int i = 0 ; i < noOfPointsINLatitudeDirection-1; ++i)
			for(int j = 0 ; j < storejCount; ++j)
				grids[i][j].calculateDirectionVector();

		/*for(int i = 0 ; i < 2 * GRID_DIM ; ++i)
			for(int j = 0 ; j < 2 * GRID_DIM ; ++j)
				{
				for(double dirVec : grids[i][j].directionVector)
				{
				System.out.print(dirVec + " ");	
				}
				System.out.println();
				}
*/
		
		for(Entry<Long,ArrayList<SegmentProcess>> entry:segmentProcessIdMap.entrySet())
		{
			ArrayList<SegmentProcess> arrayList=entry.getValue();
			for (SegmentProcess segmentProcess : arrayList) {
				
				
				for(int i = 0 ; i < noOfPointsINLatitudeDirection; ++i){
					for(int j = 0 ; j < storejCount; ++j)
					{
						int direction = grids[i][j].getDirection(segmentProcess);
						if(direction == 0)
							continue;
		
						for(int k=0;k<grids[i][j].directionVector.size();k++)
						{
							segmentProcess.directionVector.add(grids[i][j].directionVector.get(k));
						}
						
						
					}
				}
				
				out1.print(segmentProcess.startLocation.id+" "+segmentProcess.endLocation.id+":");
				for(int i=0;i<segmentProcess.directionVector.size();i++)
				{
					out1.print(segmentProcess.directionVector.get(i)+",");
				}
				out1.println();
				
															}
			

		}  //outer for loop
	
	

	}
	
	
	
	
	/*public static void calculateGridDirectionVectors(ArrayList<ArrayList<SegmentProcess>> rootToLeaves,Map<Long, ArrayList<SegmentProcess>> segmentProcessIdMap ) throws FileNotFoundException
	{
		// Draw Grid Structure by finding initial latitude and longitude
		PrintStream out1=new PrintStream(new File("SegmentFrequencyVectors.txt"));
		double maxLongitude, minLongitude, maxLatitude, minLatitude;
		maxLatitude = maxLongitude = -1 * Double.MAX_VALUE;
		minLatitude = minLongitude = Double.MAX_VALUE;
		
		Iterator<ArrayList<SegmentProcess>> pathItr = rootToLeaves.iterator();
		while (pathItr.hasNext()) {
			ArrayList<SegmentProcess> arrayList = pathItr.next();
			for (SegmentProcess segmentProcess : arrayList) {
				
				if(maxLatitude < segmentProcess.endLocation.latitude)
					maxLatitude = segmentProcess.endLocation.latitude;
				if(minLatitude > segmentProcess.endLocation.latitude)
					minLatitude = segmentProcess.endLocation.latitude;
				if(maxLatitude < segmentProcess.startLocation.latitude)
					maxLatitude = segmentProcess.startLocation.latitude;
				if (minLatitude > segmentProcess.startLocation.latitude)
					minLatitude = segmentProcess.startLocation.latitude;
				if (maxLongitude < segmentProcess.endLocation.longitude)
					maxLongitude = segmentProcess.endLocation.longitude;
				if(minLongitude > segmentProcess.endLocation.longitude)
					minLongitude = segmentProcess.endLocation.longitude;
				if(maxLongitude < segmentProcess.startLocation.longitude)
					maxLongitude = segmentProcess.startLocation.longitude;
				if (minLongitude > segmentProcess.startLocation.longitude)
					minLongitude = segmentProcess.startLocation.longitude;
			}
		}
		SegmentProcess startSegmentProcess = rootToLeaves.get(0).get(0);
		
		double max1 = Math.max(Math.abs(maxLatitude - startSegmentProcess.startLocation.latitude), Math.abs(startSegmentProcess.startLocation.latitude-minLatitude));
		System.out.println("maximum Latitude = " + max1);
		double max2 = Math.max(Math.abs(maxLongitude - startSegmentProcess.startLocation.longitude), Math.abs(startSegmentProcess.startLocation.longitude-minLongitude));
		System.out.println("maximum Longitude = " + max2);
		System.out.println("33333333333333");
		double max = Math.max(max1, max2);
		System.out.println("maximum = " + max);
		System.out.println();
		double interval = max/GRID_DIM;
		double initialLatitude = startSegmentProcess.startLocation.latitude + interval* GRID_DIM ;
		for (int i = 0; i < GRID_DIM*2; i++) {
			double initialLongitude = startSegmentProcess.startLocation.longitude - interval*GRID_DIM;
			for (int j = 0; j < GRID_DIM*2; j++) {
				grids[i][j] = new Grid();
				grids[i][j].latitudetop = initialLatitude;
				grids[i][j].longitudeleft = initialLongitude;
				grids[i][j].latitudeBottom = initialLatitude - interval;
				grids[i][j].longitudeRight = initialLongitude + interval;
				initialLongitude += interval;
			}
			initialLatitude -= interval;
		}
		
	*//**DEBUG STATEMENTS START*//*
		System.out.println("maximum" + max);
		for (int i = 0; i < GRID_DIM*2; i++) {
			for (int j = 0; j < GRID_DIM*2; j++) {
				System.out.print(grids[i][j].latitudetop+" "+grids[i][j].longitudeleft+"\t");
			}
			System.out.println();
		}
	*//**DEBUG STATEMENTS END*//*
	
		//compute frequency of each grid
		populateFrequencyCount(grids);
		
		//compute direction vectors of each grid
		for(int i = 0 ; i < GRID_DIM * 2 ; ++i)
			for(int j = 0 ; j < GRID_DIM * 2 ; ++j)
				grids[i][j].calculateDirectionVector();

		for(int i = 0 ; i < 2 * GRID_DIM ; ++i)
			for(int j = 0 ; j < 2 * GRID_DIM ; ++j)
				{
				for(double dirVec : grids[i][j].directionVector)
				{
				System.out.print(dirVec + " ");	
				}
				System.out.println();
				}
		
		
		
		for(Entry<Long,ArrayList<SegmentProcess>> entry:segmentProcessIdMap.entrySet())
		{
			ArrayList<SegmentProcess> arrayList=entry.getValue();
			for (SegmentProcess segmentProcess : arrayList) {
				
				
				for(int i = 0 ; i < GRID_DIM * 2 ; ++i)
					for(int j = 0 ; j < GRID_DIM * 2 ; ++j)
					{
						int direction = grids[i][j].getDirection(segmentProcess);
						if(direction == 0)
							continue;
		
						for(int k=0;k<grids[i][j].directionVector.size();k++)
						{
							segmentProcess.directionVector.add(grids[i][j].directionVector.get(k));
						}
						
						
					}
				
				out1.print(segmentProcess.startLocation.id+" "+segmentProcess.endLocation.id+":");
				for(int i=0;i<segmentProcess.directionVector.size();i++)
				{
					out1.print(segmentProcess.directionVector.get(i)+",");
				}
				out1.println();
				
															}

		}  //outer for loop
	
	
		
		
		
		

	}*/
	
	
	
	public static void populateFrequencyCount(Grid[][] grids,int noOfPointsINLatitudeDirection1,int storejCount1 )
	{
	try
		{
		
		System.out.println("populateFrequencyCount");
		BufferedReader in = new BufferedReader(new FileReader(GPS_CAB_METADATA));
		String string;
		
		//read the contents of the file containing the metadata
		while((string = in.readLine()) != null)
			{
			String[] subStrings = string.split(" ");
			
			//subStrings[1] now contains the name of the file containing the GPS traces
			//TODO: remove "id=" from subStrings[1]
			String[] str = subStrings[1].split("=");
			str[1] = str[1].substring(1, str[1].length() - 1);
			String fileName = PARENT_FOLDER + "new_" + str[1] + ".txt";
			
			//process the GPS traces
			//System.out.println("Processing data from " + fileName + "..");
			frequencyCount(grids, fileName,noOfPointsINLatitudeDirection1,storejCount1);
			}
		in.close();
		}
		catch(IOException e)
			{
			e.printStackTrace();
			}
	}
	
	
	public static void frequencyCount(Grid[][] grids, String fileName,int noOfPointsINLatitudeDirection2,int storejCount2)
	{
		SegmentProcess segmentProcess = null;
		File file = new File(fileName);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String string;
			double startLatitude, startLongitude, endLatitude = 0, endLongitude = 0;
			double[] distanceTravelled = new double[recomendation.Main.noOfTimeSlot];
			double[] freqSuccess = new double[recomendation.Main.noOfTimeSlot];
			double[] freqFailure = new double[recomendation.Main.noOfTimeSlot];
			Arrays.fill(distanceTravelled,0.0);
			Arrays.fill(freqSuccess,0.0);
			Arrays.fill(freqFailure,0.0);
			if((string = br.readLine()) != null)
				{
				String[] strings = string.split(" ");
				endLatitude = Double.parseDouble(strings[0]);
				endLongitude = Double.parseDouble(strings[1]);
				}
			while((string = br.readLine())!=null)
			{
				int count=0;
				startLatitude = endLatitude;
				startLongitude = endLongitude;
				String[] strings = string.split(" ");
				endLatitude = Double.parseDouble(strings[0]);
				endLongitude = Double.parseDouble(strings[1]);
				
				segmentProcess = new SegmentProcess(new Location(1L, startLatitude, startLongitude), new Location(2L, endLatitude, endLongitude),0.0, distanceTravelled, freqSuccess,freqFailure,0.0);
				
				//check if this line segment
				for(int i = 0 ; i < noOfPointsINLatitudeDirection2-1; ++i )
					for (int j = 0; j < storejCount2; j++) {
						int direction = grids[i][j].getDirection(segmentProcess);
						if(direction == 0)
							continue;
						//System.out.println("direction is "+direction);
						int oldFreq = grids[i][j].frequencyVector.get(direction - 1);
						grids[i][j].frequencyVector.set(direction - 1, oldFreq + 1);
						//System.out.println((grids[i][j].latitudetop+","+grids[i][j].longitudeRight+","+grids[i][j].longitudeleft+","+grids[i][j].latitudeBottom+","+count));
						//System.out.println();
						count++;
					}
				//System.out.println("Count is"+count+" for segment of length "+segmentProcess);
				//System.out.println(segmentProcess.startLocation.latitude+","+segmentProcess.startLocation.longitude+","+segmentProcess.endLocation.latitude+","+segmentProcess.endLocation.longitude);
			}
			
			
//			for(int i = 0 ; i < 2 * GRID_DIM ; ++i)
//				for(int j = 0 ; j < 2 * GRID_DIM ; ++j)
//					{
//					for(int freq : grids[i][j].frequencyVector)
//					{
//					System.out.print(freq+" ");	
//					}
//					System.out.println();
//					}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static class gridNode
	{
		Grid grid;
		SegmentProcess segmentProcess;
		public gridNode(Grid grid, SegmentProcess segmentProcess) {
			// TODO Auto-generated constructor stub
			this.grid = grid;
			this.segmentProcess = segmentProcess;
		}
	}
	
	
	//Check in this method whether its p1 or p2 again and understand its meaning
	static public Comparator<gridNode> asclist = new Comparator<gridNode>(){
       // @Override
        public int compare(gridNode b1, gridNode b2){
        	double p1 = 0.0;
        	double p2 = 0.0;
        	
        	if(p1 < Math.sqrt(Math.pow((b1.grid.latitudeBottom - b1.segmentProcess.startLocation.latitude), 2)+ Math.pow((b1.grid.longitudeRight - b1.segmentProcess.startLocation.longitude), 2)) )
        		p1 = Math.sqrt(Math.pow((b1.grid.latitudeBottom - b1.segmentProcess.startLocation.latitude), 2)+ Math.pow((b1.grid.longitudeRight - b1.segmentProcess.startLocation.longitude), 2)) ;
        	if(p1 < Math.sqrt(Math.pow((b1.grid.latitudeBottom - b1.segmentProcess.startLocation.latitude), 2)+ Math.pow((b1.grid.longitudeleft - b1.segmentProcess.startLocation.longitude), 2)) )
        		p1 = Math.sqrt(Math.pow((b1.grid.latitudeBottom - b1.segmentProcess.startLocation.latitude), 2)+ Math.pow((b1.grid.longitudeleft - b1.segmentProcess.startLocation.longitude), 2)) ;
        	if(p1 < Math.sqrt(Math.pow((b1.grid.latitudetop - b1.segmentProcess.startLocation.latitude), 2)+ Math.pow((b1.grid.longitudeRight - b1.segmentProcess.startLocation.longitude), 2)) )
        		p1 = Math.sqrt(Math.pow((b1.grid.latitudetop - b1.segmentProcess.startLocation.latitude), 2)+ Math.pow((b1.grid.longitudeRight - b1.segmentProcess.startLocation.longitude), 2)) ;
        	if(p1 < Math.sqrt(Math.pow((b1.grid.latitudetop - b1.segmentProcess.startLocation.latitude), 2)+ Math.pow((b1.grid.longitudeleft - b1.segmentProcess.startLocation.longitude), 2)) )
        		p1 = Math.sqrt(Math.pow((b1.grid.latitudetop - b1.segmentProcess.startLocation.latitude), 2)+ Math.pow((b1.grid.longitudeleft - b1.segmentProcess.startLocation.longitude), 2)) ;

        	if(p2 < Math.sqrt(Math.pow((b2.grid.latitudeBottom - b2.segmentProcess.startLocation.latitude), 2)+ Math.pow((b2.grid.longitudeRight - b2.segmentProcess.startLocation.longitude), 2)) )
        		p2 = Math.sqrt(Math.pow((b2.grid.latitudeBottom - b2.segmentProcess.startLocation.latitude), 2)+ Math.pow((b2.grid.longitudeRight - b2.segmentProcess.startLocation.longitude), 2)) ;
        	if(p2 < Math.sqrt(Math.pow((b2.grid.latitudeBottom - b2.segmentProcess.startLocation.latitude), 2)+ Math.pow((b2.grid.longitudeleft - b2.segmentProcess.startLocation.longitude), 2)) )
        		p2 = Math.sqrt(Math.pow((b2.grid.latitudeBottom - b2.segmentProcess.startLocation.latitude), 2)+ Math.pow((b2.grid.longitudeleft - b2.segmentProcess.startLocation.longitude), 2)) ;
        	if(p2 < Math.sqrt(Math.pow((b2.grid.latitudetop - b2.segmentProcess.startLocation.latitude), 2)+ Math.pow((b2.grid.longitudeRight - b2.segmentProcess.startLocation.longitude), 2)) )
        		p2 = Math.sqrt(Math.pow((b2.grid.latitudetop - b2.segmentProcess.startLocation.latitude), 2)+ Math.pow((b2.grid.longitudeRight - b2.segmentProcess.startLocation.longitude), 2)) ;
        	if(p2 < Math.sqrt(Math.pow((b2.grid.latitudetop - b2.segmentProcess.startLocation.latitude), 2)+ Math.pow((b2.grid.longitudeleft - b2.segmentProcess.startLocation.longitude), 2)) )
        		p2 = Math.sqrt(Math.pow((b2.grid.latitudetop - b2.segmentProcess.startLocation.latitude), 2)+ Math.pow((b2.grid.longitudeleft - b2.segmentProcess.startLocation.longitude), 2)) ;
        	
            return (int) (Math.ceil(p1 - p2));
        }
    };
    
    
    /*public static void calculateRouteDirectionVectors(RouteProfitMap[] routeProfitMaps)
	{
	for (int k = 0; k < routeProfitMaps.length; k++) {
		
		ArrayList<SegmentProcess> route = routeProfitMaps[k].route;
	//ArrayList<ArrayList<Double>> DirectionalVector = new ArrayList<ArrayList<Double>>();
	
		ArrayList<Double> tempDirectionalRoute = new ArrayList<Double>();
//		ArrayList<SegmentProcess> arrayList = (ArrayList<SegmentProcess>) itrArrayList.next();
//		Iterator<SegmentProcess> itr = arrayList.iterator();
		for (int j = 0; j < route.size(); j++) {
			
			ArrayList<gridNode> gridList  = new ArrayList<gridNode>();
			SegmentProcess segmentProcess = route.get(j);
			for (int m = 0; m < 2*GRID_DIM; m++) {
				for (int n = 0; n < 2*GRID_DIM; n++) {
					int val = grids[m][n].getDirection(segmentProcess);
					if(val != 0)
						gridList.add(new gridNode(grids[m][n], segmentProcess));
				}
			}
			System.out.println(gridList.size());
			Collections.sort(gridList,asclist);
			Iterator<gridNode> itrGrid = gridList.iterator();
			while (itrGrid.hasNext()) {
				gridNode grid =  itrGrid.next();
				for(double dir : grid.grid.directionVector)
				{
					tempDirectionalRoute.add(dir);
				}
			}
		}
		
		routeProfitMaps[k].setdirectionVector(tempDirectionalRoute);
	}
	
	System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
	for (int i = 0; i<routeProfitMaps.length; i++) {
		ArrayList<Double> arrayList = routeProfitMaps[i].getDirectionVector();
		for (Double double1 : arrayList) {
			System.out.print(double1+" ");
		}
		System.out.println();
	}
	
//	ArrayList<ArrayList> 
//	ArrayList<Double> routeA = DirectionalVector.get(0);
	ArrayList<RouteProfitMap> processedList = new ArrayList<RouteProfitMap>();
	ArrayList<RouteProfitMap> stillToBeprocessedList = new ArrayList<RouteProfitMap>();
	processedList.add(routeProfitMaps[0]);
	for( int i = 1; i < routeProfitMaps.length ; i++ )  stillToBeprocessedList.add(routeProfitMaps[i]);
	for (int i = 1; i < routeProfitMaps.length; i++)   // i =2 because last element don't need to be compared
	{
		int index = nextUncorrelatedThing(processedList ,stillToBeprocessedList );
		RouteProfitMap element = stillToBeprocessedList.get(index);
		stillToBeprocessedList.remove(index);
		processedList.add(element);
	}
	
	for (RouteProfitMap proceMap : processedList) {
		System.out.println("Correlation : "+proceMap.getNetProfit()+"  "+proceMap.route);
	}
	
	}
	*/
    
    
	public static RouteProfitMap[] calculateRouteDirectionVectors(RouteProfitMap[] routeProfitMaps)
		{
		for (int k = 0; k < routeProfitMaps.length; k++) {
			
			ArrayList<SegmentProcess> route = routeProfitMaps[k].route;
		//ArrayList<ArrayList<Double>> DirectionalVector = new ArrayList<ArrayList<Double>>();
		
			ArrayList<Double> tempDirectionalRoute = new ArrayList<Double>();
//			ArrayList<SegmentProcess> arrayList = (ArrayList<SegmentProcess>) itrArrayList.next();
//			Iterator<SegmentProcess> itr = arrayList.iterator();
			for (int j = 0; j < route.size(); j++) {
				
				ArrayList<gridNode> gridList  = new ArrayList<gridNode>();
				SegmentProcess segmentProcess = route.get(j);
				for(int i=0;i<segmentProcess.directionVector.size();i++)
				{
					tempDirectionalRoute.add(segmentProcess.directionVector.get(i));
				}
				
				
													}
			routeProfitMaps[k].setdirectionVector(tempDirectionalRoute);
				
			
			
													} //end of outer for loop
		
		//System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		/*for (int i = 0; i<routeProfitMaps.length; i++) {
			ArrayList<Double> arrayList = routeProfitMaps[i].getDirectionVector();
			for (Double double1 : arrayList) {
				//System.out.print(double1+" ");
			}
			//System.out.println();
		}
		*/
		ArrayList<RouteProfitMap> processedList = new ArrayList<RouteProfitMap>();
		ArrayList<RouteProfitMap> stillToBeprocessedList = new ArrayList<RouteProfitMap>();
		processedList.add(routeProfitMaps[0]);
		for( int i = 1; i < routeProfitMaps.length ; i++ )  stillToBeprocessedList.add(routeProfitMaps[i]);
		for (int i = 1; i < routeProfitMaps.length ; i++)   // i =2 because last element don't need to be compared
		{
			int index = nextUncorrelatedThing(processedList ,stillToBeprocessedList );
			RouteProfitMap element = stillToBeprocessedList.get(index);
			stillToBeprocessedList.remove(index);
			processedList.add(element);
		}
		
		RouteProfitMap[] newNetProfitMap=new RouteProfitMap[routeProfitMaps.length];
		int s=0;
		for (RouteProfitMap proceMap : processedList) {
			//System.out.println("Correlation : "+proceMap.getNetProfit()+"  "+proceMap.route);
			newNetProfitMap[s]=proceMap;
			s++;
			
		}
		return newNetProfitMap;
		
		
}
	

	//check whether argmax or min should be used
	public static int nextUncorrelatedThing(ArrayList<RouteProfitMap> processedList ,ArrayList<RouteProfitMap> stillToBeprocessedList )
	{
		Double[][] correaltionList = new Double[processedList.size()][stillToBeprocessedList.size()];
		
		for( int i = 0 ; i < processedList.size() ; i++)
		{
			RouteProfitMap outerLoopElement = processedList.get(i);
			for( int j = 0 ; j < stillToBeprocessedList.size() ; j++)
			{
				RouteProfitMap innerLoopElement = stillToBeprocessedList.get(j);
				correaltionList[i][j] = correlation(outerLoopElement.getDirectionVector() , innerLoopElement.directionVector );
			}
		}
		Double[] columnWiseMinimum = new Double[stillToBeprocessedList.size()];
		for( int j = 0 ; j < stillToBeprocessedList.size() ; j++)
		{
		     double minValue = Double.MAX_VALUE;
			 for( int i = 0 ; i < processedList.size() ; i++ )
		     {
		    	  if(correaltionList[i][j] < minValue)
		    		  minValue =  correaltionList[i][j];
		     }
			 columnWiseMinimum[j]=  minValue;
		}
		return argmaxList(columnWiseMinimum);
	}
	
	
	public static int argmaxList(Double[] ub){
		//System.out.println(ub);
		int i=0,argmax=0;
		double maxv = ub[0];
		while (i<ub.length) {
                    if (ub[i] <maxv) {
        	        maxv = ub[i];
        	        argmax = i;        	  
                    }
		    i++;
		}
		return argmax;
	}
	
	public static double mean(ArrayList<Double> vector)
		{
		double m = 0.0;
		for(double i : vector)
			m += i;
		m /= vector.size();
		return m;
		}
	
	public static double correlation(ArrayList<Double> a, ArrayList<Double> b) {
		ArrayList<Double> truncatedA, truncatedB;
		truncatedA = new ArrayList<Double>();
		truncatedB = new ArrayList<Double>();
		if(a.size() <= b.size())
			{
			truncatedA.addAll(a);
			for (int i = 0 ; i < a.size() ; i++)
				truncatedB.add(b.get(i));
			}
		else
			{
			truncatedB.addAll(b);
			for (int i = 0 ; i < b.size() ; i++)
				truncatedA.add(a.get(i));
			}
		
		//compute mean
		double meanA = mean(truncatedA);
		double meanB = mean(truncatedB);
		
		//subtract mean
		for(int i = 0 ; i < truncatedA.size() ; ++i)
			{
			truncatedA.set(i, truncatedA.get(i) - meanA);
			truncatedB.set(i, truncatedB.get(i) - meanB);
			}
		
		ArrayList<Double> aDotB = new ArrayList<Double>();
		
		for(int i = 0 ; i < truncatedA.size() ; ++i)
			{
			aDotB.add(truncatedA.get(i) * truncatedB.get(i));
			truncatedA.set(i, truncatedA.get(i) * truncatedA.get(i));
			truncatedB.set(i, truncatedB.get(i) * truncatedB.get(i));
			}
		
		double sumADotB = 0;
		double sumASquared = 0;
		double sumBSquared = 0;
		
		for(int i = 0 ; i < truncatedA.size() ; ++i)
			{
			sumADotB += aDotB.get(i);
			sumASquared += truncatedA.get(i);
			sumBSquared += truncatedB.get(i);
			}
		
		double corr = sumADotB / Math.sqrt(sumASquared * sumBSquared);
		
		return corr;
	}
	
	

	
/*//	static ArrayList<Node>
	public static RouteProfitMap[] mainRtree(RouteProfitMap[] routeProfitMaps,int k) {
//	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		Map<Long, ArrayList<SegmentProcess>> nodeIdMap = new HashMap<Long, ArrayList<SegmentProcess>>();
		
//		ArrayList<ArrayList<SegmentProcess>> rootToLeaves = new ArrayList<ArrayList<SegmentProcess>>();
		
//		SegmentProcess segmentProcess;
		ArrayList<ArrayList<SegmentProcess>> rootToLeaves = new ArrayList<ArrayList<SegmentProcess>>();
		for (int i = 0; i < routeProfitMaps.length; i++) {
			rootToLeaves.add(routeProfitMaps[i].route);
		}
		
		//computes direction vectors for every grid
		calculateGridDirectionVectors(rootToLeaves);
		
		//computes sequence of direction vectors for every recommended route
		RouteProfitMap[] newNetProfitMap=calculateRouteDirectionVectors(routeProfitMaps, rootToLeaves);
		return newNetProfitMap;
		
	}
	*/
	
	
	

}
