package TaxiRecommendation;



import java.io.Serializable;
import java.util.ArrayList;


public class Grid implements Serializable  {

	double longitudeleft;
	double latitudetop;
	double longitudeRight;
	double latitudeBottom;
	ArrayList<Integer> frequencyVector;
	ArrayList<Double> directionVector;
	
	public Grid() {
		// TODO Auto-generated constructor stub
		frequencyVector = new ArrayList<Integer>();
		directionVector = new ArrayList<Double>();
		for(int i = 0 ; i < 8 ; i++)
			{
			frequencyVector.add(0);
			directionVector.add(0.0);
			}
	}

	private int direction(double xStart, double yStart, double xEnd, double yEnd)
		{
		double xDiff = xEnd - xStart;
		double yDiff = yEnd - yStart;
		//xDiff < 0 => right to left
		//yDiff < 0 => bottom to top
		if(xDiff == 0)
			{
			if(yDiff > 0)
				return 3;
			else
				return 7;
			}
		
		if(yDiff == 0)
			{
			if(xDiff > 0)
				return 1;
			else
				return 5;
			}
		
		//xDiff != 0
		if(xDiff > 0)
			{
			//{7, 8, 1, 2}
			if(yDiff > 0)
				{
				//{1, 2}
				if(yDiff < xDiff)
					return 1;
				else
					return 2;
				}
			else
				{
				//{7, 8}
				if(-1 * yDiff > xDiff)
					return 7;
				else
					return 8;
				}
			}
		
		//xDiff < 0
		//{3, 4, 5, 6}
		if(yDiff > 0)
			{
			//{3, 4}
			if(-1 * xDiff < yDiff)
				return 3;
			else
				return 4;
			}
		else
			{
			//{5, 6}
			if(-1 * yDiff < -1 * xDiff)
				return 5;
			else
				return 6;
			}
		}

