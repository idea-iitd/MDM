package recomendation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class is responsible for processing data regarding segments in a map and converting it to a format
 * that can be used by the recommender.This class will create "LocationData.txt" and "SegmentData.txt"
 * @author stanley
 * @author animesh
 * 
 */
public class DataPreprocessor
	{
	/**
	 * This file contains information about all locations in the area.
	 * <p>
	 * <dl>
	 * <dt>file format:
	 * <dd>(&lt;locationID&gt;,
	 * <dd>LLA(&lt;latitude&gt;,&lt;longitude&gt;,&lt;altitude&gt;))
	 * </dl>
	 * example: (2669246352,LLA(37.7748879,-122.4370674,0.0))
	 */
	public static String INPUT_LOCATION_FILE = "/home/stanley/Documents/DataMining/dataset/Nodes.txt";
	
	/**
	 * This file contains information about all road segments in the area.
	 * <p>
	 * <dl>
	 * <dt>file format:
	 * <dd>Segment(&lt;startLocationID&gt;,
	 * <dd>&lt;endLocationID&gt;,
	 * <dd>[&lt;locations in the segment separated by comma&gt;],
	 * <dd>&lt;segmentLength&gt;,
	 * <dd>&lt;road class&gt;,
	 * <dd>&lt;parent road's id&gt;,
	 * <dd>&lt;true if one-way; false otherwise&gt;)
	 * </dl>
	 * example: Segment(65299217,65299205,[65299217,65299215,65299209,65299205],89.02723724357695,3,27225038,true)
	 */
	public static String INPUT_SEGMENTS_FILE = "/home/stanley/Documents/DataMining/dataset/HighwaySegments.txt";
	
	/**
	 * This file contains only relevant data about locations.
	 * <p>
	 * <dl>
	 * <dt>file format:
	 * <dd>&lt;locationID&gt;,&lt;longitude&gt;,&lt;latitude&gt;
	 * </dl>
	 * example: 2331980048,-122.4003797,37.7900886
	 */
	public static String OUTPUT_LOCATION_FILE = "LocationData.txt";
	
	/**
	 * This file contains only relevant data about all road segments.
	 * <p>
	 * <dl>
	 * <dt>file format:
	 * <dd>&lt;startLocationID&gt;;&lt;endLocationID&gt;;&lt;segment length&gt;;
	 * <dd>&lt;number of locations in the segment&gt;
	 * </dl>
	 * example: 65303664;65303670;565.563873939404;4
	 */
	public static String OUTPUT_SEGMENTS_FILE = "SegmentData.txt";

	/**
	 * This function extracts location information from the input file.
	 * @return HashMap<Long, Location> A mapping of the location id to the location.
	 */
	private static HashMap<Long, Location> extractLocations()
		{
		File file = new File(INPUT_LOCATION_FILE);
		HashMap<Long, Location> locationIdMap = new HashMap<Long, Location>();
				//This structure maps a location's id with the location itself.
		
		try
			{
			BufferedReader in = new BufferedReader(new FileReader(file));
			String string;
			
			while((string = in.readLine()) != null)
				{
				//split the input line on comma
				String[] subStrings = string.split(",");
				//remove preceding "(" from subStrings[0]
				subStrings[0] = subStrings[0].substring(1);
				//remove preceding "LLA(" from subStrings[1]
				subStrings[1] = subStrings[1].substring(4);
				
				long key = Long.parseLong(subStrings[0]);
				Location location = new Location(key, Double.parseDouble(subStrings[1]), Double.parseDouble(subStrings[2]));
				locationIdMap.put(key, location);
				}
			in.close();
			}
			catch(IOException e)
				{
				e.printStackTrace();
				}
		
		return locationIdMap;
		}

	/**
	 * This function reads the contents of the location file and segment file
	 * and writes two files; one containing only relevant information about each
	 * segment, and the other containing information about the relevant locations.
	 */
	public static void processAndOutput()
		{
		HashMap<Long, Location> locationIdMap = extractLocations();
				//This structure maps the location's id with the location itself.
		HashSet<Long> relevantLocations = new HashSet<Long>();
				//This structure keeps track of each segment's start and end location ids
		
		File inputSegmentsFile = new File(INPUT_SEGMENTS_FILE);
		File outputSegmentsFile = new File(OUTPUT_SEGMENTS_FILE);
		File outputLocationFile = new File(OUTPUT_LOCATION_FILE);
		
		try
			{
			BufferedReader in = new BufferedReader(new FileReader(inputSegmentsFile));
			BufferedWriter out = new BufferedWriter(new FileWriter(outputSegmentsFile));

			String string;
			//write relevant information about segments to the file
			while((string = in.readLine()) != null)
				{
				//split the input line on "(" to remove "Segment"
				string = string.split("\\(")[1];
				//string no longer contains "Segment"
				//split the string on "["
				String[] subStrings = string.split("\\[");
				//subStrings[0] now contains the road segment's start and end location ids
				String[] subSubStrings = subStrings[0].split(",");
				//subSubStrings[0] now contains the road segment's start id
				long startId = Long.parseLong(subSubStrings[0]);
				if(!relevantLocations.contains(startId))
					relevantLocations.add(startId);
				//subSubStrings[1] contains the road segment's end id
				long endId = Long.parseLong(subSubStrings[1]);
				if(!relevantLocations.contains(endId))
					relevantLocations.add(endId);
				//subStrings[1] contains the other locations in the segment, length, and additional info
				subSubStrings = subStrings[1].split("\\]");
				//subSubStrings[1] now contains the length of the segment and additional info
				String[] subSubSubStrings = subSubStrings[1].split(",");
				//subSubSubStrings[1] new contains the length of the segment
				double length = Double.parseDouble(subSubSubStrings[1]);
				
				//subSubString[0] contains the other locations in the segment
				subSubSubStrings = subSubStrings[0].split(",");
		/*		ArrayList<Long> internalLocations = new ArrayList<Long>();
				for(String str : subSubSubStrings)
					internalLocations.add(Long.parseLong(str));
		*/		
				int noOfLocations = subSubSubStrings.length;

				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append(Long.toString(startId));
				stringBuffer.append(";");
				stringBuffer.append(Long.toString(endId));
				stringBuffer.append(";");
				stringBuffer.append(Double.toString(length));
				
				stringBuffer.append(";");
				stringBuffer.append(Integer.toString(noOfLocations));
				
		/*		stringBuffer.append(";");
				stringBuffer.append(internalLocations.get(0));
				for(int i = 1 ; i < internalLocations.size() ; ++i)
					{
					stringBuffer.append(",");
					stringBuffer.append(internalLocations.get(i));
					}
		*/		stringBuffer.append("\n");
				
//				System.out.print(stringBuffer.toString());
				
				out.write(stringBuffer.toString());
				}
			out.close();
			
			//write only relevant locations to the file
			out = new BufferedWriter(new FileWriter(outputLocationFile));
			for(Long id : relevantLocations)
				{
				StringBuffer stringBuffer = new StringBuffer();
				Location location = locationIdMap.get(id);
				stringBuffer.append(Long.toString(location.id));
				stringBuffer.append(",");
				stringBuffer.append(Double.toString(location.longitude));
				stringBuffer.append(",");
				stringBuffer.append(Double.toString(location.latitude));
				stringBuffer.append("\n");
				out.write(stringBuffer.toString());
				}
			out.close();
			in.close();
			}
			catch(IOException e)
				{
				e.printStackTrace();
				}
		}
	}