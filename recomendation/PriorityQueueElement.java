package recomendation;


public class PriorityQueueElement
	{
	public SegmentProcess segment;
	public double distanceFromSource;
	
	PriorityQueueElement(SegmentProcess segment, double distanceFromSource)
		{
		this.segment = segment;
		this.distanceFromSource = distanceFromSource;
		}
	}