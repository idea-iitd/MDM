package recomendation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import recomendation.Main;
import recomendation.RoadNetwork;
import uct.DemoUCT1;

/**
 * This class is responsible for reading GPS traces in a set of files and
 * populating information regarding passengers on each road segment.This method
 * will create "CompleteSegmentData.txt"
 * 
 * @author animesh
 * @author stanley
 *
 */
public class GpsInformationExtractor {
	/**
	 * Is the maximum number of locations in a road segment. If the number of
	 * locations exceeds this limit, we never check any GPS location with this
	 * segment.
	 */
	public static int LIMIT = 4;

	/**
	 * Is the width of the buffer.
	 */
	public static double WIDTH = 0.0005; //original 0.0005

	/**
	 * Is the average speed of a taxi in Kmph.
	 */
	public static double TAXI_SPEED = 25;

	/**
	 * Is the fare charged per metres. TODO check here fare
	 */
	public static double FARE_PER_METER = 0.00285;

	/**
	 * This file contains only relevant data about locations.
	 * <p>
	 * <dl>
	 * <dt>file format:
	 * <dd>&lt;locationID&gt;,&lt;longitude&gt;,&lt;latitude&gt;
	 * </dl>
	 * example: 2331980048,-122.4003797,37.7900886
	 */
	public static String INPUT_LOCATION_FILE = "LocationData.txt";

	/**
	 * This file contains only relevant data about all road segments.
	 * <p>
	 * <dl>
	 * <dt>file format:
	 * <dd>&lt;startLocationID&gt;;&lt;endLocationID&gt;;&lt;segment length in
	 * metres&gt;;
	 * <dd>&lt;number of locations in the segment&gt;
	 * </dl>
	 * example: 65303664;65303670;565.563873939404;4
	 */
	public static String INPUT_SEGMENTS_FILE = "SegmentData.txt";

	/**
	 * This file contains data about all road segments, as required by the
	 * recommender.
	 * <p>
	 * <dl>
	 * <dt>file format:
	 * <dd>&lt;start location ID&gt;,&lt;start location latitude&gt;,&lt;start
	 * location longitude&gt;; &lt;end location ID&gt;,&lt;end location
	 * latitude&gt;,&lt;end location longitude&gt;; &lt;segment length in
	 * metres&gt;;&lt;frequencies of success separated by commas&gt;;
	 * &lt;frequencies of failure separated by commas;&gt;&lt;time required to
	 * cross the segment in seconds&gt;
	 * </dl>
	 * TODO: example:
	 */
	
	
	/**
	 * Object of RoadNetwork class
	 */
	public static RoadNetwork roadNetwork;
	
	public static String trainDataType="Top";
	
	public static double DISTANCE_THRESHOLD = 1000.0;
	
	public static String OUTPUT_SEGMENTS_FILE = "/home/nandani/Documents/sem-3/taxi-project2/Datasets/SFExperimentData/"+trainDataType;  //change here for "Normal" or "Top"

	
	public static String OUTPUT_FOLDER = "/home/nandani/Documents/sem-3/taxi-project2/Datasets/cabspottingTrajData/";
	
	public static String Top_Ten_Percent_Drivers_Info = "Top10PercentDrivers.txt";

	/**
	 * The name of the folder that contains all files related to the GPS traces
	 * of taxis.
	 */
	public static String PARENT_FOLDER = "/home/nandani/Documents/sem-3/taxi-project2/Datasets/cabspottingdata/";

	/**
	 * This file contains the names of all files whose GPS data is to be
	 * processed.
	 * <p>
	 * <dl>
	 * <dt>file format:
	 * <dd>&lt;cab id="&lt;file name&gt;"
	 * updates="&lt;number of lines in the file&gt;"/&gt;
	 * </dl>
	 * example: &lt;cab id="ikdagcy" updates="18109"/&gt;
	 * 
	 * 
	 */
	    //change here for "normal" or "top"
	
	
	public static String CROSS_VALIDATION_FOLDER = "/home/nandani/Documents/sem-3/taxi-project2/Datasets/SFCrossValidationData";
	
	public static int[] datatype={1,10,15,25,50};
	
	public static String dataCategory="Train";  //Test
	
	
	
	
	public static String GPS_CAB_METADATA ="/home/nandani/Documents/sem-3/taxi-project2/Datasets/SF_toplast/"+trainDataType; //PARENT_FOLDER+trainDataType+dataCategory;      //PARENT_FOLDER + "NormalTest";
	

	/**
	 * The set of all segments where a customer can be found.
	 */
	private static HashSet<Segment> relevantSegments = new HashSet<Segment>();

