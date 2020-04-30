package recomendation;

public class SegmentDistanceInfo
	{
	public SegmentProcess segment;
	public double distanceFromSource;
	
	public SegmentDistanceInfo(SegmentProcess segment, double distanceFromSource)
		{
		this.segment = segment;
		this.distanceFromSource = distanceFromSource;
		}
	


    public boolean equals(SegmentDistanceInfo obj)
		{
	        if(segment.equals(obj.segment))
            	return true;
            else
            	return false;
           
		}

    public int hashCode()
		{
		 return  segment.hashCode();
 		
	    }

}