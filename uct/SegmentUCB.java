package uct;

/**
 * 
 * @author nandani
 * This class stores Q Value and saCount value for each neighbouring segment
 */

public class SegmentUCB {

	//public String segmentProcess;
	public double Q;
	public int saCount;
	
	
	public SegmentUCB(double U,int count)
	{
		/*this.segmentProcess=S;*/
		this.Q=U;
		this.saCount =count;
	}
	
	
	
	

}