	/**
	 * The set of all segments where a customer cannot be found.
	 */
	private static HashSet<Segment> notUsableSegments = new HashSet<Segment>();
	
	/**
	HashMap containing all the locations
	*/
	private static HashMap<Long, Location> locationMap = new HashMap<Long, Location>();

	public static int noOfTop10PercentDrivers = 0;

	/**
	 * This map stores the average unit time net profit of all the drivers over
	 * the entire tarjectories
	 * 
	 * @key String - Driverid
	 * @Value Double - Average unit time net profit earned
	 * 
	 */

	private static Map<String, Double> driverEarnedProfits = new HashMap<String, Double>();

	/**
	 * Number of trips a driver has done
	 */
	private static ArrayList<Driver> numberOfTripsPerDriver = new ArrayList<Driver>();
	
	//whether to create mutiple folds of the same data
	public static boolean isCrossValidation = false;
	
	//no of folds to be created
	public static int noOfCrossValidation = 1;

	/**
	 * This function populates attributes of all segments in the file
	 * {@code INPUT_SEGMENTS_FILE} and partitions the segments into two disjoint
	 * set of segments; one containing all segments where a customer can be
	 * found, the other containing the set of all segments where a customer
	 * cannot be found. When this function terminates, the segments will have
	 * all data about their start location (latitude and longitude), end
	 * location (latitude and longitude), length, number of times a passenger
	 * was found whenever a taxi passed through, number of times no passenger
	 * was found when a taxi passed through, and the time required to cross the
	 * segment from start to end.
	 */
	private static void prepareAndPartitionSegments() {
		
		try {
			// read and store location data (id, latitude, longitude)
			BufferedReader in = new BufferedReader(new FileReader(
					INPUT_LOCATION_FILE));
			String string;

			while ((string = in.readLine()) != null) {
				String[] subStrings = string.split(",");
				long locationId = Long.parseLong(subStrings[0]);
				double longitude = Double.parseDouble(subStrings[1]);
				double latitude = Double.parseDouble(subStrings[2]);
				Location location = new Location(locationId, latitude,
						longitude);
				locationMap.put(locationId, location);
			}

			in.close();

			// read, prepare, and partition segment data
			in = new BufferedReader(new FileReader(INPUT_SEGMENTS_FILE));

			while ((string = in.readLine()) != null) {
				String[] subStrings = string.split(";");
				int noOfLocations = Integer.parseInt(subStrings[3]);
				long startId = Long.parseLong(subStrings[0]);
				Location startLocation = locationMap.get(startId);
				long endId = Long.parseLong(subStrings[1]);
	
				Location endLocation = locationMap.get(endId);
				double segmentLength = Double.parseDouble(subStrings[2]);
				Segment segment = new Segment(startLocation, endLocation,
						segmentLength, TAXI_SPEED);
				if (noOfLocations > LIMIT)
					notUsableSegments.add(segment);
				else
					relevantSegments.add(segment);
			}

			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This function finds the perpendicular distance of a given location from a
	 * given segment.
	 * 
	 * @param segment
	 *            The road segment from which the distance is to be found
	 * @param location
	 *            The location
	 * @return The perpendicular distance of the given location from the given
	 *         segment.
	 */
	private static double findDistance(Segment segment, Location location) {
		double x1 = segment.startLocation.longitude;
		double y1 = segment.startLocation.latitude;
		double x2 = segment.endLocation.longitude;
		double y2 = segment.endLocation.latitude;
		double x0 = location.longitude;
		double y0 = location.latitude;

		double y2MinusY1 = y2 - y1;
		double x2MinusX1 = x2 - x1;

		double numerator = y2MinusY1 * x0 - x2MinusX1 * y0 + x2 * y1 - y2 * x1;
		double denominator = Math.sqrt(y2MinusY1 * y2MinusY1 + x2MinusX1
				* x2MinusX1);

		return Math.abs(numerator) / denominator;
	}

	/**
	 * This function converts decimal degrees to radians
	 * 
	 * @param deg
	 *            degree
	 */
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/**
	 * This function finds the distance between given two location i.e GPS
	 * traces of a Taxi.
	 * 
	 * @param location
	 *            start location of GPS traces
	 * @param location
	 *            end location of a GPS traces
	 * @return The distance between the given two location of the GPS traces.
	 */
	private static double getDistanceFromLatLonInKm(GpsSignal startLocation,
			GpsSignal endLocation) {
		double lat1 = startLocation.latitude;
		double lon1 = startLocation.longitude;
		double lat2 = endLocation.latitude;
		double lon2 = endLocation.longitude;

		double R = 6371000; // Radius of the earth in metres
		double dLat = deg2rad(lat2 - lat1); // deg2rad below
		double dLon = deg2rad(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = R * c; // Distance in metres
		return d;
	}

	/**
	 * This function finds the angle between two vectors, one being the passed
	 * segment, and the other being the vector formed by taking the previous
	 * location and current location as start and end points respectively.
	 * 
	 * @param segment
	 *            The segment from which a vector is formed using the start and
	 *            end locations
	 * @param previousLocation
	 *            The previous location; is the start of the second vector
	 * @param currentLocation
	 *            The current location; is the end of the second vector
	 * @return The angle between the two vectors, in radians (from {@code 0} to
	 *         {@code Math.pi}).
	 */
	private static double getAngleInRadians(Segment segment,
			Location previousLocation, Location currentLocation) {
		// (x1, y1) and (x2, y2) are the start and end co-ordinates of the
		// segment
		double x1 = segment.startLocation.longitude;
		double y1 = segment.startLocation.latitude;
		double x2 = segment.endLocation.longitude;
		double y2 = segment.endLocation.latitude;

		// (a1, b1) and (a2, b2) are the start and end co-ordinates of the GPS
		double a1 = previousLocation.longitude;
		double b1 = previousLocation.latitude;
		double a2 = currentLocation.longitude;
		double b2 = currentLocation.latitude;

		double x2MinusX1 = x2 - x1;
		double y2MinusY1 = y2 - y1;
		double a2MinusA1 = a2 - a1;
		double b2MinusB1 = b2 - b1;

		double dotProduct = x2MinusX1 * a2MinusA1 + y2MinusY1 * b2MinusB1;
		double rootSumOfSquares_Segment = Math.sqrt(x2MinusX1 * x2MinusX1
				+ y2MinusY1 * y2MinusY1);
		double rootSumOfSquares_GPS = Math.sqrt(a2MinusA1 * a2MinusA1
				+ b2MinusB1 * b2MinusB1);

		double angle = Math.acos(dotProduct
				/ (rootSumOfSquares_Segment * rootSumOfSquares_GPS));

		return angle;
	}

	/**
	 * This function selects the most appropriate segment to place the location
	 * specified by the current signal. Initially, it searches for those
	 * segments in whose buffer this location lies. If there are more than one,
	 * it searches for the closest segment. If more than one segment are
	 * equidistant from this location, it selects the segment whose direction is
	 * most similar to the direction of travel. The direction of travel is
	 * formed using the location informations from the previous GPS signal and
	 * the current GPS signal. Returns {@code null} if the location does not lie
	 * near any segment.
	 * 
	 * @param currentSignal
	 *            The current signal
	 * @param previousSignal
	 *            The previous signal
	 * @return The best segment, {@code null} if there exists none.
	 */
	static Segment selectBestSegment(GpsSignal currentSignal,
			GpsSignal previousSignal, Segment previousSegment) {
		// find the nearest segments
		Location currentLocation = new Location(0L, currentSignal.latitude,
				currentSignal.longitude);
		ArrayList<Segment> candidateSegments = new ArrayList<Segment>();
		// find the list of segments that have the current location in their
		// buffers
		if(previousSegment==null)
			System.out.print("NULL :");
			else
			System.out.print(previousSegment.startLocation.id+" "+previousSegment.endLocation.id+" :");
		for (Segment segment : relevantSegments)
			if (segment.isInBuffer(currentLocation, WIDTH))
			{
				candidateSegments.add(segment);
				
				System.out.print(segment.startLocation.id+" "+segment.endLocation.id+",");
			}
		System.out.println();

		if (candidateSegments.isEmpty() == true) // i.e., the given location
													// does not lie near any
													// segment
			return null;

		if (candidateSegments.size() == 1) // i.e., the given location lies near
											// exactly one segment
			return candidateSegments.get(0);

		// else, the given location lies in the neighbourhood of more than one
		// segment

		// from the list of candidate segments, select the nearest segment
		double minDistance = Double.MAX_VALUE;
		ArrayList<Segment> nearestSegments = new ArrayList<Segment>();
		Location previousLocation;	
		previousLocation = new Location(0L, previousSignal.latitude,
					previousSignal.longitude);
		
		
		for (Segment segment : candidateSegments) {
			double distance = findDistance(segment,currentLocation);
			if (distance < minDistance) {
				nearestSegments.clear();
				nearestSegments.add(segment);
				
				minDistance = distance;
			} else if (distance == minDistance)
			{
				nearestSegments.add(segment);
				
			}
		}	

		// if there is exactly one nearest segment
		if (nearestSegments.size() == 1)
			return nearestSegments.get(0);

		// else, size of nearestSegments is >= 2

		// select that segment whose direction is most similar to the direction
		// of travel
		Segment bestSegment = nearestSegments.get(0);
		
		double minimumAngle = getAngleInRadians(bestSegment, previousLocation,
				currentLocation);

		for (int i = 1; i < nearestSegments.size(); ++i) {
			Segment segment = nearestSegments.get(i);
			double angle = getAngleInRadians(segment, previousLocation,
					currentLocation);
			if (angle < minimumAngle) {
				minimumAngle = angle;
				bestSegment = segment;
			}
		}

		return bestSegment;
	}

	
	public static Long findBestLocation(GpsSignal previousSignal, ArrayList<Long> neighbours, HashSet<Long> seenLocations)
	{
		
	Location previousLocation = new Location(1l,previousSignal.latitude,previousSignal.longitude);
	if(neighbours==null)
	{
		
		double minDistance = Double.MAX_VALUE;
		Long bestLocation = null;
		double distance =0.0;
		for(Entry<Long,Location> entry: locationMap.entrySet())
		{
		Location location = entry.getValue();
		GpsSignal locationSignal = new GpsSignal(location.latitude,location.longitude,1,1l);
		distance = getDistanceFromLatLonInKm(locationSignal,previousSignal);
		if(distance<minDistance)
		{
			bestLocation = entry.getKey();
			minDistance = distance;
			
		}
		}
		if(distance>DISTANCE_THRESHOLD)
			return null;
		else
			return bestLocation;
			
	}
		
	else
		
	{
		
		ArrayList<LocationDistance> newList = new ArrayList<>();
		double distance;
		for(int i=0;i<neighbours.size();i++)
		{
			Location location = locationMap.get(neighbours.get(i));
			GpsSignal locationSignal = new GpsSignal(location.latitude,location.longitude,1,1l);
			distance = getDistanceFromLatLonInKm(locationSignal,previousSignal);
			if(distance<=DISTANCE_THRESHOLD)
				newList.add(new LocationDistance(neighbours.get(i),distance));
				
		}
		
		if(newList.size()==0 || newList.size()==1 && seenLocations.contains(newList.get(0).loc)==true)
			return null;
		
		else if(newList.size()==1)
			return newList.get(0).loc;
		
		else
		{
			Collections.sort(newList);
			for(int i=0;i<newList.size();i++)
			{
				if(seenLocations.contains(newList.get(i).loc)==false)
					return newList.get(i).loc;
				
			}
			
			return null;
			
			
		}
		
			
	}
		
		
		
	}
	
	
	
	
	
	
	


	
	
	
	
	
	
	
	/**
	 * This function extracts the hour of the day from the passed UNIX time
	 * 
	 * @param unixTime
	 *            The time of the day in UNIX format
	 * @return The hour of the day as extracted from the passed UNIX time
	 */
	private static int getHour(long unixTime) {
		Date date = new Date(unixTime * 1000L); // 1000L to convert seconds to
												// milliseconds
		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT-7")); // GMT-7 is the timezone
														// of San Francisco
		String formattedDate = sdf.format(date);
		return Integer.parseInt(formattedDate);
	}

	/**
	 * This function returns the time range as given in the table below:
	 * <table align="center">
	 * <tr align="center">
	 * <th>from</th>
	 * <th>to</th>
	 * <th>return</th>
	 * </tr>
	 * <tr align="center">
	 * <td>0</td>
	 * <td>5</td>
	 * <td>0</td>
	 * </tr>
	 * <tr align="center">
	 * <td>6</td>
	 * <td>11</td>
	 * <td>1</td>
	 * </tr>
	 * <tr align="center">
	 * <td>12</td>
	 * <td>17</td>
	 * <td>2</td>
	 * </tr>
	 * <tr align="center">
	 * <td>18</td>
	 * <td>23</td>
	 * <td>3</td>
	 * </tr>
	 * </table>
	 * 
	 * @param hour
	 *            The hour of the day
	 * @return The time range as in the table above.
	 */
	private static int getTimeRange(int hour) {
		// 0 - 5
		if (hour < 6)
			return 0;
		if (hour < 12)
			return 1;
		if (hour < 18)
			return 2;
		return 3;
	}
	
	
	public static void writeTrajectory(PrintStream out, ArrayList<Long> trajectory)
	
	{
		if(trajectory.size()>2)
		{
		for(int i=0;i<trajectory.size()-1;i++)
		{
			
				out.print(trajectory.get(i)+",");
		
			
		}
		
		
		out.println(trajectory.get(trajectory.size()-1));
		
		}
	}
	
	
	private static void processGpsTrajectories(String fileName, String driverName,PrintStream out,String roadNetworkFile) {
		// fileName = PARENT_FOLDER + "new_" + "egoiwroi" + ".txt";
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			
			String string;
			
			ArrayList<GpsSignal> tour = new ArrayList<GpsSignal>();

			// read contents of the file in 'tour' in reverse
			while ((string = in.readLine()) != null) {
				String[] subStrings = string.split(" ");
				double latitude = Double.parseDouble(subStrings[0]);
				double longitude = Double.parseDouble(subStrings[1]);
				int isOccupied = Integer.parseInt(subStrings[2]);
				long unixTime = Long.parseLong(subStrings[3]);
				GpsSignal gpsSignal = new GpsSignal(latitude, longitude,
						isOccupied, unixTime);
				tour.add(0, gpsSignal);
			}

			in.close();
			
			
			
			recomendation.Main startMain = new Main();
			roadNetwork = startMain.main2(2,roadNetworkFile);
			//new lines added
			Map<Long, ArrayList<SegmentProcess>> segmentIdMap= GpsInformationExtractor.roadNetwork.segmentProcessIdMap;
			

			GpsSignal currentSignal = tour.get(0);
			Long currentLocation=0l;
			ArrayList<Long> neighbours=null;
			ArrayList<Long> trajectory = new ArrayList<Long>();
			HashSet<Long> seenLocations = new HashSet<Long>();
			for (int i = 1; i < tour.size(); ++i) {
				// get previous signal
				GpsSignal previousSignal = currentSignal;
				// get current signal
				currentSignal = tour.get(i);
				Long previousLocation = currentLocation;
				
					// if not occupied in previous signal
					if (previousSignal.isOccupied == 0)
					{
							if(currentSignal.isOccupied!=1)
							{
								currentLocation = findBestLocation(currentSignal,neighbours,seenLocations);
								
								
								if(currentLocation==null)
								{
									trajectory.clear();
									seenLocations.clear();
									continue;
									
								}
								
								
								if(previousLocation!=currentLocation)
								{
									
									seenLocations.add(currentLocation);
									trajectory.add(currentLocation);
									
									
								}
								
							}
							else
							{
								currentLocation = findBestLocation(currentSignal,neighbours,seenLocations);
								if(currentLocation==null)
								{
									trajectory.clear();
									seenLocations.clear();
									continue;
									
								}
								
								if(previousLocation!=currentLocation)
								{
									
									seenLocations.add(currentLocation);
									trajectory.add(currentLocation);
									writeTrajectory(out,trajectory);
									trajectory.clear();
									seenLocations.clear();
									continue;
									
								}	
								
							
						
							}
							ArrayList<SegmentProcess> list = segmentIdMap.get(currentLocation);
							neighbours=new ArrayList<Long>();
							if(list!=null)
							{
							for(int k=0;k<list.size();k++)
								neighbours.add(list.get(k).endLocation.id);
							}
	
	
					}
	
			}
			
			
			
			
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * This function processes the GPS traces given a file and updates
	 * appropriate segment information.
	 * 
	 * @param fileName
	 *            The name of the file containing the GPS traces
	 */
	private static void processGpsTraces(String fileName, String driverName) {
		// fileName = PARENT_FOLDER + "new_" + "egoiwroi" + ".txt";
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			PrintStream out = new PrintStream(new File(PARENT_FOLDER+"trajectory.csv"));
			String string;
			ArrayList<Segment> trajectory = new ArrayList<Segment>();
			ArrayList<GpsSignal> tour = new ArrayList<GpsSignal>();

			// read contents of the file in 'tour' in reverse
			while ((string = in.readLine()) != null) {
				String[] subStrings = string.split(" ");
				double latitude = Double.parseDouble(subStrings[0]);
				double longitude = Double.parseDouble(subStrings[1]);
				int isOccupied = Integer.parseInt(subStrings[2]);
				long unixTime = Long.parseLong(subStrings[3]);
				GpsSignal gpsSignal = new GpsSignal(latitude, longitude,
						isOccupied, unixTime);
				tour.add(0, gpsSignal);
			}

			in.close();
			Driver driver = new Driver(tour, driverName);
			numberOfTripsPerDriver.add(driver);
			// calculateTopTenPercentDrivers(tour,driverName);

			GpsSignal currentSignal = tour.get(0);
			Segment currentSegment = null;

			boolean currentSegmentHasCustomer = false;
			// true if the taxi picks up some customer in the current segment,
			// false if it does not pick up a customer in this segment
			boolean alwaysOccupiedInCurrentSegment = false;
			// true if the taxi passed through the entire segment occupied
			// without dropping
			// & picking passengers in this segment, false if the taxi travels
			// some part of
			// the segment unoccupied

			// used to add distance from where the Taxi driver got the customer
			Segment segmentPickupDistanceTraveled = null;

			for (int i = 1; i < tour.size(); ++i) {
				// get previous signal
				GpsSignal previousSignal = currentSignal;
				// get current signal
				currentSignal = tour.get(i);
				// get previous segment
				Segment previousSegment = currentSegment;
				// find segment where current signal lies
				currentSegment = selectBestSegment(currentSignal,
						previousSignal,previousSegment);
				
				

				if (currentSegment == null)
				{
					trajectory.clear();
					continue;
				}
				//System.out.println(currentSegment.startLocation.id+" "+currentSegment.endLocation.id);

				// get hour
				// OLD CODE: int timeRange =
				// getTimeRange(getHour(currentSignal.unixTime));
				//int timeRange = getHour(currentSignal.unixTime);
			    int timeRange =1;
				// if currentSegment is not the same as the previousSegment
				if (currentSegment != previousSegment) {
					if (previousSegment != null)
						if (alwaysOccupiedInCurrentSegment == false) // i.e.,
																		// traveled
																		// some
																		// part
																		// of
																		// the
																		// previous
																		// segment
																		// unoccupied
							if (currentSegmentHasCustomer == false) // i.e.,
																	// previous
																	// segment
																	// had no
																	// customers
								// update previousSegment.freqFailure[time]
								++previousSegment.freqFailure[timeRange];
					currentSegmentHasCustomer = false; // initialise current
														// segment to have no
														// customers

					// if not occupied in previous signal
					if (previousSignal.isOccupied == 0)
					{
						
						// if not occupied in current signal
						if (currentSignal.isOccupied == 0)
						{	// the current segment can not be always occupied
							alwaysOccupiedInCurrentSegment = false;
							trajectory.add(currentSegment);
						}	
						else // i.e., occupied in current signal (but not in
								// previous signal)
						{
							// the current segment must have a customer
							currentSegmentHasCustomer = true;
							
							//write to file
							trajectory.add(currentSegment);
							//writeTrajectory(out,trajectory);
							trajectory.clear();
							// update currentSegment.freqSuccess[time]
							++currentSegment.freqSuccess[timeRange];
							// initialise taxi to be always occupied in current
							// segment
							alwaysOccupiedInCurrentSegment = true;
							segmentPickupDistanceTraveled = currentSegment;
						}
					}
					else // i.e., occupied in previous signal
					// if not occupied in current signal (but occupied in
					// previous signal)
					if (currentSignal.isOccupied == 0)
					{
						// the current segment can not be always occupied
						alwaysOccupiedInCurrentSegment = false;
						
						trajectory.add(currentSegment);
						
					}
					else {// i.e., occupied in current signal (and previous
							// signal)
						alwaysOccupiedInCurrentSegment = true;
						double distance = getDistanceFromLatLonInKm(
								currentSignal, previousSignal);
						if (segmentPickupDistanceTraveled != null)
							segmentPickupDistanceTraveled.distanceTravelled[timeRange] += distance;
					}
				} else // i.e., current segment is the same as previous segment
				{
					// if not occupied in previous signal
					if (previousSignal.isOccupied == 0) {
						// if not occupied in current signal, then do nothing
						// else, i.e., occupied in current signal (but not in
						// previous signal)

						if (currentSignal.isOccupied == 1) {
							// the current segment must have a customer
							currentSegmentHasCustomer = true;
							// update currentSegment.freqSuccess[time]
							++currentSegment.freqSuccess[timeRange];
							// store the segment to add distance traveled from
							// this segment
							segmentPickupDistanceTraveled = currentSegment;
							trajectory.add(currentSegment);
						   // writeTrajectory(out,trajectory);
						    trajectory.clear();
						}
					} else {
						// i.e., occupied in previous signal
						// if not occupied in current signal (but occupied in
						// previous signal)
						if (currentSignal.isOccupied == 0)
						{
							// the current segment can not be always occupied
							alwaysOccupiedInCurrentSegment = false;
							
							trajectory.add(currentSegment);
						}
						else {
							double distance = getDistanceFromLatLonInKm(
									currentSignal, previousSignal);
							if (segmentPickupDistanceTraveled != null)
								segmentPickupDistanceTraveled.distanceTravelled[timeRange] += distance;
						}

						// else i.e., occupied in current signal (and previous
						// signal), then do nothing
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*public static void calculateTopTenPercentDrivers(ArrayList<GpsSignal> tour,
			String driverName) {
		int count = 1;
		double profit = 0.0;
		int noOfTrips = 0;
		for (int i = 0; i < tour.size(); i = i + count) {
			if (tour.get(i).isOccupied == 1) {
				GpsSignal startLocation = tour.get(i);
				long tripStartTime = tour.get(i).unixTime;
				while ((tour.get(count).isOccupied) != 0
						&& count < tour.size() - 1) {
					count++;

				}
				GpsSignal endLocation = tour.get(count);
				double distance = getDistanceFromLatLonInKm(startLocation,
						endLocation);
				long tripEndTime = tour.get(i).unixTime;
				if ((tripEndTime - tripStartTime) != 0) {
					profit = profit + (distance * FARE_PER_METER)
							/ (Math.abs(tripEndTime - tripStartTime));
					noOfTrips++;
				}
				count++;
			}

			else if (tour.get(i).isOccupied == 0) {
				long tripStartTime = tour.get(i).unixTime;
				while ((tour.get(count).isOccupied) != 1
						&& count < tour.size() - 1) {
					count++;
				}
				GpsSignal startLocation = tour.get(count);
				count++;
				while (tour.get(count).isOccupied != 0
						&& count < tour.size() - 1) {
					count++;
				}
				GpsSignal endLocation = tour.get(count);
				long tripEndTime = tour.get(count).unixTime;
				double distance = getDistanceFromLatLonInKm(startLocation,
						endLocation);
				if ((tripEndTime - tripStartTime) != 0) {
					noOfTrips++;
					profit = profit + (distance * FARE_PER_METER)
							/ (Math.abs(tripEndTime - tripStartTime));
				}
				count++;

			}
			profit = profit / noOfTrips;
		}
		driverEarnedProfits.put(driverName, profit);

	}*/

	/*
	 * public static HashMap sortByValues(Map<String,Double> map) { List list =
	 * new LinkedList(map.entrySet()); // Defined Custom Comparator here
	 * Collections.reverseOrder(list, new Comparator() { public int
	 * compare(Object o1, Object o2) { return ((Comparable) ((Map.Entry)
	 * (o1)).getValue()) .compareTo(((Map.Entry) (o2)).getValue()); } });
	 * 
	 * // Here I am copying the sorted list in HashMap // using LinkedHashMap to
	 * preserve the insertion order HashMap sortedHashMap = new LinkedHashMap();
	 * for (Iterator it = list.iterator(); it.hasNext();) { Map.Entry entry =
	 * (Map.Entry) it.next(); sortedHashMap.put(entry.getKey(),
	 * entry.getValue()); } return sortedHashMap; }
	 */

	/*
	 * public static void writeTopTenDriversToFile() { try { int i=0;
	 * 
	 * @SuppressWarnings("resource") PrintStream out1=new PrintStream(new
	 * File(Top_Ten_Percent_Drivers_Info));
	 * 
	 * @SuppressWarnings("unchecked") Map<String, Double> map =
	 * sortByValues(driverEarnedProfits); for(Entry<String,Double>
	 * entry:map.entrySet()) { if(i<noOfTop10PercentDrivers) {
	 * out1.println(entry.getKey()+","+entry.getValue().toString()); i++;
	 * 
	 * 
	 * } }
	 * 
	 * 
	 * 
	 * 
	 * } catch(IOException e) { e.printStackTrace(); }
	 * 
	 * 
	 * 
	 * 
	 * }
	 */

	/**
	 * This function reads the contents of the file containing the names of
	 * files containing the metadata, and calls the {@code processGpsTraces()}
	 * function to process each of these files.
	 */
	public static void calculateFrequencies(int dataT,int c,PrintStream out, String roadNetworkFile) {
		
	
		try {
			BufferedReader in=null;
			if(isCrossValidation==false)
			{
			 in= new BufferedReader(new FileReader(
					GPS_CAB_METADATA+dataT+".txt"));
			}
			else
			{
				in = new BufferedReader(new FileReader(
						CROSS_VALIDATION_FOLDER+"/"+trainDataType+"/"+dataCategory+dataT+"_"+c+".csv"));
			}
				
			String string;

			// read the contents of the file containing the metadata
			while ((string = in.readLine()) != null) {
				String[] subStrings = string.split(" ");
				noOfTop10PercentDrivers++;
				// subStrings[1] now contains the name of the file containing
				// the GPS traces
				// TODO: remove "id=" from subStrings[1]
				String[] str = subStrings[1].split("=");
				str[1] = str[1].substring(1, str[1].length() - 1);
				String fileName = PARENT_FOLDER + "new_" + str[1] + ".txt";

				// process the GPS traces
				System.out.println("Processing data from " + fileName + "..");
				//UnComment it to get origianl data
				//processGpsTraces(fileName, str[1]);
				processGpsTrajectories(fileName,str[1],out,roadNetworkFile);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This function writes complete details of all segments into a file.
	 */
	public static void writeSegmentsToFile(int dataT,int c) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(
					OUTPUT_SEGMENTS_FILE+dataT+"/CompleteSegmentData"+dataCategory+"_"+c+".csv"));

			HashSet<Segment> allSegments = new HashSet<Segment>();
			allSegments.addAll(notUsableSegments);
			allSegments.addAll(relevantSegments);

			for (Segment segment : allSegments) {
				Location startLocation = segment.startLocation;
				Location endLocation = segment.endLocation;
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append(Long.toString(startLocation.id));
				stringBuffer.append(",");
				stringBuffer.append(Double.toString(startLocation.latitude));
				stringBuffer.append(",");
				stringBuffer.append(Double.toString(startLocation.longitude));
				stringBuffer.append(";");
				stringBuffer.append(Long.toString(endLocation.id));
				stringBuffer.append(",");
				stringBuffer.append(Double.toString(endLocation.latitude));
				stringBuffer.append(",");
				stringBuffer.append(Double.toString(endLocation.longitude));
				stringBuffer.append(";");
				stringBuffer.append(Double.toString(segment.segmentLength));
				stringBuffer.append(";");
				int n = segment.freqSuccess.length;
				int count = 0;
				if (segment.freqSuccess[0] != 0)
					stringBuffer.append(Double
							.toString(segment.distanceTravelled[0]
									/ segment.freqSuccess[0]));
				else
					stringBuffer.append(Double
							.toString(segment.distanceTravelled[0]));
				for (int i = 1; i < n; ++i) {
					stringBuffer.append(",");
					if (segment.freqSuccess[i] != 0)
						stringBuffer.append(Double
								.toString(segment.distanceTravelled[i]
										/ segment.freqSuccess[i]));
					else
						stringBuffer.append(Double
								.toString(segment.distanceTravelled[i]));
				}
				stringBuffer.append(";");
				stringBuffer.append(Double.toString(segment.freqSuccess[0]));
				for (int i = 1; i < n; ++i) {
					stringBuffer.append(",");
					stringBuffer
							.append(Double.toString(segment.freqSuccess[i]));
				}
				stringBuffer.append(";");
				stringBuffer.append(Double.toString(segment.freqFailure[0]));
				for (int i = 1; i < n; ++i) {
					stringBuffer.append(",");
					stringBuffer
							.append(Double.toString(segment.freqFailure[i]));
				}
				stringBuffer.append(";");
				stringBuffer.append(Long.toString(segment.timeToCrossSegment));
				stringBuffer.append("\n");

				out.write(stringBuffer.toString());
			}

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*public static void writeTopTenDriversToFile() {
		Collections.sort(numberOfTripsPerDriver, new Comparator<Driver>() {

			@Override
			public int compare(Driver o1, Driver o2) {
				return o2.numberOfTrips - o1.numberOfTrips;
			}
		});

		int numberOfDrivers = numberOfTripsPerDriver.size();
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(
					Top_Ten_Percent_Drivers_Info));

			Driver pastDriver, currentDriver = null;
			
			for (int i = 0; i < numberOfDrivers; i++) {
				pastDriver = currentDriver;
				currentDriver = numberOfTripsPerDriver.get(i);
				if ( (i==0) || (i < numberOfDrivers / 10)) {
					out.write(currentDriver.name+"\n");
					System.out.println(currentDriver.name);
				}
				else if(currentDriver.numberOfTrips == pastDriver.numberOfTrips) {
					out.write(currentDriver.name+"\n");
					System.out.println(currentDriver.name);
				}
				else {
					break;
				}
			}

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
*/
	public static void main(String[] args) {
		prepareAndPartitionSegments();
		PrintStream out=null;
		try {
			
		
		for(int i=0;i<datatype.length;i++)
		{
		for(int c=1;c<=noOfCrossValidation;c++)
		{
			String INPUT_FILE;
			out = new PrintStream(new File(OUTPUT_FOLDER+trainDataType+dataCategory+datatype[i]+".csv"));
			if(dataCategory.equals("Train"))
			{
				INPUT_FILE = OUTPUT_SEGMENTS_FILE+datatype[i]+"/CompleteSegmentDataTrain.csv";
			}
			else
			{
				INPUT_FILE = OUTPUT_SEGMENTS_FILE+datatype[i]+"/CompleteSegmentDataTest.csv";
			}
			System.out.println("data "+datatype[i]+" Cross-Validation-"+c);
			calculateFrequencies(datatype[i],c,out,INPUT_FILE);
	//writeTopTenDriversToFile();
		// writeTopTenDriversToFile();
		//commented to avoid writing on file
		//writeSegmentsToFile(datatype[i],c);
		}
		}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}