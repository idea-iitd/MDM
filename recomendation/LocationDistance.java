package recomendation;

public class LocationDistance implements Comparable<LocationDistance> {

	/**
	 * @param args
	 */
	
	public Long loc;
	public double distance;
	

	
	public LocationDistance(Long loc, double distance) {
		this.loc = loc;
		this.distance = distance;
	}


	public int compareTo(LocationDistance i)
	{
		
		return (int) (this.distance-i.distance);
		
		
		
		
		
	}



}
