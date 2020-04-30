package recomendation;

import java.util.Comparator;

public class SegmentComparator implements Comparator<SegmentDistanceInfo>
	{
	//@Override
	public int compare(SegmentDistanceInfo o1, SegmentDistanceInfo o2)
		{
		if(o1.distanceFromSource < o2.distanceFromSource)
			return -1;
		if(o1.distanceFromSource > o2.distanceFromSource)
			return 1;
		return 0;
		}
	}


