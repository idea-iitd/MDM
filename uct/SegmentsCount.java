package uct;

/**
 * @author nandani
 * This class stores sCount value for each of the starting location of a segment
 */

public class SegmentsCount {

	public Long segmentStartLocation;
	public int sCount;
	
	
	public SegmentsCount(Long startLocation,int count)
	{
		this.segmentStartLocation=startLocation;
		this.sCount=count;
				
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + sCount;
		result = prime * result + ((segmentStartLocation == null) ? 0 : segmentStartLocation.intValue());
		return result;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SegmentsCount other = (SegmentsCount) obj;
		if (segmentStartLocation == null) {
			if (other.segmentStartLocation != null)
				return false;
		} else if (!segmentStartLocation.equals(other.segmentStartLocation))
			return false;
		return true;
	}
	
}