	/**
	 * This procedure checks if a given segment intersects with this grid and if it does, returns the direction
	 * @param segmentProcess
	 * @return integers 1 to 8 depending of the direction if the segment intersects, 0 otherwise
	 *//*
	public int getDirection(SegmentProcess segmentProcess)
		{
		//check if road segment is completely in the grid
		if(segmentProcess.startLocation.latitude > latitudeBottom && segmentProcess.startLocation.latitude <= latitudetop)
			if(segmentProcess.endLocation.latitude > latitudeBottom && segmentProcess.endLocation.latitude <= latitudetop)
				if(segmentProcess.startLocation.longitude >= longitudeleft && segmentProcess.startLocation.longitude < longitudeRight)
					if(segmentProcess.endLocation.longitude >= longitudeleft && segmentProcess.endLocation.longitude < longitudeRight)
						return direction(segmentProcess.startLocation.longitude, segmentProcess.startLocation.latitude, segmentProcess.endLocation.longitude, segmentProcess.endLocation.latitude);
		
		//check if road segment is parallel to latitude (equator) and intersects
		if(segmentProcess.endLocation.latitude == segmentProcess.startLocation.latitude)
			{
			if(segmentProcess.endLocation.latitude > latitudeBottom && segmentProcess.endLocation.latitude <= latitudetop)
				{
				if(segmentProcess.startLocation.longitude <= longitudeleft && segmentProcess.endLocation.longitude >= longitudeleft)
					return direction(segmentProcess.startLocation.longitude, segmentProcess.startLocation.latitude, segmentProcess.endLocation.longitude, segmentProcess.endLocation.latitude);
				if(segmentProcess.startLocation.longitude > longitudeRight && segmentProcess.endLocation.longitude < longitudeRight)
					return direction(segmentProcess.startLocation.longitude, segmentProcess.startLocation.latitude, segmentProcess.endLocation.longitude, segmentProcess.endLocation.latitude);
				}
			}
		
		//check if road segment is parallel to longitude (prime meridian) and intersects
		if(segmentProcess.endLocation.longitude == segmentProcess.startLocation.longitude)
			{
			if(segmentProcess.startLocation.longitude >= longitudeleft && segmentProcess.endLocation.longitude < longitudeRight)
				{
				if(segmentProcess.startLocation.latitude <= latitudeBottom && segmentProcess.endLocation.latitude > latitudeBottom)
					return direction(segmentProcess.startLocation.longitude, segmentProcess.startLocation.latitude, segmentProcess.endLocation.longitude, segmentProcess.endLocation.latitude);
				if(segmentProcess.startLocation.latitude > latitudetop && segmentProcess.endLocation.latitude <= latitudetop)
					return direction(segmentProcess.startLocation.longitude, segmentProcess.startLocation.latitude, segmentProcess.endLocation.longitude, segmentProcess.endLocation.latitude);
				}
			}
		
		//road segment is no longer parallel to either axis; check for intersection
		double ratio = (segmentProcess.endLocation.latitude - segmentProcess.startLocation.latitude)/(segmentProcess.endLocation.longitude - segmentProcess.startLocation.longitude);
		
		double la1 = segmentProcess.startLocation.latitude + ratio * (longitudeleft - segmentProcess.startLocation.longitude);
		if(la1 > latitudeBottom && la1 <= latitudetop)
			return direction(segmentProcess.startLocation.longitude, segmentProcess.startLocation.latitude, segmentProcess.endLocation.longitude, segmentProcess.endLocation.latitude);
		
		double la2 = segmentProcess.startLocation.latitude + ratio * (longitudeRight - segmentProcess.startLocation.longitude);
		if(la2 > latitudeBottom && la2 <= latitudetop)
			return direction(segmentProcess.startLocation.longitude, segmentProcess.startLocation.latitude, segmentProcess.endLocation.longitude, segmentProcess.endLocation.latitude);
		
		double lo1 = segmentProcess.startLocation.longitude + (latitudetop - segmentProcess.startLocation.latitude) / ratio;
		if(lo1 >= longitudeleft && lo1 < longitudeRight)
			return direction(segmentProcess.startLocation.longitude, segmentProcess.startLocation.latitude, segmentProcess.endLocation.longitude, segmentProcess.endLocation.latitude);
		
		double lo2 = segmentProcess.startLocation.longitude + (latitudeBottom - segmentProcess.startLocation.latitude) / ratio;
		if(lo2 >= longitudeleft && lo2 < longitudeRight)
			return direction(segmentProcess.startLocation.longitude, segmentProcess.startLocation.latitude, segmentProcess.endLocation.longitude, segmentProcess.endLocation.latitude);
		
		return 0;
		}
*/
	
	public int getDirection(SegmentProcess segment)
	{
		/*if(segment.startLocation.latitude>latitudeBottom && segment.startLocation.latitude<=latitudetop)
		{
			if(segment.startLocation.longitude>longitudeleft && segment.startLocation.longitude<=longitudeRight)
			{
				return direction(segment.startLocation.longitude, segment.startLocation.latitude, segment.endLocation.longitude, segment.endLocation.latitude);
			}
		}*/
		
		if(segment.endLocation.latitude>latitudeBottom && segment.endLocation.latitude<=latitudetop)
		{
			if(segment.endLocation.longitude>longitudeleft && segment.endLocation.longitude<=longitudeRight)
			{
				return direction(segment.startLocation.longitude, segment.startLocation.latitude, segment.endLocation.longitude, segment.endLocation.latitude);
			}
				
		}
		
		return 0;
		
		
	}
	
	
	
	
	
	public void calculateDirectionVector()
	{
		int sum = 0;
		for(Integer freq : frequencyVector)
		{
			sum+=freq;
		}
		for (int i = 0; i < frequencyVector.size(); i++) {
			directionVector.set(i, (double)frequencyVector.get(i)/sum);
		}
		
	}
}
