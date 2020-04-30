package TaxiRecommendation;



import java.util.ArrayList;


public class RouteProfitMap implements Comparable<RouteProfitMap>
{

	ArrayList<SegmentProcess> route;
	double netProfit;
	ArrayList<Double> directionVector;
	

	public RouteProfitMap(ArrayList<SegmentProcess> route , double netProfit)
	{
		// TODO Auto-generated constructor stub
		this.route = route;
		this.netProfit = netProfit;
		this.directionVector = new ArrayList<Double>();

	}
	
	public void setdirectionVector( ArrayList<Double> directionVector )
	{
		this.directionVector = directionVector;
	}
	
	public ArrayList<SegmentProcess> getRoute()
	{
		
		return this.route;
	}
	
	public double getNetProfit()
	{
		return netProfit;
	}
	
	public ArrayList<Double>  getDirectionVector()
	{
		return this.directionVector;
	}
	
	
	public double getLengthOfRoute(ArrayList<SegmentProcess> currentRoute)
	{
		double length=0.0;
		for(int i=0;i<currentRoute.size();i++)
		{
			length=length+currentRoute.get(i).segmentLength;
		}
		
		return length;
		
	}
	
	
	
	

	 @Override
     public boolean equals(Object obj) {
         if (obj == null) {
             return false;
         }
         if (getClass() != obj.getClass()) {
             return false;
         }
         final RouteProfitMap other = (RouteProfitMap) obj;
         if ((this.route == null) ? (other.route != null) : !this.route.equals(other.route)) {
             return false;
         }
         
         return true;
     }

     @Override
     public int hashCode() {
         int hash = 5;
         hash = 97 * hash + (this.route != null ? this.route.hashCode() : 0);
         //hash = (int) (97 * hash + this.netProfit);
         return hash;
     }

     //@Override
     public int compareTo(RouteProfitMap i) {
    	 
    	 if (this.netProfit > i.netProfit) 
    		 return -1;
    	 else if(this.netProfit==i.netProfit)
    		 return 0;
    	 else
    		 return 1;
    	 

     }


}