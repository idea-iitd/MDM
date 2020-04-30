package preprocessing;

import java.util.Comparator;

public class SegmentFrequency implements Comparable<SegmentFrequency>{

	/**
	 * @param args
	 *
	 */
	
	public String segmentid;
	public float frequency;
	public String testDataType;
	
	SegmentFrequency() {
		// TODO Auto-generated constructor stub
	}
	
	
	public SegmentFrequency(String id,float f,String td) {
		this.segmentid=id;
		this.frequency=f;
		this.testDataType=td;
		// TODO Auto-generated constructor stub
	}


	@Override
	public int compareTo(SegmentFrequency o) {
		// TODO Auto-generated method stub
		if(testDataType.equals("topxneighbours"))
		{
		if(this.frequency<o.frequency)
			return 1;
		else
			if(this.frequency>o.frequency)
				return -1;
			else
				return 0;
		}
		else if(testDataType.equals("bottomxneighbours"))
		{
			if(this.frequency>o.frequency)
				return 1;
			else
				if(this.frequency<o.frequency)
					return -1;
				else
					return 0;
		}
		else //neither of top and bottom category
			return 1;
	}


	/*@Override
	public int compare(SegmentFrequency o1,SegmentFrequency o2) {
		// TODO Auto-generated method stub
		System.out.println(o1.segmentid+","+o2.segmentid);
		if(o1.frequency<o1.frequency)
			return -1;
		else
			if(this.frequency>o1.frequency)
				return 1;
			else
				return 0;
		return (Math.round(o1.frequency)-Math.round(o2.frequency));
	}
	*/
	
	
	
	
	
}
