package recomendation;

public class Master
	{
	public static void callDataPreprocessor()
		{
		DataPreprocessor.INPUT_LOCATION_FILE = "/home/animesh/Desktop/datamining/project/dataset/Nodes.txt";
		DataPreprocessor.INPUT_SEGMENTS_FILE = "/home/animesh/Desktop/datamining/project/dataset/HighwaySegments.txt";
		DataPreprocessor.OUTPUT_LOCATION_FILE = "LocationData.txt";
		DataPreprocessor.OUTPUT_SEGMENTS_FILE = "SegmentData.txt";
		DataPreprocessor.processAndOutput();
		}

	public static void callGpsInformationExtractor()
		{
//		GpsInformationExtractor.INPUT_LOCATION_FILE = "LocationData.txt";
//		GpsInformationExtractor.INPUT_SEGMENTS_FILE = "SegmentData.txt";
//		GpsInformationExtractor.GPS_CAB_METADATA = "/home/stanley/Documents/DataMining/cabspottingdata/_cabs.txt";
//		GpsInformationExtractor.OUTPUT_SEGMENTS_FILE = "SegmentDataWithFrequencies.txt";
		GpsInformationExtractor.LIMIT = 4;
		GpsInformationExtractor.WIDTH = 0.0005;
		GpsInformationExtractor.TAXI_SPEED = 50;
		
		}
	
	public static void main(String[] args)
		{
		callDataPreprocessor();
		}
	}