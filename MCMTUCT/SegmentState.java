package MCMTUCT;

import  recomendation.*;

public  class SegmentState extends GameState {

	
	
    public SegmentState( SegmentProcess segment)
    {
    	     this.segment = segment;
    }
	@Override
	public boolean equals(GameState g)
	   {
	      if (g instanceof SegmentState) {
	         return segment.equals(((SegmentState) g).segment);
	      }
	      return false;
	   }
	
	public SegmentState clone()
		{
		return new SegmentState(this.segment);
		}

}
